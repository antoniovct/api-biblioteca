package com.antoniovictor.biblioteca.controller;

import com.antoniovictor.biblioteca.dto.EmprestimoEntrada;
import com.antoniovictor.biblioteca.dto.EmprestimoSaida;
import com.antoniovictor.biblioteca.error.CadastroEmprestimoException;
import com.antoniovictor.biblioteca.error.RenovacaoEmprestimoException;
import com.antoniovictor.biblioteca.services.EmprestimoService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("emprestimos")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Emprestimo", description = "Operações relacionadas a empréstimos de livros")
public class EmprestimoController {
    private final EmprestimoService emprestimoService;

    public EmprestimoController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }
    
    @PostMapping
    @Operation(summary = "Cadastrar um empréstimo", description = "Cadastra um empréstimo na base de dados",responses = {
            @ApiResponse(responseCode = "201", description = "Empréstimo cadastrado com sucesso",
                    content = @Content(mediaType = "application/json",schema = @Schema(implementation = EmprestimoSaida.class))),
            @ApiResponse(responseCode = "500", description = "Erro ao cadastrar empréstimo",
                    content = @Content(mediaType = "text/plain",schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity novoEmprestimo(@RequestBody @Valid EmprestimoEntrada emprestimoEntrada, UriComponentsBuilder uriBuilder) {
        try {
            var emprestimo = emprestimoService.novoEmprestimo(emprestimoEntrada);
            var uri = uriBuilder.path("/emprestimo/{id}").buildAndExpand(emprestimo.id()).toUri();
            return ResponseEntity.created(uri).body(emprestimo);
        } catch (CadastroEmprestimoException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @Operation(summary = "Listar empréstimos", description = "Lista todos os empréstimos cadastrados na base de dados",responses = {
            @ApiResponse(responseCode = "200", description = "Empréstimos listados com sucesso",
                    content = @Content(mediaType = "application/json",schema = @Schema(implementation = EmprestimoSaida.class)))
    })
    @GetMapping
    public ResponseEntity<Page<EmprestimoSaida>> listarEmprestimos(@PageableDefault(sort = "inicio", size = 20) Pageable pageable) {
        var emprestimos = emprestimoService.listaEmprestimos(pageable);
        return ResponseEntity.ok(emprestimos);
    }

    @Operation(summary = "Buscar empréstimo por ID", description = "Busca um empréstimo na base de dados pelo ID",responses = {
            @ApiResponse(responseCode = "200", description = "Empréstimo encontrado com sucesso",
                    content = @Content(mediaType = "application/json",schema = @Schema(implementation = EmprestimoSaida.class)))
    })
    @GetMapping("/emprestimo/{id}")
    public ResponseEntity<EmprestimoSaida> buscarEmprestimoPorId(@PathVariable long id) {
        var emprestimo = emprestimoService.buscarEmprestimoPorId(id);
        return ResponseEntity.ok(emprestimo);
    }

    @Operation(summary = "Renovar empréstimo", description = "Renova um empréstimo na base de dados",responses = {
            @ApiResponse(responseCode = "200", description = "Empréstimo renovado com sucesso",
                    content = @Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Erro ao renovar empréstimo",
                    content = @Content(mediaType = "text/plain",schema = @Schema(implementation = String.class)))
    })
    @PutMapping("/emprestimo/{id}/renovar")
    public ResponseEntity<String> renovarEmprestimo(@PathVariable long id) {
        try {
            emprestimoService.renovarEmprestimo(id);
            return ResponseEntity.ok("Empréstimo renovado com sucesso!");
        } catch (RenovacaoEmprestimoException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @Operation(summary = "Devolver empréstimo", description = "Devolve um empréstimo",responses = {
            @ApiResponse(responseCode = "200", description = "Empréstimo devolvido com sucesso",
                    content = @Content(mediaType = "text/plain",schema = @Schema(implementation = String.class)))
    })
    @PatchMapping("/emprestimo/{id}/devolucao")
    public ResponseEntity<String> devolverEmprestimo(@PathVariable long id) {
        emprestimoService.devolverEmprestimo(id);
        return ResponseEntity.ok("Empréstimo devolvido com sucesso!");
    }

    @Operation(summary = "Excluir empréstimo", description = "Exclui um empréstimo na base de dados",responses = {
            @ApiResponse(responseCode = "204", description = "Empréstimo excluído com sucesso")
    })
    @DeleteMapping("/emprestimo/{id}")
    public ResponseEntity<Void> excluirEmprestimo(@PathVariable long id) {
        emprestimoService.removerEmprestimo(id);
        return ResponseEntity.noContent().build();
    }
}
