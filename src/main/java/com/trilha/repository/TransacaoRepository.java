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
