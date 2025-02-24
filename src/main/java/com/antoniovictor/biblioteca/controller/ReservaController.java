package com.antoniovictor.biblioteca.controller;

import com.antoniovictor.biblioteca.dto.ReservaAtualizacao;
import com.antoniovictor.biblioteca.dto.ReservaEntrada;
import com.antoniovictor.biblioteca.dto.ReservaSaida;
import com.antoniovictor.biblioteca.error.AtualizacaoReservaException;
import com.antoniovictor.biblioteca.error.CadastroReservaException;
import com.antoniovictor.biblioteca.services.ReservaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity adicionarReserva(@RequestBody ReservaEntrada reservaEntrada, UriComponentsBuilder uriComponentsBuilder) {
        try {
            var reserva = reservaService.novaReserva(reservaEntrada.livroId(), reservaEntrada.usuarioId());
            var uri = uriComponentsBuilder.path("/reservas/{id}").buildAndExpand(reserva.id()).toUri();
            return ResponseEntity.created(uri).build();
        } catch (CadastroReservaException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Page<ReservaSaida>> listarReservas(Pageable pageable) {
        var reservas = reservaService.listaReservas(pageable);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/reserva/{id}")
    public ResponseEntity<ReservaSaida> buscarReservaPorId(@PathVariable("id") long id) {
        var reserva = reservaService.buscaReserva(id);
        return ResponseEntity.ok(reserva);
    }

    @GetMapping("/status")
    public ResponseEntity<Page<ReservaSaida>> listarReservasPorStatus(@RequestParam(name = "status") String status, Pageable pageable) {
        var reservas = reservaService.listaReservasPorStatus(status, pageable);
        return ResponseEntity.ok(reservas);
    }

    @PutMapping("/reserva/{id}")
    public ResponseEntity atualizarStatusReserva(@PathVariable("id") long id, @RequestBody ReservaAtualizacao reservaAtualizacao) {
        try {
            var reserva = reservaService.atualizarReserva(id, reservaAtualizacao);
            return ResponseEntity.ok(reserva);
        } catch (AtualizacaoReservaException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("/reserva/{id}")
    public ResponseEntity<Void> excluirReserva(@PathVariable("id") long id) {
        reservaService.excluirReserva(id);
        return ResponseEntity.noContent().build();
    }
}
