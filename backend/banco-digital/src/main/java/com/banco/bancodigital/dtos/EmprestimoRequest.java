package com.banco.bancodigital.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmprestimoRequest {
    private BigDecimal valor;
    private Integer parcelas;
}