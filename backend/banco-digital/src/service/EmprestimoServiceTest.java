package com.banco.bancodigital.service;

import com.banco.bancodigital.model.Emprestimo;
import com.banco.bancodigital.model.Usuario;
import com.banco.bancodigital.repository.EmprestimoRepository;
import com.banco.bancodigital.repository.UsuarioRepository;
import com.banco.bancodigital.service.EmprestimoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmprestimoServiceTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private EmprestimoService emprestimoService;

    private Usuario usuarioAtivo;
    private Emprestimo emprestimoPendente;

    @BeforeEach
    void setUp() {
        usuarioAtivo = new Usuario();
        usuarioAtivo.setId(1L);
        usuarioAtivo.setNome("Cliente Ativo");
        usuarioAtivo.setCpf("11122233344");
        usuarioAtivo.setEmail("ativo@example.com");
        usuarioAtivo.setPapel(Usuario.PapelUsuario.ROLE_CLIENTE);
        usuarioAtivo.setStatus(Usuario.StatusUsuario.ATIVO);
        usuarioAtivo.setLimiteCredito(5000.0);
        usuarioAtivo.setSituacaoCredito(Usuario.SituacaoCredito.APROVADO);
        usuarioAtivo.setSaldo(1000.0);

        emprestimoPendente = new Emprestimo();
        emprestimoPendente.setId(1L);
        emprestimoPendente.setUsuario(usuarioAtivo);
        emprestimoPendente.setValor(1000.0);
        emprestimoPendente.setParcelas(10);
        emprestimoPendente.setJuros(0.01);
        emprestimoPendente.setStatus(Emprestimo.StatusEmprestimo.PENDENTE);
    }

    @Test
    void testSolicitarEmprestimo_Success() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioAtivo));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimoPendente);

        Emprestimo result = emprestimoService.solicitarEmprestimo(1L, 1000.0, 10, "Viagem");

        assertNotNull(result);
        assertEquals(Emprestimo.StatusEmprestimo.PENDENTE, result.getStatus());
        assertEquals(1000.0, result.getValor());
        assertEquals(10, result.getParcelas());
        assertNotNull(result.getValorTotal());
        assertNotNull(result.getValorParcela());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(emprestimoRepository, times(1)).save(any(Emprestimo.class));
    }

    @Test
    void testSolicitarEmprestimo_UsuarioNotFound() {
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emprestimoService.solicitarEmprestimo(99L, 1000.0, 10, "Viagem");
        });
        assertEquals("Usuário não encontrado.", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(99L);
        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }

    @Test
    void testSolicitarEmprestimo_UsuarioNotApproved() {
        usuarioAtivo.setStatus(Usuario.StatusUsuario.PENDENTE);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioAtivo));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emprestimoService.solicitarEmprestimo(1L, 1000.0, 10, "Viagem");
        });
        assertEquals("Usuário não aprovado para solicitar empréstimos.", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }

    @Test
    void testSolicitarEmprestimo_ExceedsLimit() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioAtivo));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emprestimoService.solicitarEmprestimo(1L, 15000.0, 10, "Viagem");
        });
        assertEquals("Valor do empréstimo excede o limite máximo permitido.", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }

    @Test
    void testFindAllEmprestimos() {
        List<Emprestimo> emprestimos = Arrays.asList(emprestimoPendente);
        when(emprestimoRepository.findAll()).thenReturn(emprestimos);

        List<Emprestimo> result = emprestimoService.findAllEmprestimos();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(emprestimoRepository, times(1)).findAll();
    }

    @Test
    void testFindEmprestimosByUsuarioId() {
        List<Emprestimo> emprestimos = Arrays.asList(emprestimoPendente);
        when(emprestimoRepository.findByUsuarioId(1L)).thenReturn(emprestimos);

        List<Emprestimo> result = emprestimoService.findEmprestimosByUsuarioId(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(emprestimoRepository, times(1)).findByUsuarioId(1L);
    }

    @Test
    void testApproveEmprestimo() {
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimoPendente));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimoPendente);

        Emprestimo approvedEmprestimo = emprestimoService.approveEmprestimo(1L);

        assertNotNull(approvedEmprestimo);
        assertEquals(Emprestimo.StatusEmprestimo.APROVADO, approvedEmprestimo.getStatus());
        verify(emprestimoRepository, times(1)).findById(1L);
        verify(emprestimoRepository, times(1)).save(emprestimoPendente);
    }

    @Test
    void testReproveEmprestimo() {
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimoPendente));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimoPendente);

        Emprestimo reprovedEmprestimo = emprestimoService.reproveEmprestimo(1L);

        assertNotNull(reprovedEmprestimo);
        assertEquals(Emprestimo.StatusEmprestimo.NEGADO, reprovedEmprestimo.getStatus());
        verify(emprestimoRepository, times(1)).findById(1L);
        verify(emprestimoRepository, times(1)).save(emprestimoPendente);
    }
}