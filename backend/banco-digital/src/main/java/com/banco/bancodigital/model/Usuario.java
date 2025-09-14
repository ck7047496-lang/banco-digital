package com.banco.bancodigital.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true)
    private String cpf;

    @Column(unique = true)
    private String email;

    private String endereco;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PapelUsuario papel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusUsuario status = StatusUsuario.PENDENTE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SituacaoCredito situacaoCredito = SituacaoCredito.PENDENTE;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    @Column(name = "limite_credito", nullable = false, precision = 10, scale = 2)
    private BigDecimal limiteCredito = BigDecimal.ZERO;

    @Column(name = "criado_em")
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + papel.name()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.status == StatusUsuario.ATIVO;
    }
}