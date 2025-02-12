package com.antoniovictor.biblioteca.dto;

import com.antoniovictor.biblioteca.entities.Categoria;

public record LivroAtualizacao(
        String titulo,
        String autor,
        Categoria categoria,
        Integer estoque
) {
}
