package com.banco.bancodigital.service;

import com.banco.bancodigital.dto.ClienteCadastroDTO;
import com.banco.bancodigital.model.Cliente;
import com.banco.bancodigital.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Cliente cadastrarCliente(ClienteCadastroDTO clienteCadastroDTO) {
        if (clienteRepository.findByCpf(clienteCadastroDTO.getCpf()).isPresent()) {
            throw new RuntimeException("CPF já cadastrado.");
        }
        if (clienteRepository.findByEmail(clienteCadastroDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado.");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(clienteCadastroDTO.getNome());
        cliente.setCpf(clienteCadastroDTO.getCpf());
        cliente.setEmail(clienteCadastroDTO.getEmail());
        cliente.setSenha(passwordEncoder.encode(clienteCadastroDTO.getSenha()));
        cliente.setAprovado(false); // Cliente inicialmente não aprovado
        cliente.setSaldo(0.0); // Saldo inicial zero
        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> buscarClientePorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf);
    }

    public Cliente atualizarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public List<Cliente> buscarTodosClientes() {
        return clienteRepository.findAll();
    }
}