package com.antoniovictor.biblioteca.dto;

import com.antoniovictor.biblioteca.entities.RoleUsuario;
import com.antoniovictor.biblioteca.entities.Usuario;

public record UsuarioSaida(
        Long id,
        String nome,
        String email,
        String cpf,
        RoleUsuario role
) {
    public UsuarioSaida(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getCpf(), usuario.getRole());
    }
}
