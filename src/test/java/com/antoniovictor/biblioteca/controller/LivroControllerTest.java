package com.antoniovictor.biblioteca.controller;

import com.antoniovictor.biblioteca.dto.LivroAtualizacao;
import com.antoniovictor.biblioteca.dto.LivroEntrada;
import com.antoniovictor.biblioteca.dto.LivroSaida;
import com.antoniovictor.biblioteca.entities.Categoria;
import com.antoniovictor.biblioteca.entities.Livro;
import com.antoniovictor.biblioteca.repository.LivroRepository;
import com.antoniovictor.biblioteca.services.LivroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(LivroController.class)
class LivroControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private LivroService livroService;
    private Livro livro;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {

        livro = new Livro(1L, "Construção do Eu", "Augusto Cury", Categoria.DRAMA, 10, true,
                List.of(), List.of());
    }

    @Test
    @DisplayName("Deve retornar status 201 ao cadastrar um livro, Verifica se o header location foi criado e se o corpo da resposta está correto")
    @WithMockUser(roles = {"ADMIN"})
    void cadastrar() throws Exception {
        //ARRANGE
        LivroSaida livroSaida = new LivroSaida(livro);
        when(livroService.cadastrarLivro(any(LivroEntrada.class))).thenReturn(livroSaida);
        //ACT + ASSERT
        mockMvc.perform(post("/livros/cadastrar")
                        .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LivroEntrada("Construção do Eu", "Augusto Cury", "drama", 10)))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "http://localhost/livros/livro/1"))
                .andExpect(content().json(objectMapper.writeValueAsString(livroSaida)));
    }

    @Test
    @DisplayName("Deve retornar status 200 ao listar livros")
    @WithMockUser(roles = {"ADMIN"})
    void listarLivros() throws Exception {
        //ARRANGE
        when(livroService.listarLivros(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));
        //ACT + ASSERT
        mockMvc.perform(get("/livros"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar status 200 ao buscar um livro por id")
    @WithMockUser(roles = {"ADMIN"})
    void buscarLivroPorId() throws Exception {
        //ARRANGE
        when(livroService.buscarLivroPorId(1L)).thenReturn(new LivroSaida(livro));
        //ACT + ASSERT
        mockMvc.perform(get("/livros/livro/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar status 200 ao buscar livros por categoria")
    @WithMockUser(roles = {"ADMIN"})
    void buscarLivrosPorCategoria() throws Exception {
        //ARRANGE
        var livro1 = new Livro(1L, "Construção do Eu", "Augusto Cury", Categoria.DRAMA, 10, true,
                List.of(), List.of());
        var livro2 = new Livro(2L, "Game of Thrones", "George R. R. Martin", Categoria.FICCAO, 10, true,
                List.of(), List.of());
        var livro3 = new Livro(3L, "O Senhor dos Anéis", "J. R. R. Tolkien", Categoria.FICCAO, 10, true,
                List.of(), List.of());
        when(livroService.listarLivrosPorCategoria(eq("drama"), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(new LivroSaida(livro1))));
        when(livroService.listarLivrosPorCategoria(eq("ficcao"), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(new LivroSaida(livro2), new LivroSaida(livro3))));
        //ACT + ASSERT
        mockMvc.perform(get("/livros/categorias?categoria=drama"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar status 200 ao buscar livros por título")
    @WithMockUser(roles = {"ADMIN"})
    void buscarLivrosPorTitulo() throws Exception {
        //ARRANGE
        when(livroService.listarLivrosPorNome(eq("Construção do Eu"), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(new LivroSaida(livro))));
        //ACT + ASSERT
        mockMvc.perform(get("/livros/livro?titulo=Construção do Eu"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar status 200 ao atualizar um livro")
    @WithMockUser(roles = {"ADMIN"})
    void atualizarLivro() throws Exception {
        //ARRANGE
        var livroAtualizacao = new LivroAtualizacao("O Poder do Agora", "Eckhart Tolle", "drama", 10);
        when(livroService.atualizarLivro(1L, livroAtualizacao)).thenReturn(new LivroSaida(new Livro(1L, "O Poder do Agora", "Eckhart Tolle", Categoria.DRAMA, 10, true, List.of(), List.of())));
        //ACT + ASSERT
        mockMvc.perform(put("/livros/livro/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(livroAtualizacao)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar status 204 ao remover um livro")
    @WithMockUser(roles = {"ADMIN"})
    void removerLivro() throws Exception {
        //ACT + ASSERT
        mockMvc.perform(delete("/livros/livro/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}