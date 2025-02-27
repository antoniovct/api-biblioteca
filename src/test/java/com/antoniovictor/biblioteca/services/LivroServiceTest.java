package com.antoniovictor.biblioteca.services;

import com.antoniovictor.biblioteca.dto.LivroAtualizacao;
import com.antoniovictor.biblioteca.dto.LivroEntrada;
import com.antoniovictor.biblioteca.dto.LivroSaida;
import com.antoniovictor.biblioteca.entities.Livro;
import com.antoniovictor.biblioteca.repository.LivroRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @InjectMocks
    private LivroService livroService;
    @Mock
    private LivroRepository livroRepository;

    @Test
    @DisplayName("Verifica se o livro foi cadastrado corretamente e se o método save foi chamado")
    void cadastrarLivroCenario1() {
        //ARRANGE
        var livroEntrada = new LivroEntrada("Game of Thrones", "George R. R. Martin", "ficcao", 10);
        when(livroRepository.save(any(Livro.class))).thenReturn(new Livro(livroEntrada));
        //ACT
        var livroSaida = livroService.cadastrarLivro(livroEntrada);
        //ASSERT
        verify(livroRepository).save(any(Livro.class));
        assertNotNull(livroSaida);
        assertEquals(livroEntrada.titulo(), livroSaida.titulo());
    }

    @Test
    @DisplayName("Verifica se deu erro ao cadastrar um livro com categoria inexistente")
    void cadastrarLivroCenario2() {
        //ARRANGE
        var livroEntrada = new LivroEntrada("Game of Thrones", "George R. R. Martin", "fantasia", 10);
        //ACT + ASSERT
        assertThrows(IllegalArgumentException.class, () -> livroService.cadastrarLivro(livroEntrada));
    }

    @Test
    @DisplayName("Verifica se a listagem de livros foi bem sucedida")
    void listarLivros() {
        //ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        Page<Livro> pageLivros = new PageImpl<>(List.of());
        when(livroRepository.findAll(pageable)).thenReturn(pageLivros );
        //ACT
        var livrosSaida = livroService.listarLivros(pageable);
        //ASSERT
        verify(livroRepository).findAll(pageable);
        assertNotNull(livrosSaida);
    }

    @Test
    @DisplayName("Verifica se a busca de livro por id foi bem sucedida")
    void buscarLivroPorIdCenario1() {
        //ARRANGE
        when(livroRepository.findById(anyLong())).thenReturn(Optional.of(new Livro()));
        //ACT
        var livrosaida = livroService.buscarLivroPorId(1L);
        //ASSERT
        verify(livroRepository).findById(anyLong());
        assertNotNull(livrosaida);
    }

    @Test
    @DisplayName("Verifica se deu erro ao buscar um livro inexistente")
    void buscarLivroPorIdCenario2() {
        //ARRANGE
        when(livroRepository.findById(anyLong())).thenReturn(Optional.empty());
        //ACT + ASSERT
        assertThrows(EntityNotFoundException.class, () -> livroService.buscarLivroPorId(1L));
    }

    @Test
    @DisplayName("Busca todos os livros da categoria indicada")
    void listarLivrosPorCategoriaCenario1() {
        //ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        when(livroRepository.findAllByCategoriaContainingIgnoreCase("drama",pageable)).thenReturn(new PageImpl<>(List.of(Optional.of(new Livro()))));
        //ACT
        var livrosSaida = livroService.listarLivrosPorCategoria("drama", pageable);
        //ASSERT
        verify(livroRepository).findAllByCategoriaContainingIgnoreCase("drama",pageable);
        assertNotNull(livrosSaida);
    }

    @Test
    @DisplayName("Verifica se deu erro ao buscar livros")
    void listarLivrosPorCategoriaCenario2() {
        //ARRANGE
        Page<Optional<Livro>> livros = spy(new PageImpl<>(List.of()));
        Pageable pageable = PageRequest.of(0, 10);
        when(livros.isEmpty()).thenReturn(true);
        when(livroRepository.findAllByCategoriaContainingIgnoreCase("drama",pageable)).thenReturn(livros);
        //ACT + ASSERT
        assertThrows(EntityNotFoundException.class, () -> livroService.listarLivrosPorCategoria("drama", pageable));
    }

    @Test
    @DisplayName("Verifica se deu erro ao buscar livros de categoria inexistente")
    void listarLivrosPorCategoriaCenario3() {
        //ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        //ACT + ASSERT
        assertThrows(IllegalArgumentException.class, () -> livroService.listarLivrosPorCategoria("teste", pageable));
    }

    @Test
    @DisplayName("Verifica se a busca de livros por nome foi bem sucedida")
    void listarLivrosPorNomeCenario1() {
        //ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        when(livroRepository.findAllByTituloContaining("teste",pageable)).thenReturn(new PageImpl<>(List.of(Optional.of(new Livro()))));
        //ACT
        var livroSaida = livroService.listarLivrosPorNome("teste", pageable);
        //ASSERT
        verify(livroRepository).findAllByTituloContaining("teste",pageable);
        assertNotNull(livroSaida);
    }

    @Test
    @DisplayName("Verifica se deu erro ao buscar livros por nome inexistente")
    void listarLivrosPorNomeCenario2() {
        //ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        when(livroRepository.findAllByTituloContaining("teste",pageable)).thenReturn(new PageImpl<>(List.of()));
        //ACT + ASSERT
        assertThrows(EntityNotFoundException.class, () -> livroService.listarLivrosPorNome("teste", pageable));
    }

    @Test
    @DisplayName("Verifica se a atualização de livro foi bem sucedida")
    void atualizarLivroCenario1() {
        //ARRANGE
        Livro livro = spy(new Livro());
        LivroAtualizacao livroAtualizacao = new LivroAtualizacao("teste",null,null,null);
        when(livroRepository.findById(anyLong())).thenReturn(Optional.of(livro));
        //ACT
        var livroSaida = livroService.atualizarLivro(1L, livroAtualizacao);
        //ASSERT
        verify(livroRepository).findById(1L);
        verify(livro).setTitulo(livroAtualizacao.titulo());
        assertNotNull(livroSaida);
    }

    @Test
    @DisplayName("Verifica se deu erro ao atualizar um livro inexistente")
    void atualizarLivroCenario2() {
        //ARRANGE
        LivroAtualizacao livroAtualizacao = new LivroAtualizacao("teste",null,null,null);
        when(livroRepository.findById(anyLong())).thenReturn(Optional.empty());
        //ACT + ASSERT
        assertThrows(EntityNotFoundException.class, () -> livroService.atualizarLivro(1L, livroAtualizacao));
    }

    @Test
    @DisplayName("Verifica se a exclusão de livro foi bem sucedida")
    void deletarLivro() {
        //ACT
        livroService.deletarLivro(1L);
        //ASSERT
        verify(livroRepository).deleteById(anyLong());
    }
}