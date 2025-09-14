package com.banco.bancodigital.rabbitmq;

import com.banco.bancodigital.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.banco.bancodigital.model.Emprestimo;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQSender {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEmprestimoSolicitado(Long emprestimoId) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_EMPRESTIMOS_PENDENTES, emprestimoId);
    }

    public void sendEmprestimoAprovado(Emprestimo emprestimo) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_EMPRESTIMOS_APROVADOS, emprestimo);
    }

    public void sendEmprestimoRejeitado(Emprestimo emprestimo) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_EMPRESTIMOS_APROVADOS, emprestimo);
    }
}