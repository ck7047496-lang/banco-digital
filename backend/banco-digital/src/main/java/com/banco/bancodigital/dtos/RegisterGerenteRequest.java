package com.banco.bancodigital.dtos;

import com.banco.bancodigital.model.PapelUsuario;

public class RegisterGerenteRequest extends RegisterRequest {

    public RegisterGerenteRequest() {
        setPapel(PapelUsuario.ROLE_GERENTE);
    }
}