package com.antoniovictor.biblioteca.controller;

import com.antoniovictor.biblioteca.security.JwtConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("login")
@SecurityRequirement(name = "basic-auth")
@Tag(name = "Autenticação", description = "Operações relacionadas a autenticação")
public class AutenticacaoController {
    private final JwtConfig jwtConfig;

    public AutenticacaoController(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Operation(summary = "Autenticar", description = "Gera um token JWT para autenticação",responses = {
            @ApiResponse(responseCode = "200", description = "Token gerado com sucesso",content =
            @Content(mediaType = "text/plain",schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "Erro ao autenticar")
    })
    @PostMapping
    public ResponseEntity autenticar(Authentication authentication) {
        return ResponseEntity.ok(jwtConfig.generateToken(authentication));
    }
}
