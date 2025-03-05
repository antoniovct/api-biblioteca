package com.antoniovictor.biblioteca.dto;

import com.antoniovictor.biblioteca.entities.Livro;
import com.antoniovictor.biblioteca.entities.Reserva;
import com.antoniovictor.biblioteca.entities.StatusReserva;
import com.antoniovictor.biblioteca.entities.Usuario;

import java.time.LocalDateTime;

public record ReservaSaida(
        Long id,
        LocalDateTime data,
        LocalDateTime inicio,
        LocalDateTime expiracao,
        String usuario,
        String livro,
        StatusReserva status
) {
    public ReservaSaida(Reserva reserva) {
        this(reserva.getId(), reserva.getData(),reserva.getInicio(),
                reserva.getExpiracao(), reserva.getUsuario().getNome(), reserva.getLivro().getTitulo(), reserva.getStatus());
    }
}
