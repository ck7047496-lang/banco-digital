package com.banco.bancodigital.repository;

import com.banco.bancodigital.model.Emprestimo;
import com.banco.bancodigital.model.StatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    List<Emprestimo> findByUsuarioId(Long usuarioId);
    List<Emprestimo> findByStatus(StatusEmprestimo status);
    long countByStatus(StatusEmprestimo status);
    boolean existsByUsuarioIdAndStatusIn(Long usuarioId, List<StatusEmprestimo> statuses);
}