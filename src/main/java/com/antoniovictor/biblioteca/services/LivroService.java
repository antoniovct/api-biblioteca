package com.antoniovictor.biblioteca.services;

import com.antoniovictor.biblioteca.dto.LivroAtualizacao;
import com.antoniovictor.biblioteca.dto.LivroEntrada;
import com.antoniovictor.biblioteca.dto.LivroSaida;
import com.antoniovictor.biblioteca.entities.Categoria;
import com.antoniovictor.biblioteca.entities.Livro;
import com.antoniovictor.biblioteca.repository.LivroRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LivroService {
    private final LivroRepository livroRepository;
    private final String mensagemErroCategoria = "Digite um valor válido de categoria: " +
            Arrays.stream(Categoria.values()).map(Categoria::name).map(String::toLowerCase).collect(Collectors.joining(", "));

    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @Transactional
    public LivroSaida cadastrarLivro(LivroEntrada livroEntrada) {
        var categoriaExistente = Arrays.stream(Categoria.values()).anyMatch(c -> Objects.equals(c.name(), livroEntrada.categoria().toUpperCase()));
        if (!categoriaExistente) {
            throw new IllegalArgumentException(mensagemErroCategoria);
        }
        Livro livro = new Livro(livroEntrada);
        livroRepository.save(livro);
        return new LivroSaida(livro);
    }

    public Page<LivroSaida> listarLivros(Pageable pageable) {
        return livroRepository.findAll(pageable)
                .map(LivroSaida::new);
    }

    public LivroSaida buscarLivroPorId(long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado!"));
        return new LivroSaida(livro);
    }

    public Page<LivroSaida> listarLivrosPorCategoria(String categoria, Pageable pageable) {
        var categoriaExistente = Arrays.stream(Categoria.values()).anyMatch(c -> Objects.equals(c.name(), categoria.toUpperCase()));
        if (categoriaExistente) {
            Page<Optional<Livro>> livros = livroRepository.findAllByCategoriaContainingIgnoreCase(categoria,pageable);
            if (livros.isEmpty()) {
                throw new EntityNotFoundException("Nenhum livro encontrado!");
            } else {
                return livros.map(Optional::get).map(LivroSaida::new);
            }
        } else {
            throw new IllegalArgumentException(mensagemErroCategoria);
        }
    }

    public Page<LivroSaida> listarLivrosPorNome(String nome, Pageable pageable) {
        Page<Optional<Livro>> livros = livroRepository.findAllByTituloContaining(nome, pageable);
        if (livros.isEmpty()) {
            throw new EntityNotFoundException("Nenhum livro encontrado!");
        } else {
            return livros.map(Optional::get).map(LivroSaida::new);
        }
    }

    @Transactional
    public LivroSaida atualizarLivro(long id, LivroAtualizacao livroAtualizacao) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado!"));
        if (livroAtualizacao.autor() != null) {
            livro.setAutor(livroAtualizacao.autor());
        } else if (livroAtualizacao.titulo() != null) {
            livro.setTitulo(livroAtualizacao.titulo());
        } else if(livroAtualizacao.categoria() != null) {
            var categoriaExistente = Arrays.stream(Categoria.values()).anyMatch(c -> Objects.equals(c.name(), livroAtualizacao.categoria().toUpperCase()));
            if (!categoriaExistente) {
                throw new IllegalArgumentException(mensagemErroCategoria);
            }
            livro.setCategoria(Categoria.valueOf(livroAtualizacao.categoria().toUpperCase()));
        } else if(livroAtualizacao.estoque() != null) {
            livro.setEstoque(livroAtualizacao.estoque());
        }
        return new LivroSaida(livro);
    }

    @Transactional
    public void deletarLivro(long id) {
        livroRepository.deleteById(id);
    }
}
