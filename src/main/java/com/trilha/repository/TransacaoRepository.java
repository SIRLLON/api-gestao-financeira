package com.trilha.repository;

import com.trilha.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    List<Transacao> findByUsuarioId(Long usuarioId);

    List<Transacao> findByDataBetween(LocalDate startDate, LocalDate endDate);
}
//console do h2
//http://localhost:8080/h2-console/login.do?jsessionid=47fc5379e94fee11ae55fbe46f872248
