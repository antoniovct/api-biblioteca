package com.antoniovictor.biblioteca.controller;

import com.antoniovictor.biblioteca.dto.ReservaAtualizacao;
import com.antoniovictor.biblioteca.dto.ReservaEntrada;
import com.antoniovictor.biblioteca.dto.ReservaSaida;
import com.antoniovictor.biblioteca.error.AtualizacaoReservaException;
import com.antoniovictor.biblioteca.error.CadastroReservaException;
import com.antoniovictor.biblioteca.services.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("reservas")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Reserva", description = "Operações relacionadas a reservas de livros")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @Operation(summary = "Adicionar uma reserva", description = "Adiciona uma reserva na base de dados",responses ={
            @ApiResponse(responseCode = "201", description = "Reserva adicionada com sucesso",content =
            @Content(mediaType = "application/json",schema = @Schema(implementation = ReservaSaida.class))),
            @ApiResponse(responseCode = "500", description = "Erro ao adicionar reserva",content =
            @Content(mediaType = "text/pain",schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Livro ou usuário não encontrado")
    })
    @PostMapping("/adicionar")
    public ResponseEntity adicionarReserva(@RequestBody ReservaEntrada reservaEntrada, UriComponentsBuilder uriComponentsBuilder) {
        try {
            var reserva = reservaService.novaReserva(reservaEntrada.livroId(), reservaEntrada.usuarioId());
            var uri = uriComponentsBuilder.path("/reservas/reserva/{id}").buildAndExpand(reserva.id()).toUri();
            return ResponseEntity.created(uri).build();
        } catch (CadastroReservaException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @Operation(summary = "Listar reservas", description = "Lista todas as reservas cadastradas na base de dados",responses = {
            @ApiResponse(responseCode = "200", description = "Reservas listadas com sucesso",content =
            @Content(mediaType = "application/json",schema = @Schema(implementation = ReservaSaida.class)))
    })
    @GetMapping
    public ResponseEntity<Page<ReservaSaida>> listarReservas(Pageable pageable) {
        var reservas = reservaService.listaReservas(pageable);
        return ResponseEntity.ok(reservas);
    }

    @Operation(summary = "Buscar reserva por ID", description = "Busca uma reserva na base de dados pelo ID",responses = {
            @ApiResponse(responseCode = "200", description = "Reserva encontrada com sucesso",content =
            @Content(mediaType = "application/json",schema = @Schema(implementation = ReservaSaida.class))),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada",content =
            @Content(mediaType = "text/pain",schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/reserva/{id}")
    public ResponseEntity<ReservaSaida> buscarReservaPorId(@PathVariable("id") long id) {
        var reserva = reservaService.buscaReserva(id);
        return ResponseEntity.ok(reserva);
    }

    @Operation(summary = "Listar reservas por status", description = "Lista todas as reservas cadastradas na base de dados por status",responses = {
            @ApiResponse(responseCode = "200", description = "Reservas listadas com sucesso",content =
            @Content(mediaType = "application/json",schema = @Schema(implementation = ReservaSaida.class))),
            @ApiResponse(responseCode = "400", description = "Status inválido",content =
            @Content(mediaType = "text/pain",schema = @Schema(implementation = String.class)))})
    @GetMapping("/status")
    public ResponseEntity<Page<ReservaSaida>> listarReservasPorStatus(@RequestParam(name = "status") String status, Pageable pageable) {
        var reservas = reservaService.listaReservasPorStatus(status, pageable);
        return ResponseEntity.ok(reservas);
    }

    @Operation(summary = "Atualizar status de uma reserva", description = "Atualiza o status de uma reserva na base de dados",responses = {
            @ApiResponse(responseCode = "200", description = "Status da reserva atualizado com sucesso",content =
            @Content(mediaType = "application/json",schema = @Schema(implementation = ReservaSaida.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao atualizar status da reserva",content =
            @Content(mediaType = "text/pain",schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada",content =
            @Content(mediaType = "text/pain",schema = @Schema(implementation = String.class)))
    })
    @PatchMapping("/reserva/{id}")
    public ResponseEntity atualizarStatusReserva(@PathVariable("id") long id, @RequestBody ReservaAtualizacao reservaAtualizacao) {
        try {
            var reserva = reservaService.atualizarReserva(id, reservaAtualizacao);
            return ResponseEntity.ok(reserva);
        } catch (AtualizacaoReservaException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @Operation(summary = "Excluir reserva", description = "Exclui uma reserva da base de dados",responses = {
            @ApiResponse(responseCode = "204", description = "Reserva excluída com sucesso"),
    })
    @DeleteMapping("/reserva/{id}")
    public ResponseEntity<Void> excluirReserva(@PathVariable("id") long id) {
        reservaService.excluirReserva(id);
        return ResponseEntity.noContent().build();
    }
}
