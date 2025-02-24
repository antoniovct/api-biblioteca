package com.antoniovictor.biblioteca.controller;

import com.antoniovictor.biblioteca.dto.EmprestimoEntrada;
import com.antoniovictor.biblioteca.dto.EmprestimoSaida;
import com.antoniovictor.biblioteca.error.CadastroEmprestimoException;
import com.antoniovictor.biblioteca.error.RenovacaoEmprestimoException;
import com.antoniovictor.biblioteca.services.EmprestimoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("emprestimos")
public class EmprestimoController {
    private final EmprestimoService emprestimoService;

    public EmprestimoController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }
    
    @PostMapping
    public ResponseEntity novoEmprestimo(@RequestBody EmprestimoEntrada emprestimoEntrada, UriComponentsBuilder uriBuilder) {
        try {
            var emprestimo = emprestimoService.novoEmprestimo(emprestimoEntrada);
            var uri = uriBuilder.path("/emprestimo/{id}").buildAndExpand(emprestimo.id()).toUri();
            return ResponseEntity.created(uri).body(emprestimo);
        } catch (CadastroEmprestimoException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Page<EmprestimoSaida>> listarEmprestimos(Pageable pageable) {
        var emprestimos = emprestimoService.listaEmprestimos(pageable);
        return ResponseEntity.ok(emprestimos);
    }

    @GetMapping("/emprestimo/{id}")
    public ResponseEntity<EmprestimoSaida> buscarEmprestimoPorId(@PathVariable long id) {
        var emprestimo = emprestimoService.buscarEmprestimoPorId(id);
        return ResponseEntity.ok(emprestimo);
    }

    @PutMapping("/emprestimo/{id}/renovar")
    public ResponseEntity<String> renovarEmprestimo(@PathVariable long id) {
        try {
            emprestimoService.renovarEmprestimo(id);
            return ResponseEntity.ok("Empréstimo renovado com sucesso!");
        } catch (RenovacaoEmprestimoException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/emprestimo/{id}/devolucao")
    public ResponseEntity<String> devolverEmprestimo(@PathVariable long id) {
        emprestimoService.devolverEmprestimo(id);
        return ResponseEntity.ok("Empréstimo devolvido com sucesso!");
    }

    @DeleteMapping("/emprestimo/{id}")
    public ResponseEntity<Void> excluirEmprestimo(@PathVariable long id) {
        emprestimoService.removerEmprestimo(id);
        return ResponseEntity.noContent().build();
    }
}
