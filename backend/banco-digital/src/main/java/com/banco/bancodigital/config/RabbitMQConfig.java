package com.banco.bancodigital.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_EMPRESTIMOS_PENDENTES = "emprestimos_pendentes";
    public static final String QUEUE_EMPRESTIMOS_APROVADOS = "emprestimos_aprovados";

    @Bean
    public Queue emprestimosPendentesQueue() {
        return new Queue(QUEUE_EMPRESTIMOS_PENDENTES, true);
    }

    @Bean
    public Queue emprestimosAprovadosQueue() {
        return new Queue(QUEUE_EMPRESTIMOS_APROVADOS, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}