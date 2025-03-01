package com.antoniovictor.biblioteca.entities;

import com.antoniovictor.biblioteca.dto.ReservaEntrada;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime data;
    private LocalDateTime inicio;
    private LocalDateTime expiracao;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "livro_id")
    private Livro livro;
    @Enumerated(EnumType.STRING)
    private StatusReserva status;

    public Reserva(Usuario usuario, Livro livro) {
        this.data = LocalDateTime.now();
        this.usuario = usuario;
        this.livro = livro;
        this.status = StatusReserva.PENDENTE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reserva reserva)) return false;
        return Objects.equals(id, reserva.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
