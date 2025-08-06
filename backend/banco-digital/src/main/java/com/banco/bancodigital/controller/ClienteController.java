package com.banco.bancodigital.controller;

import com.banco.bancodigital.dto.ClienteCadastroDTO;
import com.banco.bancodigital.model.Cliente;
import com.banco.bancodigital.service.ClienteService;
import com.banco.bancodigital.service.RabbitMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    @PostMapping("/cadastro")
    public ResponseEntity<Cliente> cadastrarCliente(@RequestBody ClienteCadastroDTO clienteCadastroDTO) {
        Cliente novoCliente = clienteService.cadastrarCliente(clienteCadastroDTO);
        rabbitMQProducer.sendMessage(novoCliente); // Envia para a fila para análise do gerente
        return ResponseEntity.ok(novoCliente);
    }
}