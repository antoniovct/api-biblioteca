package com.antoniovictor.biblioteca.entities;

import com.antoniovictor.biblioteca.dto.LivroEntrada;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id" )
@Entity
@Table(name = "livros")
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    private String titulo;
    private String autor;
    @Enumerated(EnumType.STRING)
    private Categoria categoria;
    private Integer estoque;

    public Livro(LivroEntrada livroEntrada) {
        this.titulo = livroEntrada.titulo();
        this.autor = livroEntrada.autor();
        this.categoria = livroEntrada.categoria();
        this.estoque = livroEntrada.quantidade();
    }

    public void saida(int quantidade) {
        if (this.estoque > 0) {
            this.estoque -= quantidade;
        }
    }
}
