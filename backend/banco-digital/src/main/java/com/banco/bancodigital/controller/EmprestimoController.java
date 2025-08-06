package com.banco.bancodigital.controller;

import com.banco.bancodigital.dto.EmprestimoSolicitacaoDTO;
import com.banco.bancodigital.model.Cliente;
import com.banco.bancodigital.model.Emprestimo;
import com.banco.bancodigital.service.ClienteService;
import com.banco.bancodigital.service.EmprestimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cliente/emprestimos")
public class EmprestimoController {

    @Autowired
    private EmprestimoService emprestimoService;

    @Autowired
    private ClienteService clienteService;

    private Cliente getAuthenticatedCliente() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String cpf = authentication.getName(); // O CPF será o nome de usuário
        return clienteService.buscarClientePorCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));
    }

    @PostMapping("/solicitar")
    public ResponseEntity<Emprestimo> solicitarEmprestimo(@RequestBody EmprestimoSolicitacaoDTO emprestimoSolicitacaoDTO) {
        Cliente cliente = getAuthenticatedCliente();
        Emprestimo novoEmprestimo = emprestimoService.solicitarEmprestimo(cliente, emprestimoSolicitacaoDTO);
        return ResponseEntity.ok(novoEmprestimo);
    }

    @GetMapping
    public ResponseEntity<List<Emprestimo>> getMeusEmprestimos() {
        Cliente cliente = getAuthenticatedCliente();
        List<Emprestimo> emprestimos = emprestimoService.buscarEmprestimosPorCliente(cliente);
        return ResponseEntity.ok(emprestimos);
    }

    @PutMapping("/aprovar/{id}")
    public ResponseEntity<Emprestimo> aprovarEmprestimo(@PathVariable Long id) {
        // Esta rota deve ser acessível apenas por gerentes
        Emprestimo emprestimoAprovado = emprestimoService.aprovarEmprestimo(id);
        // Lógica para adicionar o valor do empréstimo ao saldo do cliente
        Cliente cliente = emprestimoAprovado.getCliente();
        cliente.setSaldo(cliente.getSaldo() + emprestimoAprovado.getValor());
        clienteService.atualizarCliente(cliente);
        return ResponseEntity.ok(emprestimoAprovado);
    }
}