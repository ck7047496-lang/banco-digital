package com.banco.bancodigital.rabbitmq;

import com.banco.bancodigital.config.RabbitMQConfig;
import com.banco.bancodigital.model.Emprestimo;
import com.banco.bancodigital.service.EmprestimoService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate; // Importar SimpMessagingTemplate
import org.springframework.stereotype.Component;

@Component
public class RabbitMQListener {

    private final EmprestimoService emprestimoService;
    private final SimpMessagingTemplate messagingTemplate; // Injetar SimpMessagingTemplate

    public RabbitMQListener(EmprestimoService emprestimoService, SimpMessagingTemplate messagingTemplate) {
        this.emprestimoService = emprestimoService;
        this.messagingTemplate = messagingTemplate; // Inicializar
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_EMPRESTIMOS_PENDENTES)
    public void receiveEmprestimoSolicitadoMessage(Long emprestimoId) {
        System.out.println("Recebida solicitação de empréstimo ID: " + emprestimoId);
        // A lógica de processamento será feita no EmprestimoService
        // Notificar o frontend sobre nova solicitação de empréstimo
        messagingTemplate.convertAndSend("/topic/emprestimos/pendentes", emprestimoId);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_EMPRESTIMOS_APROVADOS)
    public void receiveEmprestimoAprovadoRejeitadoMessage(Emprestimo emprestimo) {
        System.out.println("Recebida atualização de empréstimo ID: " + emprestimo.getId());
        // A lógica de notificação ao frontend será feita no EmprestimoService
        // Notificar o frontend sobre atualização de empréstimo
        messagingTemplate.convertAndSend("/topic/emprestimos/atualizacoes", emprestimo);
    }
}