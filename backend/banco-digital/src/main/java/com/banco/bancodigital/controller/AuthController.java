package com.banco.bancodigital.controller;

import com.banco.bancodigital.dtos.AuthRequest;
import com.banco.bancodigital.dtos.AuthResponse;
import com.banco.bancodigital.dtos.RegisterGerenteRequest;
import com.banco.bancodigital.dtos.RegisterRequest;
import com.banco.bancodigital.model.Usuario;
import com.banco.bancodigital.repository.UsuarioRepository;
import com.banco.bancodigital.security.JwtService;
import com.banco.bancodigital.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioService usuarioService, JwtService jwtService, AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@RequestBody RegisterRequest request) {
        Usuario newUser = usuarioService.registerNewUser(request);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/register/gerente")
    public ResponseEntity<Usuario> registerGerente(@RequestBody RegisterGerenteRequest request) {
        Usuario newGerente = usuarioService.registerNewUser(request);
        return ResponseEntity.ok(newGerente);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getIdentifier().trim(), authRequest.getSenha().trim()));

        if (authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();

            Usuario authenticatedUser = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado após autenticação."));

            if (authenticatedUser.getStatus() != com.banco.bancodigital.model.StatusUsuario.ATIVO) {
                throw new UsernameNotFoundException("Usuário não está ativo. Status atual: " + authenticatedUser.getStatus().name());
            }

            final String token = jwtService.generateToken(email, authenticatedUser.getPapel().name());
            return ResponseEntity.ok(new AuthResponse(token, authenticatedUser.getPapel().name(), authenticatedUser.getCpf(), authenticatedUser.getEmail(), authenticatedUser.getNome(), authenticatedUser.getStatus().name()));
        } else {
            throw new UsernameNotFoundException("Credenciais inválidas!");
        }
    }

}