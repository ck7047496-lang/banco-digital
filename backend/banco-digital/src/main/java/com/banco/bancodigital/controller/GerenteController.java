package com.banco.bancodigital.controller;

import com.banco.bancodigital.model.Emprestimo;
import com.banco.bancodigital.model.Usuario;
import com.banco.bancodigital.service.EmprestimoService;
import com.banco.bancodigital.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.banco.bancodigital.model.StatusUsuario;
import com.banco.bancodigital.model.StatusEmprestimo;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class GerenteController {

    private final UsuarioService usuarioService;
    private final EmprestimoService emprestimoService;

    public GerenteController(UsuarioService usuarioService, EmprestimoService emprestimoService) {
        this.usuarioService = usuarioService;
        this.emprestimoService = emprestimoService;
    }

    @GetMapping("/usuarios")
    @PreAuthorize("hasRole('ROLE_GERENTE')")
    public ResponseEntity<List<Usuario>> listarTodosUsuariosParaGerente() {
        List<Usuario> usuarios = usuarioService.findAllPendingAndActiveClients();
        return ResponseEntity.ok(usuarios);
    }

    @PatchMapping("/usuarios/{id}/aprovar")
    @PreAuthorize("hasRole('ROLE_GERENTE')")
    public ResponseEntity<Usuario> aprovarUsuario(@PathVariable Long id) {
        Usuario usuarioAprovado = usuarioService.approveClient(id);
        return ResponseEntity.ok(usuarioAprovado);
    }

    @PatchMapping("/usuarios/{id}/recusar")
    @PreAuthorize("hasRole('ROLE_GERENTE')")
    public ResponseEntity<Usuario> recusarUsuario(@PathVariable Long id) {
        Usuario usuarioRecusado = usuarioService.reproveClient(id);
        return ResponseEntity.ok(usuarioRecusado);
    }

    @GetMapping("/emprestimos")
    @PreAuthorize("hasRole('ROLE_GERENTE')")
    public ResponseEntity<List<Emprestimo>> listarSolicitacoesEmprestimo(@RequestParam(required = false) StatusEmprestimo status) {
        if (status == StatusEmprestimo.PENDENTE) { // Alterado de SOLICITADO para PENDENTE
            List<Emprestimo> emprestimos = emprestimoService.findByStatus(StatusEmprestimo.PENDENTE); // Usar findByStatus
            return ResponseEntity.ok(emprestimos);
        } else if (status != null) {
            // Se um status específico for fornecido (diferente de PENDENTE), filtre por ele
            List<Emprestimo> emprestimos = emprestimoService.findByStatus(status);
            return ResponseEntity.ok(emprestimos);
        } else {
            // Se nenhum status for fornecido, retorne todos os empréstimos
            List<Emprestimo> emprestimos = emprestimoService.findAll(); // Usar findAll
            return ResponseEntity.ok(emprestimos);
        }
    }

    @PatchMapping("/emprestimos/{id}/aprovar")
    @PreAuthorize("hasRole('ROLE_GERENTE')")
    public ResponseEntity<Emprestimo> aprovarEmprestimo(@PathVariable Long id) {
        Emprestimo emprestimoAprovado = emprestimoService.approveEmprestimo(id);
        return ResponseEntity.ok(emprestimoAprovado);
    }

    @PatchMapping("/emprestimos/{id}/reprovar")
    @PreAuthorize("hasRole('ROLE_GERENTE')")
    public ResponseEntity<Emprestimo> reprovarEmprestimo(@PathVariable Long id) {
        Emprestimo emprestimoReprovado = emprestimoService.reproveEmprestimo(id);
        return ResponseEntity.ok(emprestimoReprovado);
    }
}