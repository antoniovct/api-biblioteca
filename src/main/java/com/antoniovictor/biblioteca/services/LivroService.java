package com.antoniovictor.biblioteca.services;

import com.antoniovictor.biblioteca.dto.LivroAtualizacao;
import com.antoniovictor.biblioteca.dto.LivroEntrada;
import com.antoniovictor.biblioteca.dto.LivroSaida;
import com.antoniovictor.biblioteca.entities.Livro;
import com.antoniovictor.biblioteca.repository.LivroRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LivroService {
    private final LivroRepository livroRepository;

    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @Transactional
    public LivroSaida cadastrarLivro(LivroEntrada livroEntrada) {
        Livro livro = new Livro(livroEntrada);
        livroRepository.save(livro);
        return new LivroSaida(livro);
    }

    public List<LivroSaida> listarLivros() {
        return livroRepository.findAll().stream()
                .map(LivroSaida::new)
                .toList();
    }

    public LivroSaida buscarLivroPorId(long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado!"));
        return new LivroSaida(livro);
    }

    public List<LivroSaida> listarLivrosPorCategoria(String categoria) {
        List<Optional<Livro>> livros = livroRepository.findAllByCategoriaContaining(categoria);
        if (livros.isEmpty()) {
            throw new EntityNotFoundException("Nenhum livro encontrado!");
        } else {
            return livros.stream().map(Optional::get).map(LivroSaida::new).toList();
        }
    }

    public List<LivroSaida> listarLivrosPorNome(String nome) {
        List<Optional<Livro>> livros = livroRepository.findAllByTituloContaining(nome);
        if (livros.isEmpty()) {
            throw new EntityNotFoundException("Nenhum livro encontrado!");
        } else {
            return livros.stream().map(Optional::get).map(LivroSaida::new).toList();
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
            livro.setCategoria(livroAtualizacao.categoria());
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
