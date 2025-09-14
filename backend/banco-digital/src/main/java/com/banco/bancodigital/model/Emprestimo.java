package com.banco.bancodigital.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "emprestimos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private Integer parcelas;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal juros;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "valor_parcela", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorParcela;

    @Column(name = "data_solicitacao")
    private OffsetDateTime dataSolicitacao = OffsetDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEmprestimo status = StatusEmprestimo.PENDENTE;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}