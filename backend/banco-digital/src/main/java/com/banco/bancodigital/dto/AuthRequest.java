package com.banco.bancodigital.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String cpf;
    private String senha;
}