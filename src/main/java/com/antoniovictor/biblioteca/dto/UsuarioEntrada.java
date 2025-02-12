package com.antoniovictor.biblioteca.dto;

public record UsuarioEntrada(
        String nome,
        String email,
        String senha,
        String cpf
) {
}
