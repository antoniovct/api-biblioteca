package com.antoniovictor.biblioteca.services;

import com.antoniovictor.biblioteca.dto.ReservaAtualizacao;
import com.antoniovictor.biblioteca.dto.ReservaSaida;
import com.antoniovictor.biblioteca.entities.Reserva;
import com.antoniovictor.biblioteca.entities.StatusReserva;
import com.antoniovictor.biblioteca.error.AtualizacaoReservaException;
import com.antoniovictor.biblioteca.error.CadastroReservaException;
import com.antoniovictor.biblioteca.repository.LivroRepository;
import com.antoniovictor.biblioteca.repository.ReservaRepository;
import com.antoniovictor.biblioteca.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

@Service
public class ReservaService {
    private  final ReservaRepository reservaRepository;
    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;

    public ReservaService(ReservaRepository reservaRepository, LivroRepository livroRepository, UsuarioRepository usuarioRepository) {
        this.reservaRepository = reservaRepository;
        this.livroRepository = livroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ReservaSaida novaReserva(long idLivro, long idUsuario) throws CadastroReservaException {
        var livro = livroRepository.findById(idLivro)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado"));
        var usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado"));

        if(Boolean.TRUE.equals(!livro.getDisponivel()) && usuario.getReservas().stream().filter(r -> r.getStatus() == StatusReserva.ATIVA).toList().size() < 3)  {
            Reserva reserva = new Reserva(usuario, livro);
            reservaRepository.save(reserva);
            livro.addReserva(reserva);
            usuario.addReserva(reserva);
            return new ReservaSaida(reserva);
        } else {
            throw new CadastroReservaException("Reserva não efetuada, livro disponível ou máximo de reservas ativas atingido.");
        }
    }

    public Page<ReservaSaida> listaReservas(Pageable pageable) {
        var pageableSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("data"));

        return reservaRepository.findAll(pageableSort)
                .map(ReservaSaida::new);
    }

    public ReservaSaida buscaReserva(long idReserva) {
        var reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new EntityNotFoundException("Reserva não encontrada"));
        return new ReservaSaida(reserva);
    }

    public Page<ReservaSaida> listaReservasPorStatus(String statusReserva, Pageable pageable) {
        var listaDeStatus = Arrays.stream(StatusReserva.values()).toList().stream().map(Enum::name);
        if (listaDeStatus.anyMatch(s -> Objects.equals(s, statusReserva.toUpperCase()))) {
            var status = StatusReserva.valueOf(statusReserva.toUpperCase());
            return reservaRepository.findAllByStatusOrderByData(status, pageable)
                    .map(ReservaSaida::new);
        } else {
            throw new IllegalArgumentException("Digite um valor de status valido: ativa, pendente, finalizada ou expirada");
        }

    }


    @Transactional
    public ReservaSaida atualizarReserva(long idReserva, ReservaAtualizacao reservaAtualizacao) throws AtualizacaoReservaException {
        var reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new EntityNotFoundException("Reserva não encontrada"));
        StatusReserva statusAtualizado;
        var statusExistente = Arrays.stream(StatusReserva.values()).toList().stream()
                .anyMatch(s -> Objects.equals(s,reservaAtualizacao.status()));
        if (statusExistente) {
            statusAtualizado = StatusReserva.valueOf(reservaAtualizacao.status().toUpperCase());
        }else {
            throw new AtualizacaoReservaException("Digite um valor válido para atualizar o status da reserva: ativa, finalizada ou expirada.");
        }
        reserva.setStatus(statusAtualizado);
        if (statusAtualizado == StatusReserva.ATIVA) {
            reserva.setInicio(LocalDateTime.now());
            reserva.setExpiracao(LocalDateTime.now().plusHours(48));
            return new ReservaSaida(reserva);
        } else if (statusAtualizado == StatusReserva.FINALIZADA) {
            var livro = reserva.getLivro();
            livro.removeReserva(reserva);
            return new ReservaSaida(reserva);
        } else if (statusAtualizado == StatusReserva.EXPIRADA) {
            var livro = reserva.getLivro();
            livro.removeReserva(reserva);
            return new ReservaSaida(reserva);
        }else {
            throw new AtualizacaoReservaException("Digite um valor válido para atualizar o status da reserva: ativa, finalizada ou expirada.");
        }
    }

    @Transactional
    public void excluirReserva(long idReserva) {
        reservaRepository.deleteById(idReserva);
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void verificaReservaExpirada() {
        var dataAtual = LocalDateTime.now();
        var reservasExpiradas = reservaRepository.findByStatusAndExpiracaoGreaterThanEqual(StatusReserva.ATIVA, dataAtual);
        if(!reservasExpiradas.isEmpty()) {
            for(Reserva reserva : reservasExpiradas) {
                reserva.setStatus(StatusReserva.EXPIRADA);
                var livro = reserva.getLivro();
                livro.removeReserva(reserva);
            }
        }
    }


}
