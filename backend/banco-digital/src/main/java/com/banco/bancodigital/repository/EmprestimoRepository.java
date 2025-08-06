package com.banco.bancodigital.repository;

import com.banco.bancodigital.model.Emprestimo;
import com.banco.bancodigital.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    List<Emprestimo> findByCliente(Cliente cliente);
    Optional<Emprestimo> findByClienteAndAprovadoFalse(Cliente cliente);
}