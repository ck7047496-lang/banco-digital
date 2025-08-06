package com.banco.bancodigital.service;

import com.banco.bancodigital.model.Cliente;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    public void sendMessage(Cliente cliente) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonCliente = mapper.writeValueAsString(cliente);
            rabbitTemplate.convertAndSend(queueName, jsonCliente);
            System.out.println("Mensagem enviada para o RabbitMQ: " + jsonCliente);
        } catch (Exception e) {
            System.err.println("Erro ao enviar mensagem para o RabbitMQ: " + e.getMessage());
        }
    }
}