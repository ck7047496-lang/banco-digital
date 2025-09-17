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
import java.util.UUID;

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
        String uniqueCpf = UUID.randomUUID().toString().substring(0, 11).replaceAll("-", "");
        String uniqueEmail = "joao_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        RegisterRequest request = new RegisterRequest("João Silva", uniqueCpf, uniqueEmail, "Rua A, 123", "senha123", null);
        Usuario usuario = usuarioService.registerNewUser(request);

        assertNotNull(usuario.getId());
        assertEquals("João Silva", usuario.getNome());
        assertEquals(uniqueCpf, usuario.getCpf());
        assertEquals(uniqueEmail, usuario.getEmail());
        assertTrue(passwordEncoder.matches("senha123", usuario.getSenha()));
        assertEquals(PapelUsuario.ROLE_CLIENTE, usuario.getPapel());
        assertEquals(StatusUsuario.PENDENTE, usuario.getStatus());
        assertEquals(BigDecimal.ZERO, usuario.getSaldo());
    }

    @Test
    void testFindByEmail() {
        String uniqueCpf = UUID.randomUUID().toString().substring(0, 11).replaceAll("-", "");
        String uniqueEmail = "maria_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        RegisterRequest request = new RegisterRequest("Maria Souza", uniqueCpf, uniqueEmail, "Rua B, 456", "senha456", null);
        usuarioService.registerNewUser(request);

        Optional<Usuario> foundUser = usuarioService.findByEmail(uniqueEmail);
        assertTrue(foundUser.isPresent());
        assertEquals("Maria Souza", foundUser.get().getNome());
    }

    @Test
    void testFindById() {
        String uniqueCpf = UUID.randomUUID().toString().substring(0, 11).replaceAll("-", "");
        String uniqueEmail = "pedro_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        RegisterRequest request = new RegisterRequest("Pedro Lima", uniqueCpf, uniqueEmail, "Rua C, 789", "senha789", null);
        Usuario savedUser = usuarioService.registerNewUser(request);

        Optional<Usuario> foundUser = usuarioService.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("Pedro Lima", foundUser.get().getNome());
    }

    @Test
    void testUpdateSaldo() {
        String uniqueCpf = UUID.randomUUID().toString().substring(0, 11).replaceAll("-", "");
        String uniqueEmail = "ana_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        RegisterRequest request = new RegisterRequest("Ana Costa", uniqueCpf, uniqueEmail, "Rua D, 101", "senha101", null);
        Usuario savedUser = usuarioService.registerNewUser(request);

        BigDecimal initialSaldo = savedUser.getSaldo();
        BigDecimal valor = new BigDecimal("100.00");
        usuarioService.updateSaldo(savedUser.getId(), valor);

        Usuario updatedUser = usuarioRepository.findById(savedUser.getId()).orElseThrow();
        assertEquals(initialSaldo.add(valor), updatedUser.getSaldo());
    }

    @Test
    void testFindAllPendingClients() {
        usuarioService.registerNewUser(new RegisterRequest("Cliente Pendente 1", UUID.randomUUID().toString().substring(0, 11).replaceAll("-", ""), "pendente1_" + UUID.randomUUID().toString().substring(0, 8) + "@unique.com", "End1", "senha", null));
        usuarioService.registerNewUser(new RegisterRequest("Cliente Ativo 1", UUID.randomUUID().toString().substring(0, 11).replaceAll("-", ""), "ativo1_" + UUID.randomUUID().toString().substring(0, 8) + "@unique.com", "End2", "senha", null)).setStatus(StatusUsuario.ATIVO);
        usuarioService.registerNewUser(new RegisterRequest("Cliente Pendente 2", UUID.randomUUID().toString().substring(0, 11).replaceAll("-", ""), "pendente2_" + UUID.randomUUID().toString().substring(0, 8) + "@unique.com", "End3", "senha", null));

        List<Usuario> pendingClients = usuarioService.findAllPendingClients();
        assertEquals(2, pendingClients.size());
        assertTrue(pendingClients.stream().allMatch(u -> u.getStatus().equals(StatusUsuario.PENDENTE)));
    }

    @Test
    void testFindAllPendingAndActiveClients() {
        usuarioService.registerNewUser(new RegisterRequest("Cliente Pendente 1", UUID.randomUUID().toString().substring(0, 11).replaceAll("-", ""), "pendente1_active_" + UUID.randomUUID().toString().substring(0, 8) + "@unique.com", "End1", "senha", null));
        usuarioService.registerNewUser(new RegisterRequest("Cliente Ativo 1", UUID.randomUUID().toString().substring(0, 11).replaceAll("-", ""), "ativo1_active_" + UUID.randomUUID().toString().substring(0, 8) + "@unique.com", "End2", "senha", null)).setStatus(StatusUsuario.ATIVO);
        usuarioService.registerNewUser(new RegisterRequest("Cliente Recusado 1", UUID.randomUUID().toString().substring(0, 11).replaceAll("-", ""), "recusado1_active_" + UUID.randomUUID().toString().substring(0, 8) + "@unique.com", "End3", "senha", null)).setStatus(StatusUsuario.RECUSADO);

        List<Usuario> clients = usuarioService.findAllPendingAndActiveClients();
        assertEquals(2, clients.size());
        assertTrue(clients.stream().anyMatch(u -> u.getStatus().equals(StatusUsuario.PENDENTE)));
        assertTrue(clients.stream().anyMatch(u -> u.getStatus().equals(StatusUsuario.ATIVO)));
    }

    @Test
    void testApproveClient() {
        String uniqueCpf = UUID.randomUUID().toString().substring(0, 11).replaceAll("-", "");
        String uniqueEmail = "aprovar_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        RegisterRequest request = new RegisterRequest("Cliente para Aprovar", uniqueCpf, uniqueEmail, "End4", "senha", null);
        Usuario savedUser = usuarioService.registerNewUser(request);

        Usuario approvedUser = usuarioService.approveClient(savedUser.getId());
        assertEquals(StatusUsuario.ATIVO, approvedUser.getStatus());
        assertEquals(StatusUsuario.ATIVO, usuarioRepository.findById(savedUser.getId()).orElseThrow().getStatus());
    }

    @Test
    void testReproveClient() {
        String uniqueCpf = UUID.randomUUID().toString().substring(0, 11).replaceAll("-", "");
        String uniqueEmail = "reprovar_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        RegisterRequest request = new RegisterRequest("Cliente para Reprovar", uniqueCpf, uniqueEmail, "End5", "senha", null);
        Usuario savedUser = usuarioService.registerNewUser(request);

        Usuario reprovedUser = usuarioService.reproveClient(savedUser.getId());
        assertEquals(StatusUsuario.RECUSADO, reprovedUser.getStatus());
        assertEquals(StatusUsuario.RECUSADO, usuarioRepository.findById(savedUser.getId()).orElseThrow().getStatus());
    }
}