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
        Livro livro,
        StatusEmprestimo statusEmprestimo,
        Usuario usuario
) {
    public EmprestimoSaida(Emprestimo emprestimo) {
        this(emprestimo.getId(), emprestimo.getInicio(),emprestimo.getFim(),
                emprestimo.getMulta(),emprestimo.getLivro(),emprestimo.getStatus(),emprestimo.getUsuario());
    }
}
