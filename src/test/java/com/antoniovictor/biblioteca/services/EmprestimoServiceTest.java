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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

    @InjectMocks
    private EmprestimoService emprestimoService;
    @Mock
    private  EmprestimoRepository emprestimoRepository;
    @Mock
    private  UsuarioRepository usuarioRepository;
    @Mock
    private  LivroRepository livroRepository;

    @Test
    @DisplayName("Empréstimo bem sucedido")
    void novoEmprestimoCenario1() throws CadastroEmprestimoException {
        //ARRANGE
        Usuario usuario = new Usuario();
        Livro livro = new Livro();
        livro.setDisponivel(true);
        livro.setEstoque(1);
        EmprestimoEntrada emprestimoEntrada = new EmprestimoEntrada(1l,1l);
        when(usuarioRepository.findById(1l)).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(1l)).thenReturn(Optional.of(livro));
        //ACT
        EmprestimoSaida emprestimoSaida = emprestimoService.novoEmprestimo(emprestimoEntrada);
        //ASSERT
        assertNotNull(emprestimoSaida);
        verify(emprestimoRepository).save(any(Emprestimo.class));
    }

    @Test
    @DisplayName("Erro: Usuário tem empréstimos pendentes")
    void novoEmprestimoCenario2() {
        //ARRANGE
        Usuario usuario = new Usuario();
        Livro livro = new Livro();
        livro.setDisponivel(true);
        livro.setEstoque(1);
        usuario.setEmprestimos(List.of(new Emprestimo(usuario, livro)));
        usuario.getEmprestimos().getFirst().setStatus(StatusEmprestimo.PENDENTE);
        EmprestimoEntrada emprestimoEntrada = new EmprestimoEntrada(1L, 1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        //ACT & ASSERT
        assertThrows(CadastroEmprestimoException.class,
                () -> emprestimoService.novoEmprestimo(emprestimoEntrada));
    }

    @Test
    @DisplayName("Erro: Usuário já tem dois empréstimos ativos")
    void novoEmprestimoCenario3() {
        //ARRANGE
        Usuario usuario = new Usuario();
        Livro livro = new Livro();
        livro.setDisponivel(true);
        livro.setEstoque(1);
        usuario.setEmprestimos(List.of(new Emprestimo(usuario, livro), new Emprestimo(usuario, livro)));
        usuario.getEmprestimos().forEach(e -> e.setStatus(StatusEmprestimo.ATIVO));
        EmprestimoEntrada emprestimoEntrada = new EmprestimoEntrada(1L, 1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        //ACT & ASSERT
        assertThrows(CadastroEmprestimoException.class,
                () -> emprestimoService.novoEmprestimo(emprestimoEntrada));
    }

    @Test
    @DisplayName("Erro: Reserva ativa pertence a outro usuário")
    void novoEmprestimoCenario4() {
        //ARRANGE
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        Livro livro = new Livro();
        livro.setDisponivel(true);
        livro.setEstoque(1);
        livro.setReservas(List.of(new Reserva(usuario2, livro)));
        livro.getReservas().forEach(r -> r.setStatus(StatusReserva.ATIVA));
        EmprestimoEntrada emprestimoEntrada = new EmprestimoEntrada(1L, 1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario1));
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        //ACT & ASSERT
        assertThrows(CadastroEmprestimoException.class,
                () -> emprestimoService.novoEmprestimo(emprestimoEntrada));
    }

    @Test
    @DisplayName("Erro: Usuário não encontrado")
    void novoEmprestimoCenario5() {
        //ARRANGE
        EmprestimoEntrada emprestimoEntrada = new EmprestimoEntrada(1L, 1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());
        //ACT & ASSERT
        assertThrows(EntityNotFoundException.class,
                () -> emprestimoService.novoEmprestimo(emprestimoEntrada));
    }

    @Test
    @DisplayName("Erro: Livro não encontrado")
    void novoEmprestimoCenario6() {
        //ARRANGE
        EmprestimoEntrada emprestimoEntrada = new EmprestimoEntrada(1L, 1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(new Usuario()));
        when(livroRepository.findById(1L)).thenReturn(Optional.empty());
        //ACT & ASSERT
        assertThrows(EntityNotFoundException.class,
                () -> emprestimoService.novoEmprestimo(emprestimoEntrada));
    }

    @Test
    @DisplayName("Erro: Livro não está disponível")
    void novoEmprestimoCenario7() {
        //ARRANGE
        Usuario usuario = new Usuario();
        Livro livro = new Livro();
        livro.setDisponivel(false);
        livro.setEstoque(0);
        EmprestimoEntrada emprestimoEntrada = new EmprestimoEntrada(1L, 1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        //ACT & ASSERT
        assertThrows(CadastroEmprestimoException.class,
                () -> emprestimoService.novoEmprestimo(emprestimoEntrada));
    }

    @Test
    @DisplayName("Verifica se o retorno do método não é nulo")
    void listaEmprestimos() {
        //ARRANGE
        Pageable pageable = PageRequest.of(0, 10, Sort.by("inicio"));
        when(emprestimoRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of()));
        //ACT
        var emprestimos = emprestimoService.listaEmprestimos(pageable);
        //ASSERT
        assertNotNull(emprestimos);
    }

    @Test
    @DisplayName("Empréstimo com id solicitado")
    void buscarEmprestimoPorIdCenario1() {
        //ARRANGE
        Emprestimo emprestimo = new Emprestimo();
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        //ACT
        var emprestimoSaida = emprestimoService.buscarEmprestimoPorId(1L);
        //ASSERT
        assertEquals(new EmprestimoSaida(emprestimo), emprestimoSaida);
    }

    @Test
    @DisplayName("Erro: Empréstimo não encontrado")
    void buscarEmprestimoPorIdCenario2() {
        //ARRANGE
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.empty());
        //ACT + ASSERT
        assertThrows(EntityNotFoundException.class, () -> emprestimoService.buscarEmprestimoPorId(1L));
    }

    @Test
    @DisplayName("Renovação bem sucedida")
    void renovarEmprestimoCenario1() throws RenovacaoEmprestimoException {
        //ARRANGE
        Emprestimo emprestimo = mock(Emprestimo.class);
        Livro livro = new Livro();
        when(emprestimo.getLivro()).thenReturn(livro);
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        //ACT
        emprestimoService.renovarEmprestimo(1L);
        //ASSERT
        verify(emprestimo).renovar();
    }

    @Test
    @DisplayName("Erro: reserva ativa para o livro")
    void renovarEmprestimoCenario2() throws RenovacaoEmprestimoException {
        //ARRANGE
        Emprestimo emprestimo = new Emprestimo();
        Livro livro = new Livro();
        emprestimo.setLivro(livro);
        livro.setReservas(List.of(new Reserva()));
        livro.getReservas().forEach(r -> r.setStatus(StatusReserva.PENDENTE));
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        //ACT + ASSERT
        assertThrows(RenovacaoEmprestimoException.class, () -> emprestimoService.renovarEmprestimo(1L));
    }

    @Test
    @DisplayName("Devolução bem sucedida")
    void devolverEmprestimoCenario1() {
        //ARRANGE
        Emprestimo emprestimo = mock(Emprestimo.class);
        Livro livro = mock(Livro.class);
        when(livro.getDisponivel()).thenReturn(true);
        when(emprestimo.getLivro()).thenReturn(livro);
        when(emprestimo.getFim()).thenReturn(LocalDate.now());
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        //ACT
        emprestimoService.devolverEmprestimo(1L);
        //ASSERT
        verify(emprestimo).devolver();
        verify(livro).setEstoque(anyInt());
    }

    @Test
    @DisplayName("Geração de multa")
    void devolverEmprestimoCenario2() {
        //ARRANGE
        Emprestimo emprestimo = mock(Emprestimo.class);
        Livro livro = mock(Livro.class);
        when(livro.getDisponivel()).thenReturn(true);
        when(emprestimo.getLivro()).thenReturn(livro);
        when(emprestimo.getFim()).thenReturn(LocalDate.now().minusDays(5));
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        //ACT
        emprestimoService.devolverEmprestimo(1L);
        //ASSERT
        verify(emprestimo).setMulta(10.0);
    }

    @Test
    @DisplayName("Ativação automática da reserva")
    void devolverEmprestimoCenario3() {
        //ARRANGE
        Emprestimo emprestimo = new Emprestimo();
        Livro livro = new Livro();
        Reserva reserva1 = new Reserva();
        Reserva reserva2 = new Reserva();

        emprestimo.setFim(LocalDate.now());
        emprestimo.setLivro(livro);
        livro.setDisponivel(false);
        livro.setEstoque(0);
        livro.setReservas(List.of(reserva1, reserva2));
        livro.getReservas().forEach(r -> r.setStatus(StatusReserva.PENDENTE));
        reserva1.setData(LocalDateTime.now().minusDays(1));
        reserva2.setData(LocalDateTime.now());

        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        //ACT
        emprestimoService.devolverEmprestimo(1L);
        //ASSERT
        assertEquals(StatusReserva.ATIVA, reserva1.getStatus());
        assertEquals(StatusReserva.PENDENTE, reserva2.getStatus());
    }

    @Test
    @DisplayName("Erro: Empréstimo não encontrado")
    void devolverEmprestimoCenario4() {
        //ARRANGE
        when(emprestimoRepository.findById(anyLong())).thenReturn(Optional.empty());
        //ACT + ASSERT
        assertThrows(EntityNotFoundException.class, () -> emprestimoService.devolverEmprestimo(1L));
    }

    @Test
    @DisplayName("Método delete é chamado")
    void removerEmprestimo() {
        //ACT
        emprestimoService.removerEmprestimo(1L);
        //ASSERT
        verify(emprestimoRepository).deleteById(1L);
    }
}