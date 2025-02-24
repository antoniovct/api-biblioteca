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
    @DisplayName("Cadastro do livro bem sucedido")
    void cadastrarLivro() {
        //ARRANGE
        //ACT
        var livroSaida = livroService.cadastrarLivro(mock(LivroEntrada.class));
        //ASSERT
        verify(livroRepository).save(any(Livro.class));
        assertNotNull(livroSaida);
    }

    @Test
    @DisplayName("Busca por todos os livros")
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
    @DisplayName("Busca um livro pelo id")
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
    @DisplayName("Erro: livro não encontrado")
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
    @DisplayName("Erro: nenhum livro encontrado")
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
    @DisplayName("Erro: categoria não existe")
    void listarLivrosPorCategoriaCenario3() {
        //ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        //ACT + ASSERT
        assertThrows(IllegalArgumentException.class, () -> livroService.listarLivrosPorCategoria("teste", pageable));
    }

    @Test
    @DisplayName("Busca livros pelo nome indicado")
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
    @DisplayName("Erro: nenhum livro encontrado")
    void listarLivrosPorNomeCenario2() {
        //ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        when(livroRepository.findAllByTituloContaining("teste",pageable)).thenReturn(new PageImpl<>(List.of()));
        //ACT + ASSERT
        assertThrows(EntityNotFoundException.class, () -> livroService.listarLivrosPorNome("teste", pageable));
    }

    @Test
    @DisplayName("Atualização de livro bem sucedida")
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
    @DisplayName("Erro: livro não encontrado")
    void atualizarLivroCenario2() {
        //ARRANGE
        LivroAtualizacao livroAtualizacao = new LivroAtualizacao("teste",null,null,null);
        when(livroRepository.findById(anyLong())).thenReturn(Optional.empty());
        //ACT + ASSERT
        assertThrows(EntityNotFoundException.class, () -> livroService.atualizarLivro(1L, livroAtualizacao));
    }

    @Test
    void deletarLivro() {
        //ACT
        livroService.deletarLivro(1L);
        //ASSERT
        verify(livroRepository).deleteById(anyLong());
    }
}