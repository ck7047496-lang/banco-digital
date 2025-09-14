package com.banco.bancodigital;

import com.banco.bancodigital.model.PapelUsuario;
import com.banco.bancodigital.model.Usuario;
import com.banco.bancodigital.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.banco.bancodigital.repository")
public class BancoDigitalApplication {

	public static void main(String[] args) {
		SpringApplication.run(BancoDigitalApplication.class, args);
	}

}