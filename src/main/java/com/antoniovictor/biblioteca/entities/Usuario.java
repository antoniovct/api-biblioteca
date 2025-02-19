package com.antoniovictor.biblioteca.entities;

import com.antoniovictor.biblioteca.dto.UsuarioEntrada;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
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
    @Setter(AccessLevel.NONE)
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private String cpf;
    @OneToMany(mappedBy = "usuario",cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<Emprestimo> emprestimos = new ArrayList<>();
    private Boolean ativo;
    @OneToMany(mappedBy = "usuario",cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<Reserva> reservas = new ArrayList<>();

    public Usuario(UsuarioEntrada usuarioEntrada, String senha) {
        this.nome = usuarioEntrada.nome();
        this.email = usuarioEntrada.email();
        this.senha = senha;
        this.cpf = usuarioEntrada.cpf();
        this.ativo = true;
    }

    public void addReserva(Reserva reserva) {
        this.reservas.add(reserva);
    }

    public void addEmprestimo(Emprestimo emprestimo) {
        this.emprestimos.add(emprestimo);
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
