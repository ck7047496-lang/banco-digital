package com.banco.bancodigital.repository;

import com.banco.bancodigital.model.PapelUsuario;
import com.banco.bancodigital.model.SituacaoCredito;
import com.banco.bancodigital.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByCpf(String cpf);
    Optional<Usuario> findByNome(String nome); // Adicionado para buscar por nome
    List<Usuario> findByStatus(com.banco.bancodigital.model.StatusUsuario status);
    List<Usuario> findByPapelAndSituacaoCredito(PapelUsuario papel, SituacaoCredito situacaoCredito);
    List<Usuario> findByStatusIn(List<com.banco.bancodigital.model.StatusUsuario> statuses);
}