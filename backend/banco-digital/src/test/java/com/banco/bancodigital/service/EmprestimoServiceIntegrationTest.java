package com.banco.bancodigital.service;

import com.banco.bancodigital.dtos.EmprestimoRequest;
import com.banco.bancodigital.dtos.RegisterRequest;
import com.banco.bancodigital.model.Emprestimo;
import com.banco.bancodigital.model.PapelUsuario;
import com.banco.bancodigital.model.StatusEmprestimo;
import com.banco.bancodigital.model.StatusUsuario;
import com.banco.bancodigital.model.Usuario;
import com.banco.bancodigital.rabbitmq.RabbitMQSender;
import com.banco.bancodigital.repository.EmprestimoRepository;
import com.banco.bancodigital.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class EmprestimoServiceIntegrationTest {

    @Autowired
    private EmprestimoService emprestimoService;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @MockBean
    private RabbitMQSender rabbitMQSender;

    private Usuario testUser;

    @BeforeEach
    void setUp() {
        emprestimoRepository.deleteAll();
        usuarioRepository.deleteAll();

        testUser = new Usuario();
        testUser.setNome("Test User");
        testUser.setCpf("11122233344");
        testUser.setEmail("test@example.com");
        testUser.setEndereco("Test Address");
        testUser.setSenha("encodedPassword");
        testUser.setPapel(PapelUsuario.ROLE_CLIENTE);
        testUser.setStatus(StatusUsuario.ATIVO);
        testUser.setSaldo(BigDecimal.ZERO);
        testUser = usuarioRepository.save(testUser);
    }

    @Test
    void testSolicitarEmprestimo() {
        EmprestimoRequest request = new EmprestimoRequest(new BigDecimal("1000.00"), 12);
        Emprestimo emprestimo = emprestimoService.solicitarEmprestimo(testUser.getId(), request);

        assertNotNull(emprestimo.getId());
        assertEquals(new BigDecimal("1000.00"), emprestimo.getValor());
        assertEquals(12, emprestimo.getParcelas());
        assertEquals(StatusEmprestimo.PENDENTE, emprestimo.getStatus());
        assertNotNull(emprestimo.getDataSolicitacao());
        assertEquals(testUser.getId(), emprestimo.getUsuario().getId());

        // Verify calculated values
        BigDecimal expectedJuros = new BigDecimal("0.01");
        BigDecimal valorEmprestimo = new BigDecimal("1000.00");
        BigDecimal numParcelas = new BigDecimal(12);

        BigDecimal expectedValorTotal = valorEmprestimo.multiply(BigDecimal.ONE.add(expectedJuros.multiply(numParcelas)));
        BigDecimal expectedValorParcela = expectedValorTotal.divide(numParcelas, 2, BigDecimal.ROUND_HALF_UP); // Arredonda para 2 casas decimais

        assertEquals(expectedValorTotal.setScale(2, BigDecimal.ROUND_HALF_UP), emprestimo.getValorTotal().setScale(2, BigDecimal.ROUND_HALF_UP));
        assertEquals(expectedValorParcela, emprestimo.getValorParcela());

        verify(rabbitMQSender, times(1)).sendEmprestimoSolicitado(emprestimo.getId());
    }

    @Test
    void testSolicitarEmprestimo_ValorExcedeLimite() {
        EmprestimoRequest request = new EmprestimoRequest(new BigDecimal("15000.00"), 12);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emprestimoService.solicitarEmprestimo(testUser.getId(), request);
        });
        assertTrue(exception.getMessage().contains("Valor do empréstimo excede o limite máximo"));
    }

    @Test
    void testSolicitarEmprestimo_ParcelasExcedeLimite() {
        EmprestimoRequest request = new EmprestimoRequest(new BigDecimal("1000.00"), 36);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emprestimoService.solicitarEmprestimo(testUser.getId(), request);
        });
        assertTrue(exception.getMessage().contains("Número de parcelas excede o limite máximo"));
    }

    @Test
    void testFindById() {
        EmprestimoRequest request = new EmprestimoRequest(new BigDecimal("500.00"), 6);
        Emprestimo savedEmprestimo = emprestimoService.solicitarEmprestimo(testUser.getId(), request);

        Optional<Emprestimo> foundEmprestimo = emprestimoService.findById(savedEmprestimo.getId());
        assertTrue(foundEmprestimo.isPresent());
        assertEquals(savedEmprestimo.getId(), foundEmprestimo.get().getId());
    }

    @Test
    void testFindByUsuarioId() {
        // Criar um segundo usuário para a segunda solicitação de empréstimo
        Usuario secondUser = new Usuario();
        secondUser.setNome("Second Test User");
        secondUser.setCpf("55566677788");
        secondUser.setEmail("second.test@example.com");
        secondUser.setEndereco("Second Test Address");
        secondUser.setSenha("encodedPassword");
        secondUser.setPapel(PapelUsuario.ROLE_CLIENTE);
        secondUser.setStatus(StatusUsuario.ATIVO);
        secondUser.setSaldo(BigDecimal.ZERO);
        final Usuario savedSecondUser = usuarioRepository.save(secondUser);

        EmprestimoRequest request1 = new EmprestimoRequest(new BigDecimal("500.00"), 6);
        emprestimoService.solicitarEmprestimo(testUser.getId(), request1);

        EmprestimoRequest request2 = new EmprestimoRequest(new BigDecimal("2000.00"), 24);
        emprestimoService.solicitarEmprestimo(savedSecondUser.getId(), request2); // Usar o segundo usuário

        List<Emprestimo> emprestimosTestUser = emprestimoService.findByUsuarioId(testUser.getId());
        assertEquals(1, emprestimosTestUser.size());
        assertTrue(emprestimosTestUser.stream().allMatch(e -> e.getUsuario().getId().equals(testUser.getId())));

        List<Emprestimo> emprestimosSecondUser = emprestimoService.findByUsuarioId(savedSecondUser.getId());
        assertEquals(1, emprestimosSecondUser.size());
        assertTrue(emprestimosSecondUser.stream().allMatch(e -> e.getUsuario().getId().equals(savedSecondUser.getId())));
    }

    @Test
    void testFindAllSolicitedEmprestimos() {
        // Criar um terceiro usuário para a segunda solicitação de empréstimo
        Usuario thirdUser = new Usuario();
        thirdUser.setNome("Third Test User");
        thirdUser.setCpf("99988877766");
        thirdUser.setEmail("third.test@example.com");
        thirdUser.setEndereco("Third Test Address");
        thirdUser.setSenha("encodedPassword");
        thirdUser.setPapel(PapelUsuario.ROLE_CLIENTE);
        thirdUser.setStatus(StatusUsuario.ATIVO);
        thirdUser.setSaldo(BigDecimal.ZERO);
        final Usuario savedThirdUser = usuarioRepository.save(thirdUser);

        EmprestimoRequest request1 = new EmprestimoRequest(new BigDecimal("500.00"), 6);
        emprestimoService.solicitarEmprestimo(testUser.getId(), request1);

        EmprestimoRequest request2 = new EmprestimoRequest(new BigDecimal("2000.00"), 24);
        Emprestimo approvedEmprestimo = emprestimoService.solicitarEmprestimo(savedThirdUser.getId(), request2); // Usar o terceiro usuário
        emprestimoService.approveEmprestimo(approvedEmprestimo.getId());

        List<Emprestimo> solicitedEmprestimos = emprestimoService.findAllPendingEmprestimos();
        assertEquals(1, solicitedEmprestimos.size());
        assertEquals(StatusEmprestimo.PENDENTE, solicitedEmprestimos.get(0).getStatus());
        assertEquals(testUser.getId(), solicitedEmprestimos.get(0).getUsuario().getId());
    }

    @Test
    void testApproveEmprestimo() {
        EmprestimoRequest request = new EmprestimoRequest(new BigDecimal("1000.00"), 12);
        Emprestimo savedEmprestimo = emprestimoService.solicitarEmprestimo(testUser.getId(), request);

        Emprestimo approvedEmprestimo = emprestimoService.approveEmprestimo(savedEmprestimo.getId());
        assertEquals(StatusEmprestimo.APROVADO, approvedEmprestimo.getStatus());
        assertEquals(StatusEmprestimo.APROVADO, emprestimoRepository.findById(savedEmprestimo.getId()).orElseThrow().getStatus());

        verify(rabbitMQSender, times(1)).sendEmprestimoAprovado(any(Emprestimo.class));
    }

    @Test
    void testApproveEmprestimo_AlreadyProcessed() {
        EmprestimoRequest request = new EmprestimoRequest(new BigDecimal("1000.00"), 12);
        Emprestimo savedEmprestimo = emprestimoService.solicitarEmprestimo(testUser.getId(), request);
        emprestimoService.approveEmprestimo(savedEmprestimo.getId()); // First approval

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emprestimoService.approveEmprestimo(savedEmprestimo.getId()); // Second approval
        });
        assertTrue(exception.getMessage().contains("Empréstimo já foi processado."));
    }

    @Test
    void testReproveEmprestimo() {
        EmprestimoRequest request = new EmprestimoRequest(new BigDecimal("1000.00"), 12);
        Emprestimo savedEmprestimo = emprestimoService.solicitarEmprestimo(testUser.getId(), request);

        Emprestimo reprovedEmprestimo = emprestimoService.reproveEmprestimo(savedEmprestimo.getId());
        assertEquals(StatusEmprestimo.NEGADO, reprovedEmprestimo.getStatus());
        assertEquals(StatusEmprestimo.NEGADO, emprestimoRepository.findById(savedEmprestimo.getId()).orElseThrow().getStatus());

        verify(rabbitMQSender, times(1)).sendEmprestimoRejeitado(any(Emprestimo.class));
    }

    @Test
    void testReproveEmprestimo_AlreadyProcessed() {
        EmprestimoRequest request = new EmprestimoRequest(new BigDecimal("1000.00"), 12);
        Emprestimo savedEmprestimo = emprestimoService.solicitarEmprestimo(testUser.getId(), request);
        emprestimoService.reproveEmprestimo(savedEmprestimo.getId()); // First reproval

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emprestimoService.reproveEmprestimo(savedEmprestimo.getId()); // Second reproval
        });
        assertTrue(exception.getMessage().contains("Empréstimo já foi processado."));
    }
}