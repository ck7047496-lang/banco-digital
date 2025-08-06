package com.banco.bancodigital.service;

import com.banco.bancodigital.dto.EmprestimoSolicitacaoDTO;
import com.banco.bancodigital.model.Cliente;
import com.banco.bancodigital.model.Emprestimo;
import com.banco.bancodigital.repository.EmprestimoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmprestimoService {

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    public Emprestimo solicitarEmprestimo(Cliente cliente, EmprestimoSolicitacaoDTO emprestimoSolicitacaoDTO) {
        // Regra 1: O cliente só pode solicitar empréstimo uma única vez
        List<Emprestimo> emprestimosAnteriores = emprestimoRepository.findByCliente(cliente);
        if (!emprestimosAnteriores.isEmpty()) {
            throw new RuntimeException("Cliente já solicitou um empréstimo anteriormente.");
        }

        // Regra 2: Só pode haver um empréstimo ativo por cliente
        Optional<Emprestimo> emprestimoAtivo = emprestimoRepository.findByClienteAndAprovadoFalse(cliente);
        if (emprestimoAtivo.isPresent()) {
            throw new RuntimeException("Cliente já possui um empréstimo ativo pendente de aprovação.");
        }

        // Regra 3: Limite de empréstimo e parcelas
        if (emprestimoSolicitacaoDTO.getValor() > 10000 || emprestimoSolicitacaoDTO.getParcelas() > 24) {
            throw new RuntimeException("Valor ou número de parcelas excede o limite permitido.");
        }

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setCliente(cliente);
        emprestimo.setValor(emprestimoSolicitacaoDTO.getValor());
        emprestimo.setParcelas(emprestimoSolicitacaoDTO.getParcelas());
        emprestimo.setJurosMensal(0.01); // 1% ao mês
        emprestimo.setDataSolicitacao(LocalDate.now());
        emprestimo.setAprovado(false); // Empréstimo inicialmente não aprovado
        return emprestimoRepository.save(emprestimo);
    }

    public Emprestimo aprovarEmprestimo(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado."));

        if (emprestimo.isAprovado()) {
            throw new RuntimeException("Empréstimo já aprovado.");
        }

        emprestimo.setAprovado(true);
        // O valor é disponibilizado imediatamente na conta do cliente (lógica a ser implementada no ClienteService)
        return emprestimoRepository.save(emprestimo);
    }

    public List<Emprestimo> buscarEmprestimosPorCliente(Cliente cliente) {
        return emprestimoRepository.findByCliente(cliente);
    }

    public List<Emprestimo> buscarTodosEmprestimos() {
        return emprestimoRepository.findAll();
    }
}