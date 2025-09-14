package com.banco.bancodigital.service;

import com.banco.bancodigital.dtos.RegisterRequest;
import com.banco.bancodigital.model.PapelUsuario;
import com.banco.bancodigital.model.StatusUsuario;
import com.banco.bancodigital.model.Usuario;
import com.banco.bancodigital.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UsuarioServiceIntegrationTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
    }

    @Test
    void testRegisterNewUser() {
        RegisterRequest request = new RegisterRequest("João Silva", "12345678900", "joao@example.com", "Rua A, 123", "senha123", null);
        Usuario usuario = usuarioService.registerNewUser(request);

        assertNotNull(usuario.getId());
        assertEquals("João Silva", usuario.getNome());
        assertEquals("12345678900", usuario.getCpf());
        assertEquals("joao@example.com", usuario.getEmail());
        assertTrue(passwordEncoder.matches("senha123", usuario.getSenha()));
        assertEquals(PapelUsuario.ROLE_CLIENTE, usuario.getPapel());
        assertEquals(StatusUsuario.PENDENTE, usuario.getStatus());
        assertEquals(BigDecimal.ZERO, usuario.getSaldo());
    }

    @Test
    void testFindByEmail() {
        RegisterRequest request = new RegisterRequest("Maria Souza", "09876543211", "maria@example.com", "Rua B, 456", "senha456", null);
        usuarioService.registerNewUser(request);

        Optional<Usuario> foundUser = usuarioService.findByEmail("maria@example.com");
        assertTrue(foundUser.isPresent());
        assertEquals("Maria Souza", foundUser.get().getNome());
    }

    @Test
    void testFindById() {
        RegisterRequest request = new RegisterRequest("Pedro Lima", "11223344556", "pedro@example.com", "Rua C, 789", "senha789", null);
        Usuario savedUser = usuarioService.registerNewUser(request);

        Optional<Usuario> foundUser = usuarioService.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("Pedro Lima", foundUser.get().getNome());
    }

    @Test
    void testUpdateSaldo() {
        RegisterRequest request = new RegisterRequest("Ana Costa", "66554433221", "ana@example.com", "Rua D, 101", "senha101", null);
        Usuario savedUser = usuarioService.registerNewUser(request);

        BigDecimal initialSaldo = savedUser.getSaldo();
        BigDecimal valor = new BigDecimal("100.00");
        usuarioService.updateSaldo(savedUser.getId(), valor);

        Usuario updatedUser = usuarioRepository.findById(savedUser.getId()).orElseThrow();
        assertEquals(initialSaldo.add(valor), updatedUser.getSaldo());
    }

    @Test
    void testFindAllPendingClients() {
        usuarioService.registerNewUser(new RegisterRequest("Cliente Pendente 1", "11111111111", "pendente1@example.com", "End1", "senha", null));
        usuarioService.registerNewUser(new RegisterRequest("Cliente Ativo 1", "22222222222", "ativo1@example.com", "End2", "senha", null)).setStatus(StatusUsuario.ATIVO);
        usuarioService.registerNewUser(new RegisterRequest("Cliente Pendente 2", "33333333333", "pendente2@example.com", "End3", "senha", null));

        List<Usuario> pendingClients = usuarioService.findAllPendingClients();
        assertEquals(2, pendingClients.size());
        assertTrue(pendingClients.stream().allMatch(u -> u.getStatus().equals(StatusUsuario.PENDENTE)));
    }

    @Test
    void testFindAllPendingAndActiveClients() {
        usuarioService.registerNewUser(new RegisterRequest("Cliente Pendente 1", "11111111111", "pendente1@example.com", "End1", "senha", null));
        usuarioService.registerNewUser(new RegisterRequest("Cliente Ativo 1", "22222222222", "ativo1@example.com", "End2", "senha", null)).setStatus(StatusUsuario.ATIVO);
        usuarioService.registerNewUser(new RegisterRequest("Cliente Recusado 1", "33333333333", "recusado1@example.com", "End3", "senha", null)).setStatus(StatusUsuario.RECUSADO);

        List<Usuario> clients = usuarioService.findAllPendingAndActiveClients();
        assertEquals(2, clients.size());
        assertTrue(clients.stream().anyMatch(u -> u.getStatus().equals(StatusUsuario.PENDENTE)));
        assertTrue(clients.stream().anyMatch(u -> u.getStatus().equals(StatusUsuario.ATIVO)));
    }

    @Test
    void testApproveClient() {
        RegisterRequest request = new RegisterRequest("Cliente para Aprovar", "44444444444", "aprovar@example.com", "End4", "senha", null);
        Usuario savedUser = usuarioService.registerNewUser(request);

        Usuario approvedUser = usuarioService.approveClient(savedUser.getId());
        assertEquals(StatusUsuario.ATIVO, approvedUser.getStatus());
        assertEquals(StatusUsuario.ATIVO, usuarioRepository.findById(savedUser.getId()).orElseThrow().getStatus());
    }

    @Test
    void testReproveClient() {
        RegisterRequest request = new RegisterRequest("Cliente para Reprovar", "55555555555", "reprovar@example.com", "End5", "senha", null);
        Usuario savedUser = usuarioService.registerNewUser(request);

        Usuario reprovedUser = usuarioService.reproveClient(savedUser.getId());
        assertEquals(StatusUsuario.RECUSADO, reprovedUser.getStatus());
        assertEquals(StatusUsuario.RECUSADO, usuarioRepository.findById(savedUser.getId()).orElseThrow().getStatus());
    }
}