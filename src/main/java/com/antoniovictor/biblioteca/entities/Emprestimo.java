package com.antoniovictor.biblioteca.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "emprestimos")
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate inicio;
    private LocalDate fim;
    private Double multa = 0.00;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Emprestimo that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
