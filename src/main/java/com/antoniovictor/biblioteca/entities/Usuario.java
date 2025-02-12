package com.antoniovictor.biblioteca.entities;

import com.antoniovictor.biblioteca.dto.UsuarioEntrada;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id" )
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private String cpf;

    public Usuario(UsuarioEntrada usuarioEntrada, String senha) {
        this.nome = usuarioEntrada.nome();
        this.email = usuarioEntrada.email();
        this.senha = senha;
        this.cpf = usuarioEntrada.cpf();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.email.contains("admin")) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }else {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
