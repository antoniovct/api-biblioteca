package com.antoniovictor.biblioteca.services;

import com.antoniovictor.biblioteca.dto.EmprestimoEntrada;
import com.antoniovictor.biblioteca.dto.EmprestimoSaida;
import com.antoniovictor.biblioteca.entities.*;
import com.antoniovictor.biblioteca.error.CadastroEmprestimoException;
import com.antoniovictor.biblioteca.error.RenovacaoEmprestimoException;
import com.antoniovictor.biblioteca.repository.EmprestimoRepository;
import com.antoniovictor.biblioteca.repository.LivroRepository;
import com.antoniovictor.biblioteca.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
public class EmprestimoService {
    private final EmprestimoRepository emprestimoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LivroRepository livroRepository;

    public EmprestimoService(EmprestimoRepository emprestimoRepository, UsuarioRepository usuarioRepository, LivroRepository livroRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.usuarioRepository = usuarioRepository;
        this.livroRepository = livroRepository;
    }

    @Transactional
    public EmprestimoSaida novoEmprestimo(EmprestimoEntrada emprestimoEntrada) throws CadastroEmprestimoException {

        var usuario = usuarioRepository.findById(emprestimoEntrada.idUsuario())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        var livro = livroRepository.findById(emprestimoEntrada.idLivro())
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado"));
        var emprestimosPendentes = usuario.getEmprestimos().stream()
                .filter(e -> e.getStatus() == StatusEmprestimo.PENDENTE)
                .toList();
        var emprestimosAtivos = usuario.getEmprestimos().stream()
                .filter(e -> e.getStatus() == StatusEmprestimo.ATIVO)
                .toList();
        var reservaAtiva = livro.getReservas().stream().filter(r -> r.getStatus() == StatusReserva.ATIVA).findFirst();

        if(reservaAtiva.isPresent()) {
            var reserva = reservaAtiva.get();
            if (reserva.getUsuario().equals(usuario) && emprestimosPendentes.isEmpty() && livro.getDisponivel() && emprestimosAtivos.size() < 2) {
                var emprestimo = new Emprestimo(usuario, livro);
                emprestimoRepository.save(emprestimo);
                livro.addEmprestimo(emprestimo);
                usuario.addEmprestimo(emprestimo);
                return new EmprestimoSaida(emprestimo);
            } else {
                throw new CadastroEmprestimoException("Erro ao efetuar empréstimo, usuário não apto à solicitar empréstimo, ou livro não disponível!");
            }
        }

        if (emprestimosPendentes.isEmpty() && livro.getDisponivel() && emprestimosAtivos.size() < 2) {
            var emprestimo = new Emprestimo(usuario, livro);
            emprestimoRepository.save(emprestimo);
            livro.addEmprestimo(emprestimo);
            usuario.addEmprestimo(emprestimo);
            return new EmprestimoSaida(emprestimo);
        } else {
            throw new CadastroEmprestimoException("Erro ao efetuar empréstimo, usuário não apto à solicitar empréstimo, ou livro não disponível!");
        }
    }

    public List<EmprestimoSaida> listaEmprestimos() {
        return emprestimoRepository.findAll().stream()
                .sorted(Comparator.comparing(Emprestimo::getInicio))
                .map(EmprestimoSaida::new)
                .toList();
    }


    public EmprestimoSaida buscarEmprestimoPorId(long id) {
        var emprestimo = emprestimoRepository.findById(id).orElseThrow(
                EntityNotFoundException::new);
        return new EmprestimoSaida(emprestimo);
    }

    @Transactional
    public void renovarEmprestimo(long id) throws RenovacaoEmprestimoException {
        var emprestimo = emprestimoRepository.findById(id).orElseThrow(
                EntityNotFoundException::new);
        var livro = emprestimo.getLivro();
        var reservas = livro.getReservas().stream()
                .filter(r -> r.getStatus() == StatusReserva.PENDENTE)
                .sorted(Comparator.comparing(Reserva::getData))
                .toList();
        if (reservas.isEmpty()) {
            emprestimo.renovar();
        } else {
            throw new RenovacaoEmprestimoException("Erro ao renovar empréstimo, livro reservado por outro usuário!");
        }

    }

    @Transactional
    public void devolverEmprestimo(long id) {
        var emprestimo = emprestimoRepository.findById(id).orElseThrow(
                EntityNotFoundException::new);
        var dataAtual = LocalDate.now();
        if (dataAtual.isAfter(emprestimo.getFim())) {
            var diasEmAtraso = ChronoUnit.DAYS.between(emprestimo.getFim(), dataAtual);
            double valorMulta = diasEmAtraso * 2.0;
            emprestimo.setMulta(valorMulta);
        }
        emprestimo.devolver();
        Livro livro = emprestimo.getLivro();
        if (!livro.getDisponivel()) {
                livro.getReservas().stream()
                    .filter(r -> r.getStatus() == StatusReserva.PENDENTE)
                    .min(Comparator.comparing(Reserva::getData))
                        .ifPresent(r -> r.setStatus(StatusReserva.ATIVA));
        }
        livro.setEstoque(livro.getEstoque() + 1);
    }

    @Transactional
    public void removerEmprestimo(long id) {
        emprestimoRepository.deleteById(id);
    }

}
