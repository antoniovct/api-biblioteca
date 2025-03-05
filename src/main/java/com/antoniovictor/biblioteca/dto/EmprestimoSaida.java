package com.antoniovictor.biblioteca.dto;

import com.antoniovictor.biblioteca.entities.Emprestimo;
import com.antoniovictor.biblioteca.entities.Livro;
import com.antoniovictor.biblioteca.entities.StatusEmprestimo;
import com.antoniovictor.biblioteca.entities.Usuario;

import java.time.LocalDate;

public record EmprestimoSaida(
        Long id,
        LocalDate inicio,
        LocalDate fim,
        Double multa,
        String livro,
        StatusEmprestimo statusEmprestimo,
        String usuario
) {
    public EmprestimoSaida(Emprestimo emprestimo) {
        this(emprestimo.getId(), emprestimo.getInicio(),emprestimo.getFim(),
                emprestimo.getMulta(),emprestimo.getLivro().getTitulo(),emprestimo.getStatus(),emprestimo.getUsuario().getNome());
    }
}
