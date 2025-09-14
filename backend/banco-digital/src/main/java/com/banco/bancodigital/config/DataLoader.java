package com.banco.bancodigital.config;

import com.banco.bancodigital.model.PapelUsuario;
import com.banco.bancodigital.model.Usuario;
import com.banco.bancodigital.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Procura pelo gerente ou cria um novo
            Usuario gerente = usuarioRepository.findByEmail("gerente@banco.com")
                .orElse(new Usuario());

            // Define ou atualiza os dados do gerente
            gerente.setNome("Gerente");
            gerente.setCpf("00000000000");
            gerente.setEmail("gerente@banco.com");
            gerente.setSenha(passwordEncoder.encode("senha123")); // Garante que a senha seja a correta
            gerente.setPapel(PapelUsuario.ROLE_GERENTE);
            gerente.setStatus(com.banco.bancodigital.model.StatusUsuario.ATIVO);
            
            usuarioRepository.save(gerente);
            System.out.println("Gerente padrão verificado/atualizado com sucesso.");
        };
    }
}