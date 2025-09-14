package com.banco.bancodigital.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String papel;
    private String cpf;
    private String email;
    private String nome;
    private String status;
}