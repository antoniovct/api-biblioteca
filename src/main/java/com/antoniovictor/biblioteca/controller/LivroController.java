package com.antoniovictor.biblioteca.controller;

import com.antoniovictor.biblioteca.dto.LivroAtualizacao;
import com.antoniovictor.biblioteca.dto.LivroEntrada;
import com.antoniovictor.biblioteca.dto.LivroSaida;
import com.antoniovictor.biblioteca.services.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("livros")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Livro", description = "Operações relacionadas a livros")
public class LivroController {

    private final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    @Operation(summary = "Cadastrar um livro", description = "Cadastra um livro na base de dados",responses ={
            @ApiResponse(responseCode = "201", description = "Livro cadastrado com sucesso",content = 
            @Content(mediaType = "application/json",schema = @Schema(implementation = LivroSaida.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao cadastrar livro",content =
            @Content(mediaType = "text/pain",schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/cadastrar")
    public ResponseEntity<LivroSaida> cadastrar(@RequestBody @Valid LivroEntrada livroEntrada, UriComponentsBuilder uriBuilder) {
        var livro = livroService.cadastrarLivro(livroEntrada);
        var uri = uriBuilder.path("/livros/livro/{id}").buildAndExpand(livro.id()).toUri();
        return ResponseEntity.created(uri).body(livro);
    }
    
    @Operation(summary = "Listar livros", description = "Lista todos os livros cadastrados na base de dados",responses = {
            @ApiResponse(responseCode = "200", description = "Livros listados com sucesso",content = 
            @Content(mediaType = "application/json",schema = @Schema(implementation = LivroSaida.class)))
    })
    @GetMapping
    public ResponseEntity<Page<LivroSaida>> listarLivros(Pageable pageable) {
        var livros = livroService.listarLivros(pageable);
        return ResponseEntity.ok(livros);
    }
    
    @Operation(summary = "Buscar livro por ID", description = "Busca um livro na base de dados pelo ID",responses = {
            @ApiResponse(responseCode = "200", description = "Livro encontrado com sucesso",content = 
            @Content(mediaType = "application/json",schema = @Schema(implementation = LivroSaida.class))),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado",content = 
            @Content(mediaType = "text/pain",schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/livro/{id}")
    public ResponseEntity<LivroSaida> buscarLivroPorId(@PathVariable("id") long id) {
        var livro = livroService.buscarLivroPorId(id);
        return ResponseEntity.ok(livro);
    }
    
    @Operation(summary = "Buscar livros por categoria", description = "Busca livros na base de dados por categoria",responses = {
            @ApiResponse(responseCode = "200", description = "Livros encontrados com sucesso",content = 
            @Content(mediaType = "application/json",schema = @Schema(implementation = LivroSaida.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum livro encontrado",content = 
            @Content(mediaType = "text/pain",schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Categoria inválida",content =
            @Content(mediaType = "text/pain",schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/categorias")
    public ResponseEntity<Page<LivroSaida>> buscarLivrosPorCategoria(@RequestParam("categoria") String categoria, Pageable pageable) {
        var livros = livroService.listarLivrosPorCategoria(categoria, pageable);
        return ResponseEntity.ok(livros);
    }
    
    @Operation(summary = "Buscar livros por título", description = "Busca livros na base de dados por título",responses = {
            @ApiResponse(responseCode = "200", description = "Livros encontrados com sucesso",content = 
            @Content(mediaType = "application/json",schema = @Schema(implementation = LivroSaida.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum livro encontrado",content = 
            @Content(mediaType = "text/pain",schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/livro")
    public ResponseEntity<Page<LivroSaida>> buscarLivrosPorTitulo(@RequestParam("titulo") String titulo, Pageable pageable) {
        var livros = livroService.listarLivrosPorNome(titulo, pageable);
        return ResponseEntity.ok(livros);
    }
    
    @Operation(summary = "Atualizar livro", description = "Atualiza um livro na base de dados",responses = {
            @ApiResponse(responseCode = "200", description = "Livro atualizado com sucesso",content = 
            @Content(mediaType = "application/json",schema = @Schema(implementation = LivroSaida.class))),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado",content = 
            @Content(mediaType = "text/pain",schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Categoria inválida",content =
            @Content(mediaType = "text/pain",schema = @Schema(implementation = String.class)))
    })
    @PutMapping("/livro/{id}")
    public ResponseEntity<LivroSaida> atualizarLivro(@PathVariable("id") long id, @RequestBody @Valid LivroAtualizacao livroAtualizacao) {
        var livro = livroService.atualizarLivro(id, livroAtualizacao);
        return ResponseEntity.ok(livro);
    }
    
    @Operation(summary = "Remover livro", description = "Remove um livro da base de dados",responses = {
            @ApiResponse(responseCode = "204", description = "Livro removido com sucesso")
    })
    @DeleteMapping("/livro/{id}")
    public ResponseEntity<Void> removerLivro(@PathVariable("id") long id) {
        livroService.deletarLivro(id);
        return ResponseEntity.noContent().build();
    }
}
