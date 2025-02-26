package com.antoniovictor.biblioteca.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id" )
@Entity
@Table(name = "emprestimos")
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate inicio;
    private LocalDate fim;
    private Double multa;
    @ManyToOne
    @JoinColumn(name = "livro_id")
    private Livro livro;
    @Enumerated(EnumType.STRING)
    private StatusEmprestimo status;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Emprestimo(Usuario usuario,Livro livro) {
        this.inicio = LocalDate.now();
        this.fim = LocalDate.now().plusWeeks(2);
        this.status = StatusEmprestimo.ATIVO;
        this.usuario = usuario;
        this.livro = livro;
    }


    public void renovar() {
        this.fim = LocalDate.now().plusWeeks(2);
    }
}
