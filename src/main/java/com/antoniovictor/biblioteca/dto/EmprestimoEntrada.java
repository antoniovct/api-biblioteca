package com.antoniovictor.biblioteca.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EmprestimoEntrada(
        @NotNull
        @Positive
        Long idUsuario,
        @NotNull
        @Positive
        Long idLivro
) {
}
