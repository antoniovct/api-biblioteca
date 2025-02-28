package com.antoniovictor.biblioteca.controller;

import com.antoniovictor.biblioteca.dto.UsuarioAtualizacao;
import com.antoniovictor.biblioteca.dto.UsuarioEntrada;
import com.antoniovictor.biblioteca.dto.UsuarioSaida;
import com.antoniovictor.biblioteca.entities.Usuario;
import com.antoniovictor.biblioteca.services.UsuarioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UsuarioService usuarioService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(1L, "Antonio", "antonio@admin.com", "123456","04274656136", new ArrayList<>(), true, new ArrayList<>());
    }

    @Test
    @DisplayName("Verifica se o método cadastrar retorna status 201, header Location e body com o usuário cadastrado")
    @WithMockUser(roles = {"ADMIN"})
    void cadastrar() throws Exception {
        //ARRANGE
        var usuarioEntrada = new UsuarioEntrada("Antonio", "antonio@admin.com", "123456", "04274656136");
        when(usuarioService.cadastrar(usuarioEntrada)).thenReturn(new UsuarioSaida(usuario));
        //ACT + ASSERT
        mockMvc.perform(post("/usuarios/cadastrar").with(csrf())
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(usuarioEntrada)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "http://localhost/usuarios/usuario/1"))
                .andExpect(content().json(objectMapper.writeValueAsString(new UsuarioSaida(usuario))));
    }

    @Test
    @DisplayName("Verifica se o método listar retorna status 200 e uma lista de usuários")
    @WithMockUser(roles = {"ADMIN"})
    void listar() throws Exception {
        //ARRANGE
        var pageable = PageRequest.of(0, 10);
        when(usuarioService.listar(any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<>(List.of(new UsuarioSaida(usuario))),pageable,1));
        //ACT + ASSERT
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new PageImpl<>(new ArrayList<>(List.of(new UsuarioSaida(usuario))),pageable,1))));
    }

    @Test
    @DisplayName("Verifica se o método buscarPorId retorna status 200 e um usuário")
    @WithMockUser(roles = {"ADMIN"})
    void buscarPorId() throws Exception {
        //ARRANGE
        when(usuarioService.buscarPorId(1L)).thenReturn(new UsuarioSaida(usuario));
        //ACT + ASSERT
        mockMvc.perform(get("/usuarios/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new UsuarioSaida(usuario))));
    }

    @Test
    @DisplayName("Verifica se o método atualizar retorna status 200 e um usuário atualizado")
    @WithMockUser(roles = {"ADMIN"})
    void atualizar() throws Exception {
        //ARRANGE
        var usuarioAtualizacao = new UsuarioAtualizacao("Antonio Victor", null, null);
        when(usuarioService.atualizar(1L, usuarioAtualizacao)).thenReturn(new UsuarioSaida(usuario));
        //ACT + ASSERT
        mockMvc.perform(put("/usuarios/usuario/1").with(csrf())
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(usuarioAtualizacao)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new UsuarioSaida(usuario))));
    }

    @Test
    @DisplayName("Verifica se o método bloquear retorna status 204")
    @WithMockUser(roles = {"ADMIN"})
    void bloquear() throws Exception {
        //ACT + ASSERT
        mockMvc.perform(put("/usuarios/usuario/1/bloquear").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Verifica se o método remover retorna status 204")
    @WithMockUser(roles = {"ADMIN"})
    void remover() throws Exception {
        //ACT + ASSERT
        mockMvc.perform(delete("/usuarios/usuario/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}