package com.banco.bancodigital.service;

import com.banco.bancodigital.model.Cliente;
import com.banco.bancodigital.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        Cliente cliente = clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new UsernameNotFoundException("Cliente não encontrado com CPF: " + cpf));

        String finalRole;
        if ("999.999.999-99".equals(cpf)) { // Exemplo de CPF para gerente
            finalRole = "ROLE_GERENTE";
        } else {
            finalRole = cliente.isAprovado() ? "ROLE_CLIENTE" : "ROLE_PENDENTE";
        }

        return new User(cliente.getCpf(), cliente.getSenha(), Collections.singletonList(() -> finalRole));
    }
}