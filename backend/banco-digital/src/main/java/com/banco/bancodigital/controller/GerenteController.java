package com.banco.bancodigital.controller;

import com.banco.bancodigital.model.Cliente;
import com.banco.bancodigital.model.Emprestimo;
import com.banco.bancodigital.service.ClienteService;
import com.banco.bancodigital.service.EmprestimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gerente")
public class GerenteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EmprestimoService emprestimoService;

    @PutMapping("/clientes/aprovar/{cpf}")
    public ResponseEntity<Cliente> aprovarCliente(@PathVariable String cpf) {
        Cliente cliente = clienteService.buscarClientePorCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));
        cliente.setAprovado(true);
        return ResponseEntity.ok(clienteService.atualizarCliente(cliente));
    }

    @PutMapping("/emprestimos/aprovar/{id}")
    public ResponseEntity<Emprestimo> aprovarEmprestimo(@PathVariable Long id) {
        Emprestimo emprestimo = emprestimoService.aprovarEmprestimo(id);
        // Lógica para adicionar o valor do empréstimo ao saldo do cliente
        Cliente cliente = emprestimo.getCliente();
        cliente.setSaldo(cliente.getSaldo() + emprestimo.getValor());
        clienteService.atualizarCliente(cliente);
        return ResponseEntity.ok(emprestimo);
    }

    @GetMapping("/clientes/pendentes")
    public ResponseEntity<List<Cliente>> getClientesPendentes() {
        // Implementar busca por clientes com aprovado = false
        // Por enquanto, retorna todos os clientes para simplificar
        return ResponseEntity.ok(clienteService.buscarTodosClientes().stream()
                .filter(cliente -> !cliente.isAprovado())
                .toList());
    }

    @GetMapping("/emprestimos/pendentes")
    public ResponseEntity<List<Emprestimo>> getEmprestimosPendentes() {
        return ResponseEntity.ok(emprestimoService.buscarTodosEmprestimos().stream()
                .filter(emprestimo -> !emprestimo.isAprovado())
                .toList());
    }
}