package com.antoniovictor.biblioteca.controller;

import com.antoniovictor.biblioteca.dto.UsuarioAtualizacao;
import com.antoniovictor.biblioteca.dto.UsuarioEntrada;
import com.antoniovictor.biblioteca.dto.UsuarioSaida;
import com.antoniovictor.biblioteca.services.UsuarioService;
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


@RestController
@RequestMapping("usuarios")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Usuario", description = "Operações relacionadas a usuários")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Cadastrar um usuário", description = "Cadastra um usuário na base de dados",responses ={
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso",content =
            @Content(mediaType = "application/json",schema = @Schema(implementation = UsuarioSaida.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao cadastrar usuário",content =
            @Content(mediaType = "text/pain",schema = @Schema(implementation = String.class)))
    })
    @PostMapping("cadastrar")
    public ResponseEntity<UsuarioSaida> cadastrar(@RequestBody @Valid UsuarioEntrada usuarioEntrada, UriComponentsBuilder uriBuilder) {
        var usuario = usuarioService.cadastrar(usuarioEntrada);
        var uri = uriBuilder.path("usuarios/usuario/{id}").buildAndExpand(usuario.id()).toUri();
        return ResponseEntity.created(uri).body(usuario);
    }

    @Operation(summary = "Verificar email", description = "Verifica o email do usuário",responses ={
            @ApiResponse(responseCode = "200", description = "Email verificado com sucesso",content =
            @Content(mediaType = "text/pain",schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PostMapping("/usuario/{id}/verificar-email")
    public ResponseEntity<String> verificarEmail(@PathVariable(name = "id") long id, @RequestParam(name = "codigo") String codigo) {
        var resposta = usuarioService.validarEmail(id, codigo);
        return ResponseEntity.ok(resposta);
    }

    @Operation(summary = "Listar usuários", description = "Lista todos os usuários cadastrados na base de dados",responses = {
            @ApiResponse(responseCode = "200", description = "Usuários listados com sucesso",content =
            @Content(mediaType = "application/json",schema = @Schema(implementation = UsuarioSaida.class)))
    })
    @GetMapping
    public ResponseEntity<Page<UsuarioSaida>> listar(Pageable pageable) {
        var usuarios = usuarioService.listar(pageable);
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Buscar usuário por ID", description = "Busca um usuário na base de dados pelo ID",responses = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso",content =
            @Content(mediaType = "application/json",schema = @Schema(implementation = UsuarioSaida.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",content =
            @Content(mediaType = "text/pain",schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/usuario/{id}")
    public ResponseEntity<UsuarioSaida> buscarPorId(@PathVariable(value = "id") long id) {
        var usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Atualizar usuário", description = "Atualiza um usuário na base de dados",responses = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",content =
            @Content(mediaType = "application/json",schema = @Schema(implementation = UsuarioSaida.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",content =
            @Content(mediaType = "text/pain",schema = @Schema(implementation = String.class)))
    })
    @PutMapping("/usuario/{id}")
    public ResponseEntity<UsuarioSaida> atualizar(@PathVariable(value = "id") long id, @RequestBody UsuarioAtualizacao usuarioAtualizacao) {
        var usuario = usuarioService.atualizar(id, usuarioAtualizacao);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Bloquear usuário", description = "Bloqueia um usuário na base de dados",responses = {
            @ApiResponse(responseCode = "204", description = "Usuário bloqueado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping("/usuario/{id}/bloquear")
    public ResponseEntity<Void> bloquear(@PathVariable(value = "id") long id) {
        usuarioService.bloquear(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remover usuário", description = "Remove um usuário da base de dados",responses = {
            @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso")
    })
    @DeleteMapping("/usuario/{id}")
    public ResponseEntity<Void> remover(@PathVariable(value = "id") long id) {
        usuarioService.remover(id);
        return ResponseEntity.noContent().build();
    }

}
