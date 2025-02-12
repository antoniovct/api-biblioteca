package com.antoniovictor.biblioteca.dto;

import com.antoniovictor.biblioteca.entities.Categoria;

public record LivroEntrada(
        String titulo,
        String autor,
        Categoria categoria,
        Integer quantidade
) {
}
