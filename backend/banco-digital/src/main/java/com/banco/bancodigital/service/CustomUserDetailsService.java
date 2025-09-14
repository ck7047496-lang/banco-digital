package com.banco.bancodigital.service;

import com.banco.bancodigital.model.Usuario;
import com.banco.bancodigital.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(identifier)
                .orElseGet(() -> usuarioRepository.findByCpf(identifier)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o identificador: " + identifier)));
        System.out.println("Senha do usuário carregada do banco de dados para " + identifier + ": " + usuario.getSenha());
        return new User(usuario.getEmail(), usuario.getSenha(), usuario.getAuthorities());
    }
}