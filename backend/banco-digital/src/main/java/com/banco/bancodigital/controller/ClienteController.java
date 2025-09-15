package com.banco.bancodigital.controller;

import com.banco.bancodigital.dtos.EmprestimoRequest;
import com.banco.bancodigital.model.Emprestimo;
import com.banco.bancodigital.model.Usuario;
import com.banco.bancodigital.service.EmprestimoService;
import com.banco.bancodigital.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {

    private final UsuarioService usuarioService;
    private final EmprestimoService emprestimoService;

    public ClienteController(UsuarioService usuarioService, EmprestimoService emprestimoService) {
        this.usuarioService = usuarioService;
        this.emprestimoService = emprestimoService;
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping("/dados")
    public ResponseEntity<Usuario> getDados() {
        String email = getCurrentUserEmail();
        Usuario usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/saldo")
    public ResponseEntity<java.math.BigDecimal> getSaldo() {
        String email = getCurrentUserEmail();
        Usuario usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return ResponseEntity.ok(usuario.getSaldo());
    }

    @PostMapping("/solicitar-emprestimo")
    public ResponseEntity<Emprestimo> solicitarEmprestimo(@RequestBody EmprestimoRequest request) {
        String email = getCurrentUserEmail();
        Usuario usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Emprestimo novoEmprestimo = emprestimoService.solicitarEmprestimo(usuario.getId(), request);
        return ResponseEntity.ok(novoEmprestimo);
    }

    @GetMapping("/emprestimos")
    public ResponseEntity<List<Emprestimo>> getEmprestimos() {
        String email = getCurrentUserEmail();
        Usuario usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        List<Emprestimo> emprestimos = emprestimoService.findByUsuarioId(usuario.getId());
        return ResponseEntity.ok(emprestimos);
    }
}