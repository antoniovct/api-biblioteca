package com.antoniovictor.biblioteca.entities;

import com.antoniovictor.biblioteca.dto.LivroEntrada;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id" )
@Entity
@Table(name = "livros")
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String autor;
    @Enumerated(EnumType.STRING)
    private Categoria categoria;
    private Integer estoque;
    private Boolean disponivel;
    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Emprestimo> emprestimos = new ArrayList<>();
    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas = new ArrayList<>();

    public Livro(LivroEntrada livroEntrada) {
        this.titulo = livroEntrada.titulo();
        this.autor = livroEntrada.autor();
        this.categoria = Categoria.valueOf(livroEntrada.categoria().toUpperCase());
        this.estoque = livroEntrada.quantidade();
        this.disponivel = true;
    }

    public void addEmprestimo(Emprestimo emprestimo) {
        this.emprestimos.add(emprestimo);
        this.estoque -= 1;
        if (this.estoque == 0) {
            this.disponivel = false;
        }
    }

    public void addReserva(Reserva reserva) {
        this.reservas.add(reserva);
    }

    public void removeReserva(Reserva reserva) {
        this.reservas.remove(reserva);
    }
}
