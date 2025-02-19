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
        Usuario usuario,
        Livro livro,
        StatusReserva status
) {
    public ReservaSaida(Reserva reserva) {
        this(reserva.getId(), reserva.getData(),reserva.getInicio(),
                reserva.getExpiracao(), reserva.getUsuario(), reserva.getLivro(), reserva.getStatus());
    }
}
