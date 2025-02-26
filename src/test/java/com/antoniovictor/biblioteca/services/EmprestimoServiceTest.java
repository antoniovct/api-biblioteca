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
import org.junit.jupiter.api.BeforeEach;
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

    private Usuario usuario;
    private Livro livro;
    @InjectMocks
    private EmprestimoService emprestimoService;
    @Mock
    private  EmprestimoRepository emprestimoRepository;
    @Mock
    private  UsuarioRepository usuarioRepository;
    @Mock
    private  LivroRepository livroRepository;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(1L, "Antonio Victor", "victor@admin.com","123", "04274656136", new ArrayList<>(),true,new ArrayList<>());
        livro = new Livro(1L, "O Senhor dos Anéis", "J.R.R. Tolkien", Categoria.FICCAO, 10, true, new ArrayList<>(), new ArrayList<>());
    }

    @Test
    @DisplayName("Verifica se o retorno do método não é nulo, se o empréstimo foi salvo e se os dados estão corretos")
    void novoEmprestimoCenario1() throws CadastroEmprestimoException {
        //ARRANGE
        EmprestimoEntrada emprestimoEntrada = new EmprestimoEntrada(1L, 1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        //ACT
        EmprestimoSaida emprestimoSaida = emprestimoService.novoEmprestimo(emprestimoEntrada);
        //ASSERT
        assertNotNull(emprestimoSaida);
        verify(emprestimoRepository).save(any(Emprestimo.class));
        assertEquals(livro.getTitulo(), emprestimoSaida.livro().getTitulo());
        assertEquals(usuario.getNome(), emprestimoSaida.usuario().getNome());
    }

    @Test
    @DisplayName("Verifica se o método lança exceção ao tentar realizar um empréstimo ao usuário com empréstimo pendente")
    void novoEmprestimoCenario2() {
        //ARRANGE
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
    @DisplayName("Verifica se o método lança exceção ao tentar realizar um empréstimo ao usuário com 2 empréstimos ativos")
    void novoEmprestimoCenario3() {
        //ARRANGE
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
    @DisplayName("Verifica se o método lança exceção ao tentar realizar um empréstimo de um livro com reserva ativa para outro usuário")
    void novoEmprestimoCenario4() {
        //ARRANGE
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        livro.setReservas(List.of(new Reserva(usuario2, livro)));
        livro.getReservas().forEach(r -> r.setStatus(StatusReserva.ATIVA));
        EmprestimoEntrada emprestimoEntrada = new EmprestimoEntrada(1L, 1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        //ACT & ASSERT
        assertThrows(CadastroEmprestimoException.class,
                () -> emprestimoService.novoEmprestimo(emprestimoEntrada));
    }

    @Test
    @DisplayName("Verifica se o método lança exceção por usuário não encontrado")
    void novoEmprestimoCenario5() {
        //ARRANGE
        EmprestimoEntrada emprestimoEntrada = new EmprestimoEntrada(1L, 1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());
        //ACT & ASSERT
        assertThrows(EntityNotFoundException.class,
                () -> emprestimoService.novoEmprestimo(emprestimoEntrada));
    }

    @Test
    @DisplayName("Verifica se o método lança exceção por livro não encontrado")
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
    @DisplayName("Verifica se o método lança exceção por livro não disponível")
    void novoEmprestimoCenario7() {
        //ARRANGE
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
    @DisplayName("Verifica se o retorno do método não é nulo e se a lista de empréstimos está ordenada")
    void listaEmprestimos() {
        // ARRANGE
        var emprestimo1 = new Emprestimo(usuario, livro);
        var emprestimo2 = new Emprestimo(usuario, livro);
        var emprestimo3 = new Emprestimo(usuario, livro);

        emprestimo1.setInicio(LocalDate.now().minusDays(1));
        emprestimo2.setInicio(LocalDate.now().minusDays(2));
        emprestimo3.setInicio(LocalDate.now().minusDays(3));

        Pageable pageable = PageRequest.of(0, 10, Sort.by("inicio"));

        var emprestimosOrdenados = List.of(emprestimo3, emprestimo2, emprestimo1); // Ordenação esperada
        var emprestimosSaidaEsperados = emprestimosOrdenados.stream()
                .map(EmprestimoSaida::new)
                .toList();

        when(emprestimoRepository.findAll(pageable)).thenReturn(new PageImpl<>(emprestimosOrdenados, pageable, 3));

        // ACT
        var emprestimos = emprestimoService.listaEmprestimos(pageable);

        // ASSERT
        assertNotNull(emprestimos);
        assertIterableEquals(emprestimosSaidaEsperados, emprestimos.getContent());
    }


    @Test
    @DisplayName("Verifica se o retorno do método não é nulo e se os dados estão corretos")
    void buscarEmprestimoPorIdCenario1() {
        //ARRANGE
        Emprestimo emprestimo = new Emprestimo();
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        //ACT
        var emprestimoSaida = emprestimoService.buscarEmprestimoPorId(1L);
        //ASSERT
        assertNotNull(emprestimoSaida);
        assertEquals(new EmprestimoSaida(emprestimo), emprestimoSaida);
    }

    @Test
    @DisplayName("Verifica se o método lança exceção por empréstimo não encontrado")
    void buscarEmprestimoPorIdCenario2() {
        //ARRANGE
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.empty());
        //ACT + ASSERT
        assertThrows(EntityNotFoundException.class, () -> emprestimoService.buscarEmprestimoPorId(1L));
    }

    @Test
    @DisplayName("Verifica se o método de renovação é chamado")
    void renovarEmprestimoCenario1() throws RenovacaoEmprestimoException {
        //ARRANGE
        Emprestimo emprestimo = mock(Emprestimo.class);
        when(emprestimo.getLivro()).thenReturn(livro);
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        //ACT
        emprestimoService.renovarEmprestimo(1L);
        //ASSERT
        verify(emprestimo).renovar();
    }

    @Test
    @DisplayName("Verifica se o método lança exceção por reserva pendente")
    void renovarEmprestimoCenario2() {
        //ARRANGE
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setLivro(livro);
        livro.setReservas(List.of(new Reserva()));
        livro.getReservas().forEach(r -> r.setStatus(StatusReserva.PENDENTE));
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        //ACT + ASSERT
        assertThrows(RenovacaoEmprestimoException.class, () -> emprestimoService.renovarEmprestimo(1L));
    }

    @Test
    @DisplayName("Verifica se o estoque do livro é atualizado e o empréstimo finalizado")
    void devolverEmprestimoCenario1() {
        //ARRANGE
        Emprestimo emprestimo = new Emprestimo(usuario, livro);
        livro.addEmprestimo(emprestimo);
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        //ACT
        emprestimoService.devolverEmprestimo(1L);
        //ASSERT
        assertEquals(10, livro.getEstoque());
        assertEquals(StatusEmprestimo.FINALIZADO, emprestimo.getStatus());
    }

    @Test
    @DisplayName("Verifica se a multa é aplicada")
    void devolverEmprestimoCenario2() {
        //ARRANGE
        Emprestimo emprestimo = new Emprestimo(usuario, livro);
        emprestimo.setFim(LocalDate.now().minusDays(5));
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        //ACT
        emprestimoService.devolverEmprestimo(1L);
        //ASSERT
        assertTrue(emprestimo.getMulta() > 0);
        assertEquals(10.0, emprestimo.getMulta());
    }

    @Test
    @DisplayName("Verifica se as reservas são atualizadas")
    void devolverEmprestimoCenario3() {
        //ARRANGE
        Emprestimo emprestimo = new Emprestimo(usuario, livro);
        Reserva reserva1 = new Reserva(usuario, livro);
        Reserva reserva2 = new Reserva(usuario, livro);
        livro.setReservas(List.of(reserva1, reserva2));

        emprestimo.setFim(LocalDate.now());
        livro.setDisponivel(false);
        livro.setEstoque(0);
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
    @DisplayName("Verifica se o método lança exceção por empréstimo não encontrado")
    void devolverEmprestimoCenario4() {
        //ARRANGE
        when(emprestimoRepository.findById(anyLong())).thenReturn(Optional.empty());
        //ACT + ASSERT
        assertThrows(EntityNotFoundException.class, () -> emprestimoService.devolverEmprestimo(1L));
    }

    @Test
    @DisplayName("Verifica se o método de remoção é chamado")
    void removerEmprestimo() {
        //ACT
        emprestimoService.removerEmprestimo(1L);
        //ASSERT
        verify(emprestimoRepository).deleteById(1L);
    }
}