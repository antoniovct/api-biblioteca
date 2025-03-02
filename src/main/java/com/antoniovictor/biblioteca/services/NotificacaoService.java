package com.antoniovictor.biblioteca.services;

import com.antoniovictor.biblioteca.entities.Emprestimo;
import com.antoniovictor.biblioteca.entities.Livro;
import com.antoniovictor.biblioteca.entities.Usuario;
import com.antoniovictor.biblioteca.repository.EmprestimoRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class NotificacaoService {

    private final JavaMailSender javaMailSender;
    private final EmprestimoRepository emprestimoRepository;

    public NotificacaoService(JavaMailSender javaMailSender, EmprestimoRepository emprestimoRepository) {
        this.javaMailSender = javaMailSender;
        this.emprestimoRepository = emprestimoRepository;
    }

    private void enviarEmail(String para, String assunto, String mensagem) {

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(para);
        email.setSubject(assunto);
        email.setText(mensagem);

        javaMailSender.send(email);
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void lembreteDevolucao() {
        var dataAtual = LocalDate.now();
        var emprestimosProximosAoFim = emprestimoRepository.findAll().stream()
                .filter(e -> e.getFim().equals(dataAtual.plusDays(1))).toList();

        if (!emprestimosProximosAoFim.isEmpty()) {
            for (Emprestimo emprestimo : emprestimosProximosAoFim) {
                var usuario = emprestimo.getUsuario();
                enviarEmail(usuario.getEmail(), "Devolução de Livro", "O prazo de devolução do livro " + emprestimo.getLivro().getTitulo() + " encerra amanhã.");
            }
        }
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void avisoAtraso() {
        var dataAtual = LocalDate.now();
        var emprestimosAtrasados = emprestimoRepository.findAll().stream()
                .filter(e -> e.getFim().isBefore(dataAtual)).toList();

        if (!emprestimosAtrasados.isEmpty()) {
            for (Emprestimo emprestimo : emprestimosAtrasados) {
                var usuario = emprestimo.getUsuario();
                enviarEmail(usuario.getEmail(), "Atraso na Devolução de Livro", "O prazo de devolução do livro " + emprestimo.getLivro().getTitulo() + " encerrou em " + emprestimo.getFim() + ".");
            }
        }
    }

    public void notificarLivroDisponivel(Usuario usuario, Livro livro) {
        enviarEmail(usuario.getEmail(), "Livro Disponível", "O livro " + livro.getTitulo() + " está disponível para empréstimo.");
    }

}
