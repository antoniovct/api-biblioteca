package com.antoniovictor.biblioteca.dto;

import com.antoniovictor.biblioteca.entities.Categoria;
import com.antoniovictor.biblioteca.entities.Livro;

public record LivroSaida(
        Long id,
        String titulo,
        String autor,
        Categoria categoria,
        Integer estoque
) {
    public LivroSaida(Livro livro) {
        this(livro.getId(), livro.getTitulo(), livro.getAutor(), livro.getCategoria(), livro.getEstoque());
    }
}
