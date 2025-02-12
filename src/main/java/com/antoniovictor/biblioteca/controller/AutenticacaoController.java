package com.antoniovictor.biblioteca.controller;

import com.antoniovictor.biblioteca.security.JwtConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("login")
public class AutenticacaoController {
    private final JwtConfig jwtConfig;

    public AutenticacaoController(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @PostMapping
    public ResponseEntity autenticar(Authentication authentication) {
        return ResponseEntity.ok(jwtConfig.generateToken(authentication));
    }
}
