package com.banco.bancodigital.dtos;

import com.banco.bancodigital.model.PapelUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String nome;
    private String cpf;
    private String email;
    private String endereco;
    private String senha;
    private PapelUsuario papel;
}