package com.banco.bancodigital.service;

import com.banco.bancodigital.dtos.RegisterRequest;
import com.banco.bancodigital.model.PapelUsuario;
import com.banco.bancodigital.model.StatusUsuario;
import com.banco.bancodigital.model.Usuario;
import com.banco.bancodigital.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registerNewUser(RegisterRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setCpf(request.getCpf());
        usuario.setEmail(request.getEmail());
        usuario.setEndereco(request.getEndereco());
        System.out.println("Senha original antes da criptografia: " + request.getSenha());
        String encodedPassword = passwordEncoder.encode(request.getSenha());
        System.out.println("Senha criptografada: " + encodedPassword);
        usuario.setSenha(encodedPassword);
        usuario.setPapel(request.getPapel() != null ? request.getPapel() : PapelUsuario.ROLE_CLIENTE);
        usuario.setStatus(StatusUsuario.PENDENTE);
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuario> findByCpf(String cpf) {
        return usuarioRepository.findByCpf(cpf);
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public void updateSaldo(Long usuarioId, java.math.BigDecimal valor) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + usuarioId));
        usuario.setSaldo(usuario.getSaldo().add(valor));
        usuarioRepository.save(usuario);
    }

    public List<Usuario> findAllPendingClients() {
        return usuarioRepository.findByStatus(StatusUsuario.PENDENTE);
    }

    public List<Usuario> findAllPendingAndActiveClients() {
        List<StatusUsuario> statuses = List.of(StatusUsuario.PENDENTE, StatusUsuario.ATIVO);
        return usuarioRepository.findByStatusIn(statuses);
    }

    public Usuario approveClient(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + id));
        usuario.setStatus(StatusUsuario.ATIVO);
        return usuarioRepository.save(usuario);
    }

    public Usuario reproveClient(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + id));
        usuario.setStatus(StatusUsuario.RECUSADO);
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }
}