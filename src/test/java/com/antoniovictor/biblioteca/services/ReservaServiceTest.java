package com.antoniovictor.biblioteca.services;

import com.antoniovictor.biblioteca.dto.ReservaAtualizacao;
import com.antoniovictor.biblioteca.dto.ReservaSaida;
import com.antoniovictor.biblioteca.entities.Livro;
import com.antoniovictor.biblioteca.entities.Reserva;
import com.antoniovictor.biblioteca.entities.StatusReserva;
import com.antoniovictor.biblioteca.entities.Usuario;
import com.antoniovictor.biblioteca.error.AtualizacaoReservaException;
import com.antoniovictor.biblioteca.error.CadastroReservaException;
import com.antoniovictor.biblioteca.repository.LivroRepository;
import com.antoniovictor.biblioteca.repository.ReservaRepository;
import com.antoniovictor.biblioteca.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private LivroRepository livroRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @InjectMocks
    private ReservaService reservaService;

    @Test
    @DisplayName("Reserva bem sucedida")
    void novaReservaCenario1() throws CadastroReservaException {
        //ARRANGE
        Livro livro = spy(new Livro());
        Usuario usuario = spy(new Usuario());
        when(livroRepository.findById(anyLong())).thenReturn(Optional.of(livro));
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuario));
        when(livro.getDisponivel()).thenReturn(Boolean.FALSE);
        when(usuario.getReservas()).thenReturn(List.of(new Reserva()));
        //ACT
        var reservaSaida = reservaService.novaReserva(1L,1L);
        //ASSERT
        verify(reservaRepository).save(any(Reserva.class));
        verify(livro).addReserva(any(Reserva.class));
        assertThat(reservaSaida)
                .usingRecursiveComparison()
                .ignoringFields("data")
                .isEqualTo(new ReservaSaida(new Reserva(usuario, livro)));

    }

    @Test
    @DisplayName("Erro: Livro disponível")
    void novaReservaCenario2(){
        //ARRANGE
        Livro livro = mock(Livro.class);
        Usuario usuario = mock(Usuario.class);
        when(livroRepository.findById(anyLong())).thenReturn(Optional.of(livro));
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuario));
        when(livro.getDisponivel()).thenReturn(Boolean.TRUE);
        //ACT + ASSERT
        assertThrows(CadastroReservaException.class, () -> reservaService.novaReserva(1L,1L));
    }

    @Test
    @DisplayName("Erro: Usuário com 3 reservas ativas")
    void novaReservaCenario3(){
        //ARRANGE
        Livro livro = spy(new Livro());
        Usuario usuario = spy(new Usuario());
        usuario.setReservas(List.of(new Reserva(), new Reserva(), new Reserva()));
        usuario.getReservas().forEach(r -> r.setStatus(StatusReserva.ATIVA));
        when(livroRepository.findById(anyLong())).thenReturn(Optional.of(livro));
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuario));
        when(livro.getDisponivel()).thenReturn(Boolean.FALSE);
        //ACT + ASSERT
        assertThrows(CadastroReservaException.class, () -> reservaService.novaReserva(1L,1L));
    }

    @Test
    @DisplayName("Erro: Usuário não encontrado")
    void novaReservaCenario4(){
        //ARRANGE
        Livro livro = spy(new Livro());
        when(livroRepository.findById(anyLong())).thenReturn(Optional.of(livro));
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        //ACT + ASSERT
        assertThrows(EntityNotFoundException.class, () -> reservaService.novaReserva(1L,1L));
    }

    @Test
    @DisplayName("Erro: Livro não encontrado")
    void novaReservaCenario5(){
        //ARRANGE
        when(livroRepository.findById(anyLong())).thenReturn(Optional.empty());
        //ACT + ASSERT
        assertThrows(EntityNotFoundException.class, () -> reservaService.novaReserva(1L,1L));
    }

    @Test
    @DisplayName("Verifica se o retorno do método não é nulo")
    void listaReservas() {
        //ARRANGE
        Reserva reserva1 = new Reserva();
        Reserva reserva2 = new Reserva();
        Reserva reserva3 = new Reserva();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("data"));

        when(reservaRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(reserva1, reserva2, reserva3)));
        //ACT
        var listaReservas = reservaService.listaReservas(pageable);
        //ASSERT
        assertNotNull(listaReservas);
    }

    @Test
    @DisplayName("Retorna reserva associada ao id buscado")
    void buscaReservaCenario1() {
        //ARRANGE
        Reserva reserva = new Reserva();
        reserva.setId(1L);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        //ACT
        var reservaBuscada = reservaService.buscaReserva(1L);
        //ASSERT
        verify(reservaRepository).findById(anyLong());
        assertEquals(new ReservaSaida(reserva), reservaBuscada);
    }

    @Test
    @DisplayName("Erro: Reserva não encontrada")
    void buscaReservaCenario2() {
        //ARRANGE
        when(reservaRepository.findById(anyLong())).thenReturn(Optional.empty());
        //ACT + ASSERT
        assertThrows(EntityNotFoundException.class, () -> reservaService.buscaReserva(1L));
    }



    @Test
    @DisplayName("Erro: status digitado não existe")
    void listaReservasPorStatus() {
        //ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        //ACT + ASSERT
        assertThrows(IllegalArgumentException.class,() -> reservaService.listaReservasPorStatus("ativo", pageable));
    }

    @Test
    @DisplayName("Atualização do status da reserva para ATIVA, bem sucedida")
    void atualizarReservaCenario1() throws AtualizacaoReservaException {
        //ARRANGE
        Reserva reserva = spy(new Reserva());
        reserva.setStatus(StatusReserva.PENDENTE);
        when(reservaRepository.findById(anyLong())).thenReturn(Optional.of(reserva));
        //ACT
        var reservaSaida = reservaService.atualizarReserva(1L, new ReservaAtualizacao("ativa"));
        //ASSERT
        verify(reserva).setStatus(StatusReserva.ATIVA);
        assertEquals(new ReservaSaida(reserva), reservaSaida);
        assertEquals(StatusReserva.ATIVA, reserva.getStatus());
    }

    @Test
    @DisplayName("Atualização do status da reserva para FINALIZADA, bem sucedida")
    void atualizarReservaCenario2() throws AtualizacaoReservaException {
        //ARRANGE
        Reserva reserva = spy(new Reserva());
        Livro livro  = mock(Livro.class);
        reserva.setStatus(StatusReserva.ATIVA);
        when(reservaRepository.findById(anyLong())).thenReturn(Optional.of(reserva));
        when(reserva.getLivro()).thenReturn(livro);
        //ACT
        var reservaSaida = reservaService.atualizarReserva(1L, new ReservaAtualizacao("finalizada"));
        //ASSERT
        verify(reserva).setStatus(StatusReserva.FINALIZADA);
        verify(livro).removeReserva(any(Reserva.class));
        assertEquals(new ReservaSaida(reserva), reservaSaida);
        assertEquals(StatusReserva.FINALIZADA, reserva.getStatus());
    }

    @Test
    @DisplayName("Atualização do status da reserva para EXPIRADA, bem sucedida")
    void atualizarReservaCenario3() throws AtualizacaoReservaException {
        //ARRANGE
        Reserva reserva = spy(new Reserva());
        Livro livro  = mock(Livro.class);
        reserva.setStatus(StatusReserva.ATIVA);
        when(reservaRepository.findById(anyLong())).thenReturn(Optional.of(reserva));
        when(reserva.getLivro()).thenReturn(livro);
        //ACT
        var reservaSaida = reservaService.atualizarReserva(1L, new ReservaAtualizacao("expirada"));
        //ASSERT
        verify(reserva).setStatus(StatusReserva.EXPIRADA);
        verify(livro).removeReserva(any(Reserva.class));
        assertEquals(new ReservaSaida(reserva), reservaSaida);
        assertEquals(StatusReserva.EXPIRADA, reserva.getStatus());
    }

    @Test
    @DisplayName("Erro: status digitado não existe")
    void atualizarReservaCenario4() {
        //ARRANGE
        Reserva reserva = spy(new Reserva());
        reserva.setStatus(StatusReserva.ATIVA);
        when(reservaRepository.findById(anyLong())).thenReturn(Optional.of(reserva));
        //ACT + ASSERT
        assertThrows(AtualizacaoReservaException.class,
                () -> reservaService.atualizarReserva(1L, new ReservaAtualizacao("expirado")));
    }

    @Test
    @DisplayName("Reserva excluída")
    void excluirReserva() {
        //ACT
        reservaService.excluirReserva(anyLong());
        //ASSERT
        verify(reservaRepository).deleteById(anyLong());
    }

    @Test
    @DisplayName("Status da reserva atualizada para expirada")
    void verificaReservaExpirada() {
        //ARRANGE
        Reserva reserva1 = spy(new Reserva());
        Reserva reserva2 = spy(new Reserva());
        Reserva reserva3 = spy(new Reserva());
        Reserva reserva4 = spy(new Reserva());
        Livro livro = mock(Livro.class);

        reserva1.setStatus(StatusReserva.ATIVA);
        reserva2.setStatus(StatusReserva.PENDENTE);
        reserva3.setStatus(StatusReserva.ATIVA);
        reserva4.setStatus(StatusReserva.EXPIRADA);
        reserva1.setExpiracao(LocalDateTime.now());
        reserva3.setExpiracao(LocalDateTime.now().minusDays(2));

        when(reservaRepository.findByStatusAndExpiracaoGreaterThanEqual(eq(StatusReserva.ATIVA), any(LocalDateTime.class)))
                .thenReturn(List.of(reserva1, reserva3));
        when(reserva1.getLivro()).thenReturn(livro);
        when(reserva3.getLivro()).thenReturn(livro);
        //ACT
        reservaService.verificaReservaExpirada();
        //ASSERT
        verify(reserva1).setStatus(StatusReserva.EXPIRADA);
        verify(reserva3).setStatus(StatusReserva.EXPIRADA);
        verify(livro).removeReserva(reserva1);
        verify(livro).removeReserva(reserva3);
    }
}