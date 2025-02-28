package com.antoniovictor.biblioteca.controller;

import com.antoniovictor.biblioteca.dto.ReservaAtualizacao;
import com.antoniovictor.biblioteca.dto.ReservaEntrada;
import com.antoniovictor.biblioteca.dto.ReservaSaida;
import com.antoniovictor.biblioteca.entities.*;
import com.antoniovictor.biblioteca.error.AtualizacaoReservaException;
import com.antoniovictor.biblioteca.error.CadastroReservaException;
import com.antoniovictor.biblioteca.services.ReservaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservaController.class)
@ExtendWith(MockitoExtension.class)
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ReservaService reservaService;
    private Usuario usuario;
    private Livro livro;
    private ReservaEntrada reservaEntrada;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        usuario = new Usuario(1L, "Antonio", "antonio@admin.com", "123456","04274656136", new ArrayList<>(), true, new ArrayList<>());
        livro = new Livro(1L, "Construção do Eu", "Augusto Cury", Categoria.DRAMA, 10, true, new ArrayList<>(), new ArrayList<>());
        reservaEntrada = new ReservaEntrada(1L, 1L);
        objectMapper.registerModule(new JavaTimeModule());
    }


    @Test
    @DisplayName("Deve retornar status 201 ao adicionar uma reserva, Verifica se o header location foi criado e se o corpo da resposta está correto")
    @WithMockUser(roles = {"ADMIN"})
    void adicionarReservaCenario1() throws Exception {
        //ARRANGE
        var reserva = new Reserva(usuario, livro);
        reserva.setId(1L);
        when(reservaService.novaReserva(reservaEntrada.livroId(), reservaEntrada.usuarioId())).thenReturn(new ReservaSaida(reserva));
        //ACT + ASSERT
        mockMvc.perform(post("/reservas/adicionar").with(csrf())
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(reservaEntrada)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "http://localhost/reservas/reserva/1"));
    }

    @Test
    @DisplayName("Verifica se o status retornado é 500 ao adicionar uma reserva com livro indisponível")
    @WithMockUser(roles = {"ADMIN"})
    void adicionarReservaCenario2() throws Exception {
        //ARRANGE
        doThrow(new CadastroReservaException("Reserva não efetuada, livro disponível ou máximo de reservas ativas atingido.")).when(reservaService).novaReserva(reservaEntrada.livroId(), reservaEntrada.usuarioId());
        //ACT + ASSERT
        mockMvc.perform(post("/reservas/adicionar").with(csrf())
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(reservaEntrada)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Verifica se o status retornado é 200")
    @WithMockUser(roles = {"ADMIN"})
    void listarReservas() throws Exception {
        //ARRANGE
        when(reservaService.listaReservas(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(new ReservaSaida(new Reserva(usuario, livro)))));
        //ACT + ASSERT
        mockMvc.perform(get("/reservas"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Verifica se o status retornado é 200")
    @WithMockUser(roles = {"ADMIN"})
    void buscarReservaPorId() throws Exception {
        //ARRANGE
        var reserva = new Reserva(usuario, livro);
        reserva.setId(1L);
        when(reservaService.buscaReserva(1L)).thenReturn(new ReservaSaida(reserva));
        //ACT + ASSERT
        mockMvc.perform(get("/reservas/reserva/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Verifica se o status retornado é 200")
    @WithMockUser(roles = {"ADMIN"})
    void listarReservasPorStatus() throws Exception {
        //ARRANGE
        var reserva1 = new Reserva(usuario, livro);
        reserva1.setId(1L);
        var reserva2 = new Reserva(usuario, livro);
        reserva2.setId(2L);
        when(reservaService.listaReservasPorStatus(eq("ativa"), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(new ReservaSaida(reserva1), new ReservaSaida(reserva2))));
        //ACT + ASSERT
        mockMvc.perform(get("/reservas/status?status=ativa"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Verifica se o status retornado é 200")
    @WithMockUser(roles = {"ADMIN"})
    void atualizarStatusReservaCenario1() throws Exception {
        //ARRANGE
        var reserva = new Reserva(usuario, livro);
        reserva.setId(1L);
        reserva.setStatus(StatusReserva.ATIVA);
        when(reservaService.atualizarReserva(1L, new ReservaAtualizacao("finalizada"))).thenReturn(new ReservaSaida(reserva));
        //ACT + ASSERT
        mockMvc.perform(patch("/reservas/reserva/1").with(csrf())
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(new ReservaAtualizacao("finalizada"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Verifica se o status retornado é 400 ao atualizar uma reserva com status inválido")
    @WithMockUser(roles = {"ADMIN"})
    void atualizarStatusReservaCenario2() throws Exception {
        //ARRANGE
        doThrow(new AtualizacaoReservaException("Digite um valor válido para atualizar o status da reserva: ativa, finalizada ou expirada.")).when(reservaService).atualizarReserva(1L, new ReservaAtualizacao("invalido"));
        //ACT + ASSERT
        mockMvc.perform(patch("/reservas/reserva/1").with(csrf())
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(new ReservaAtualizacao("invalido")))
                .accept("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Verifica se o status retornado é 204")
    @WithMockUser(roles = {"ADMIN"})
    void excluirReserva() throws Exception {
        //ACT + ASSERT
        mockMvc.perform(delete("/reservas/reserva/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}