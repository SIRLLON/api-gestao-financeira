package com.trilha.service;

import com.trilha.dto.ExternalAccountResponse;
import com.trilha.dto.TransactionRequest;
import com.trilha.dto.TransactionResponse;
import com.trilha.dto.UsuarioResponse;
import com.trilha.exception.CategoriaNotFoundException;
import com.trilha.exception.UsuarioNotFoundException;
import com.trilha.mapper.UsuarioMapper;
import com.trilha.model.Categoria;
import com.trilha.model.Transacao;
import com.trilha.model.Usuario;
import com.trilha.repository.CategoriaRepository;
import com.trilha.repository.TransacaoRepository;
import com.trilha.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransacaoService {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Autowired
    private ExternalAccountService externalAccountService;
    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public TransactionResponse  createTransacao(TransactionRequest transactionRequest) {
        // Buscar usuário e categoria pelo ID
        Usuario usuario = usuarioService.getUserById(transactionRequest.getUsuarioId())
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado"));

        Categoria categoria = categoriaService.getCategoriaById(transactionRequest.getCategoriaId())
                .orElseThrow(() -> new CategoriaNotFoundException("Categoria não encontrada"));

        // Criar a transação com os dados
        Transacao transacao = new Transacao();
        transacao.setDescricao(transactionRequest.getDescricao());
        transacao.setValor(transactionRequest.getValor());
        transacao.setData(transactionRequest.getData());
        transacao.setUsuario(usuario);
        transacao.setCategoria(categoria);

        Transacao savedTransacao = transacaoRepository.save(transacao);
        // Obter dados da conta externa
        ExternalAccountResponse externalAccount = externalAccountService.getAccountData(savedTransacao.getUsuario().getId());

        UsuarioResponse usuarioResponse = UsuarioMapper.toUsuarioResponse(savedTransacao.getUsuario(), externalAccount);
        String formattedDate = savedTransacao.getData().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Double convertedValue = null; // Se não for uma conversão, pode deixar como null
        Double exchangeRate = null; // Se não for uma conversão, pode deixar como null

        return new TransactionResponse(
                savedTransacao.getId(),
                savedTransacao.getDescricao(),
                savedTransacao.getValor(),
                formattedDate, // Data formatada
                convertedValue, // Valor convertido (null para criar transação)
                exchangeRate,   // Taxa de câmbio (null para criar transação)
                usuarioResponse,
                savedTransacao.getCategoria()
        );
    }

    public TransactionResponse convertTransaction(TransactionRequest request) {
        // Busca o usuário e a categoria para validar as IDs
        Usuario usuario = usuarioService.getUserById(request.getUsuarioId())
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado"));

        Categoria categoria = categoriaService.getCategoriaById(request.getCategoriaId())
                .orElseThrow(() -> new CategoriaNotFoundException("Categoria não encontrada"));

        // Definir valores iniciais para a transação
        Double valor = request.getValor();
        String from = request.getFrom(); // Moeda de origem
        String to = request.getTo();   // Moeda de destino

        Double convertedValue = null;  // Valor convertido (se houver)
        Double exchangeRate = null;   // Taxa de câmbio (se houver)

        // Buscar informações da conta externa, como número da conta
        ExternalAccountResponse externalAccount = externalAccountService.getAccountData(usuario.getId());


        if (request.getFrom().equals(request.getTo())) {
            // Se forem iguais, retorna o valor original sem conversão

            return new TransactionResponse(
                    null, // ID não será salvo aqui
                    request.getDescricao(),
                    request.getValor(),
                    request.getData().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    convertedValue,
                    exchangeRate,
                    UsuarioMapper.toUsuarioResponse(usuario, externalAccount),
                    categoria
            );

        }

        // Realiza a conversão de moeda
        ExchangeRateService.ConversionResult conversionResult = exchangeRateService.convertCurrency(
                request.getFrom(),
                request.getTo(),
                request.getValor()
        );

        // Formata a data para o formato desejado
        String formattedDate = request.getData().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Mapeia o usuário para o DTO de resposta
        UsuarioResponse usuarioResponse = UsuarioMapper.toUsuarioResponse(usuario , externalAccount);

        // Atualizar os valores convertidos e a taxa de câmbio
        convertedValue = conversionResult.getConvertedValue();
        exchangeRate = conversionResult.getExchangeRate();  // Taxa de câmbio utilizada na conversão

        // Substituir o valor com o valor convertido
        //valor = Double.valueOf(convertedValue);

        // Criar a transação com os dados
        Transacao transacao = new Transacao();
        transacao.setDescricao(request.getDescricao());
        transacao.setValor(valor);
        transacao.setConvertedValue(convertedValue);  // Armazena o valor convertido
        transacao.setData(request.getData());
        transacao.setUsuario(usuario);
        transacao.setCategoria(categoria);

        // Configurar os novos campos
        transacao.setOrigem(from);
        transacao.setDestino(to);
        transacao.setExchangeRate(exchangeRate);

        // Salvar a transação no banco de dados
        Transacao savedTransacao = transacaoRepository.save(transacao);

        // Atualizar o saldo do usuário com base no valor da transação
        updateUserBalance(usuario, transacao);

        // Cria o objeto `TransactionResponse` com os dados fornecidos
        return new TransactionResponse(
                null, // ID não será salvo aqui
                request.getDescricao(),
                request.getValor(),
                request.getData().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                conversionResult.getConvertedValue(),
                conversionResult.getExchangeRate(),
                UsuarioMapper.toUsuarioResponse(usuario, externalAccount),
                categoria
        );
    }

    private void updateUserBalance(Usuario usuario, Transacao transacao) {
        // Valor convertido para BRL
        Double valorEmBRL;

        if (!"BRL".equals(transacao.getDestino())) {
            // Converte o valor para BRL usando o serviço de conversão de moedas
            ExchangeRateService.ConversionResult conversionResult = exchangeRateService.convertCurrency(
                    transacao.getOrigem(),
                    "BRL",
                    transacao.getValor()
            );
            valorEmBRL = conversionResult.getConvertedValue();
        } else {
            // Já está em BRL, usa o valor diretamente
            valorEmBRL = transacao.getValor();
        }

        // Atualiza o saldo do usuário subtraindo o valor convertido
        Double novoSaldo = usuario.getSaldo() - valorEmBRL;

        // Atualizar o saldo no objeto usuário
        usuario.setSaldo(novoSaldo);

        // Salvar o usuário atualizado no banco de dados
        usuarioRepository.save(usuario);
    }

    public Optional<Transacao> getTransactionById(Long id) {
        return transacaoRepository.findById(id);
    }

    public List<Transacao> getAllTransactions() {
        return transacaoRepository.findAll();
    }

    public List<Transacao> getTransactionsLast7Days(LocalDate date) {
        // Calcula a data de 7 dias atrás
        LocalDate sevenDaysAgo = date.minusDays(7);

        // Chama o repositório para buscar transações no intervalo de datas
        return transacaoRepository.findByDataBetween(sevenDaysAgo, date);
    }

    public void deleteTransactionById(Long id) {
        transacaoRepository.deleteById(id);
    }

    public List<Transacao> getTransactionByUserId(Long usuarioId) {
        return transacaoRepository.findByUsuarioId(usuarioId);
    }

    public Map<Categoria, Double> getExpenseSummaryByCategory(Long usuarioId) {
        List<Transacao> transacoes = getTransactionByUserId(usuarioId);
        return transacoes.stream()
                .collect(Collectors.groupingBy(
                        Transacao::getCategoria,
                        Collectors.summingDouble(Transacao::getValor)
                ));
    }
}
