package com.banco.bancodigital.service;

import com.banco.bancodigital.model.Cliente;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    @Autowired
    private ClienteService clienteService;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveMessage(String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Cliente cliente = mapper.readValue(message, Cliente.class);
            System.out.println("Mensagem recebida do RabbitMQ: " + cliente.getCpf());

            // Lógica para o gerente aprovar o cadastro (simulada aqui)
            // Na aplicação real, isso seria uma tela para o gerente
            // Por enquanto, vamos apenas "aprovar" automaticamente para fins de demonstração
            cliente.setAprovado(true);
            clienteService.atualizarCliente(cliente);
            System.out.println("Cliente " + cliente.getCpf() + " aprovado automaticamente.");

        } catch (Exception e) {
            System.err.println("Erro ao processar mensagem do RabbitMQ: " + e.getMessage());
        }
    }
}