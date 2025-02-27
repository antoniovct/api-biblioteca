package com.antoniovictor.biblioteca.dto;

import com.antoniovictor.biblioteca.entities.Categoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LivroEntrada(
        @NotBlank
        String titulo,
        @NotBlank
        String autor,
        @NotBlank
        String categoria,
        @NotNull @Positive
        Integer quantidade
) {
}
