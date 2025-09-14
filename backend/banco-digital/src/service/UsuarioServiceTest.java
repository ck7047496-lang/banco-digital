package com.banco.bancodigital.service;

import com.banco.bancodigital.model.Usuario;
import com.banco.bancodigital.repository.UsuarioRepository;
import com.banco.bancodigital.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Teste Usuario");
        usuario.setEmail("teste@example.com");
        usuario.setSenha("senha123");
        usuario.setCpf("12345678900");
        usuario.setPapel(Usuario.PapelUsuario.ROLE_CLIENTE);
        usuario.setStatus(Usuario.StatusUsuario.PENDENTE);
        usuario.setSaldo(0.0);
        usuario.setLimiteCredito(0.0);
        usuario.setSituacaoCredito(Usuario.SituacaoCredito.PENDENTE);
    }

    @Test
    void testSaveUsuario() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario savedUsuario = usuarioService.saveUsuario(usuario);

        assertNotNull(savedUsuario);
        assertEquals("encodedPassword", savedUsuario.getSenha());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void testFindById() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<Usuario> foundUsuario = usuarioService.findById(1L);

        assertTrue(foundUsuario.isPresent());
        assertEquals(usuario.getEmail(), foundUsuario.get().getEmail());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testApproveClient() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario approvedUsuario = usuarioService.approveClient(1L);

        assertNotNull(approvedUsuario);
        assertEquals(Usuario.StatusUsuario.ATIVO, approvedUsuario.getStatus());
        assertEquals(1000.0, approvedUsuario.getLimiteCredito());
        assertEquals(Usuario.SituacaoCredito.APROVADO, approvedUsuario.getSituacaoCredito());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void testReproveClient() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario reprovedUsuario = usuarioService.reproveClient(1L);

        assertNotNull(reprovedUsuario);
        assertEquals(Usuario.StatusUsuario.REPROVADO, reprovedUsuario.getStatus());
        assertEquals(0.0, reprovedUsuario.getLimiteCredito());
        assertEquals(Usuario.SituacaoCredito.NEGADO, reprovedUsuario.getSituacaoCredito());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void testUpdateSaldo() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario updatedUsuario = usuarioService.updateSaldo(1L, 500.0);

        assertNotNull(updatedUsuario);
        assertEquals(500.0, updatedUsuario.getSaldo());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(usuario);
    }
}