package com.antoniovictor.biblioteca.dto;

public record UsuarioAtualizacao(
        String nome,
        String email,
        String senha,
        String role
) {
}
