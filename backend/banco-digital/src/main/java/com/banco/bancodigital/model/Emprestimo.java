package com.banco.bancodigital.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Emprestimo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double valor;
    private int parcelas;
    private double jurosMensal;
    private LocalDate dataSolicitacao;
    private boolean aprovado;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
}