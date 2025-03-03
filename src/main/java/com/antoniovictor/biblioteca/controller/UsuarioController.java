package com.antoniovictor.biblioteca.controller;

import com.antoniovictor.biblioteca.dto.UsuarioAtualizacao;
import com.antoniovictor.biblioteca.dto.UsuarioEntrada;
import com.antoniovictor.biblioteca.dto.UsuarioSaida;
import com.antoniovictor.biblioteca.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
@RequestMapping("usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("cadastrar")
    public ResponseEntity<UsuarioSaida> cadastrar(@RequestBody @Valid UsuarioEntrada usuarioEntrada, UriComponentsBuilder uriBuilder) {
        var usuario = usuarioService.cadastrar(usuarioEntrada);
        var uri = uriBuilder.path("usuarios/usuario/{id}").buildAndExpand(usuario.id()).toUri();
        return ResponseEntity.created(uri).body(usuario);
    }

    @PostMapping("/usuario/{id}/verificar-email")
    public ResponseEntity<String> verificarEmail(@PathVariable(name = "id") long id, @RequestParam(name = "codigo") String codigo) {
        var resposta = usuarioService.validarEmail(id, codigo);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioSaida>> listar(Pageable pageable) {
        var usuarios = usuarioService.listar(pageable);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<UsuarioSaida> buscarPorId(@PathVariable(value = "id") long id) {
        var usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/usuario/{id}")
    public ResponseEntity<UsuarioSaida> atualizar(@PathVariable(value = "id") long id, @RequestBody UsuarioAtualizacao usuarioAtualizacao) {
        var usuario = usuarioService.atualizar(id, usuarioAtualizacao);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/usuario/{id}/bloquear")
    public ResponseEntity<Void> bloquear(@PathVariable(value = "id") long id) {
        usuarioService.bloquear(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/usuario/{id}")
    public ResponseEntity<Void> remover(@PathVariable(value = "id") long id) {
        usuarioService.remover(id);
        return ResponseEntity.noContent().build();
    }

}
