package com.banco.bancodigital.dto;

import lombok.Data;

@Data
public class ClienteCadastroDTO {
    private String nome;
    private String cpf;
    private String email;
    private String senha;
}