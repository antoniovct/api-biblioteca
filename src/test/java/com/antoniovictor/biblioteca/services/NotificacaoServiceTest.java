package com.antoniovictor.biblioteca.services;

import com.antoniovictor.biblioteca.entities.Emprestimo;
import com.antoniovictor.biblioteca.entities.Livro;
import com.antoniovictor.biblioteca.entities.Usuario;
import com.antoniovictor.biblioteca.repository.EmprestimoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    @InjectMocks
    private NotificacaoService notificacaoService;

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private JavaMailSender javaMailSender;

    private Usuario usuario;
    private Livro livro;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setEmail("sousasoares9@gmail.com");

        livro = new Livro();
        livro.setTitulo("O Pequeno Príncipe");
    }

    @Test
    @DisplayName("Deve enviar email de lembrete de devolução")
    void lembreteDevolucao() {
        // ARRANGE
        var emprestimo = new Emprestimo(usuario, livro);
        emprestimo.setFim(LocalDate.now().plusDays(1));
        when(emprestimoRepository.findAll()).thenReturn(List.of(emprestimo));

        // ACT
        notificacaoService.lembreteDevolucao();

        // ASSERT
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deve enviar email de aviso de atraso")
    void avisoAtraso() {
        // ARRANGE
        var emprestimo = new Emprestimo(usuario, livro);
        emprestimo.setFim(LocalDate.now().minusDays(1)); // Data no passado (atrasado)
        when(emprestimoRepository.findAll()).thenReturn(List.of(emprestimo));

        // ACT
        notificacaoService.avisoAtraso();

        // ASSERT
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deve enviar email de notificação de livro disponível")
    void notificarLivroDisponivel() {
        // ARRANGE
        Usuario usuario2 = new Usuario();
        usuario2.setEmail("usuario2@dominio.com");
        Livro livro2 = new Livro();
        livro2.setTitulo("Dom Quixote");

        // ACT
        notificacaoService.notificarLivroDisponivel(usuario2, livro2);

        // ASSERT
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
