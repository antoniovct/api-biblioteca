package com.antoniovictor.biblioteca.controller;

import com.antoniovictor.biblioteca.dto.LivroAtualizacao;
import com.antoniovictor.biblioteca.dto.LivroEntrada;
import com.antoniovictor.biblioteca.dto.LivroSaida;
import com.antoniovictor.biblioteca.services.LivroService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("livros")
public class LivroController {

    private final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<LivroSaida> cadastrar(@RequestBody @Valid LivroEntrada livroEntrada, UriComponentsBuilder uriBuilder) {
        var livro = livroService.cadastrarLivro(livroEntrada);
        var uri = uriBuilder.path("/livro/{id}").buildAndExpand(livro.id()).toUri();
        return ResponseEntity.created(uri).body(livro);
    }

    @GetMapping
    public ResponseEntity<Page<LivroSaida>> listarLivros(Pageable pageable) {
        var livros = livroService.listarLivros(pageable);
        return ResponseEntity.ok(livros);
    }

    @GetMapping("/livro/{id}")
    public ResponseEntity<LivroSaida> buscarLivroPorId(@PathVariable("id") long id) {
        var livro = livroService.buscarLivroPorId(id);
        return ResponseEntity.ok(livro);
    }

    @GetMapping("/categorias")
    public ResponseEntity<Page<LivroSaida>> buscarLivrosPorCategoria(@RequestParam("categoria") String categoria, Pageable pageable) {
        var livros = livroService.listarLivrosPorCategoria(categoria, pageable);
        return ResponseEntity.ok(livros);
    }

    @GetMapping("/livro")
    public ResponseEntity<Page<LivroSaida>> buscarLivrosPorTitulo(@RequestParam("titulo") String titulo, Pageable pageable) {
        var livros = livroService.listarLivrosPorNome(titulo, pageable);
        return ResponseEntity.ok(livros);
    }

    @PutMapping("/livro/{id}")
    public ResponseEntity<LivroSaida> atualizarLivro(@PathVariable("id") long id, @RequestBody LivroAtualizacao livroAtualizacao) {
        var livro = livroService.atualizarLivro(id, livroAtualizacao);
        return ResponseEntity.ok(livro);
    }

    @DeleteMapping("/livro/{id}")
    public ResponseEntity<Void> removerLivro(@PathVariable("id") long id) {
        livroService.deletarLivro(id);
        return ResponseEntity.noContent().build();
    }
}
