package com.antoniovictor.biblioteca.dto;

import com.antoniovictor.biblioteca.entities.Categoria;
import jakarta.validation.constraints.Positive;

public record LivroAtualizacao(
        String titulo,
        String autor,
        String categoria,
        @Positive
        Integer estoque
) {
}
