package com.banco.bancodigital.service;

import com.banco.bancodigital.dtos.EmprestimoRequest;
import com.banco.bancodigital.model.Emprestimo;
import com.banco.bancodigital.model.StatusEmprestimo;
import com.banco.bancodigital.model.Usuario;
import com.banco.bancodigital.rabbitmq.RabbitMQSender;
import com.banco.bancodigital.repository.EmprestimoRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final UsuarioService usuarioService;
    private final RabbitMQSender rabbitMQSender;

    public EmprestimoService(EmprestimoRepository emprestimoRepository, UsuarioService usuarioService, RabbitMQSender rabbitMQSender) {
        this.emprestimoRepository = emprestimoRepository;
        this.usuarioService = usuarioService;
        this.rabbitMQSender = rabbitMQSender;
    }

    private static final BigDecimal TAXA_JUROS_POR_PARCELA = new BigDecimal("0.01"); // 1% por parcela
    private static final int MAX_PARCELAS = 24;
    private static final BigDecimal LIMITE_MAXIMO_EMPRESTIMO = new BigDecimal("10000.00");

    @Transactional
    public Emprestimo solicitarEmprestimo(Long usuarioId, EmprestimoRequest request) {
        Usuario usuario = usuarioService.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Verificar se o usuário já possui um empréstimo PENDENTE ou APROVADO
        boolean hasExistingLoan = emprestimoRepository.existsByUsuarioIdAndStatusIn(
                usuarioId, List.of(StatusEmprestimo.PENDENTE, StatusEmprestimo.APROVADO));

        if (hasExistingLoan) {
            throw new RuntimeException("Você já possui um empréstimo PENDENTE ou APROVADO. Aguarde a conclusão para solicitar um novo.");
        }

        if (request.getValor().compareTo(LIMITE_MAXIMO_EMPRESTIMO) > 0) {
            throw new RuntimeException("Valor do empréstimo excede o limite máximo de R$ " + LIMITE_MAXIMO_EMPRESTIMO);
        }
        if (request.getParcelas() > MAX_PARCELAS) {
            throw new RuntimeException("Número de parcelas excede o limite máximo de " + MAX_PARCELAS);
        }

        BigDecimal valorComJuros = request.getValor().multiply(BigDecimal.ONE.add(TAXA_JUROS_POR_PARCELA.multiply(new BigDecimal(request.getParcelas()))));
        BigDecimal valorParcela = valorComJuros.divide(new BigDecimal(request.getParcelas()), 2, BigDecimal.ROUND_HALF_UP);

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setValor(request.getValor());
        emprestimo.setParcelas(request.getParcelas());
        emprestimo.setValorTotal(valorComJuros);
        emprestimo.setValorParcela(valorParcela);
        emprestimo.setDataSolicitacao(OffsetDateTime.now());
        emprestimo.setJuros(TAXA_JUROS_POR_PARCELA); // Definir os juros aqui
        emprestimo.setStatus(StatusEmprestimo.PENDENTE);
        emprestimo.setUsuario(usuario);

        Emprestimo novoEmprestimo = emprestimoRepository.save(emprestimo);

        rabbitMQSender.sendEmprestimoSolicitado(novoEmprestimo.getId());

        return novoEmprestimo;
    }

    public Optional<Emprestimo> findById(Long id) {
        return emprestimoRepository.findById(id);
    }

    public List<Emprestimo> findByUsuarioId(Long usuarioId) {
        return emprestimoRepository.findByUsuarioId(usuarioId);
    }

    public List<Emprestimo> findAllPendingEmprestimos() {
        return emprestimoRepository.findByStatus(StatusEmprestimo.PENDENTE);
    }

    @Transactional
    public Emprestimo approveEmprestimo(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

        if (emprestimo.getStatus() != StatusEmprestimo.PENDENTE) {
            throw new RuntimeException("Empréstimo já foi processado.");
        }

        emprestimo.setStatus(StatusEmprestimo.APROVADO);
        usuarioService.updateSaldo(emprestimo.getUsuario().getId(), emprestimo.getValor());
        Emprestimo emprestimoAprovado = emprestimoRepository.save(emprestimo);
        rabbitMQSender.sendEmprestimoAprovado(emprestimoAprovado);
        return emprestimoAprovado;
    }

    @Transactional
    public Emprestimo reproveEmprestimo(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

        if (emprestimo.getStatus() != StatusEmprestimo.PENDENTE) {
            throw new RuntimeException("Empréstimo já foi processado.");
        }
        emprestimo.setStatus(StatusEmprestimo.NEGADO);
        Emprestimo emprestimoNegado = emprestimoRepository.save(emprestimo);
        rabbitMQSender.sendEmprestimoRejeitado(emprestimoNegado);
        return emprestimoNegado;
    }

    public List<Emprestimo> findByStatus(StatusEmprestimo status) {
        return emprestimoRepository.findByStatus(status);
    }

    public List<Emprestimo> findAll() {
        return emprestimoRepository.findAll();
    }
}