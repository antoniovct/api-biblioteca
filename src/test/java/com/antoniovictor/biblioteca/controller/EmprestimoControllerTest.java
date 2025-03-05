package com.antoniovictor.biblioteca.controller;

import com.antoniovictor.biblioteca.dto.EmprestimoEntrada;
import com.antoniovictor.biblioteca.dto.EmprestimoSaida;
import com.antoniovictor.biblioteca.entities.*;
import com.antoniovictor.biblioteca.error.RenovacaoEmprestimoException;
import com.antoniovictor.biblioteca.repository.EmprestimoRepository;
import com.antoniovictor.biblioteca.repository.LivroRepository;
import com.antoniovictor.biblioteca.repository.UsuarioRepository;
import com.antoniovictor.biblioteca.services.EmprestimoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class EmprestimoControllerTest {


    private MockMvc mockMvc;
    private Livro livro;
    private Usuario usuario;
    private EmprestimoEntrada jsonEntrada;
    private EmprestimoSaida jsonSaida;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private EmprestimoService emprestimoService;
    @InjectMocks
    private EmprestimoController emprestimoController;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private LivroRepository livroRepository;
    @Mock
    private EmprestimoRepository emprestimoRepository;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.standaloneSetup(emprestimoController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper.registerModule(new JavaTimeModule());

        livro = new Livro(1L, "Construção do Eu", "Augusto Cury", Categoria.DRAMA, 10, true,
                List.of(), List.of());
        usuario = new Usuario(1L, "victor", "victor@admin.com", "123", "04274656136", List.of(), true, List.of(), RoleUsuario.ADMIN, false, "123456");

        jsonEntrada = new EmprestimoEntrada(1L, 1L);
        jsonSaida =  new EmprestimoSaida(1L, LocalDate.now(), LocalDate.now().plusWeeks(2), 0.0, livro.getTitulo(), StatusEmprestimo.ATIVO, usuario.getNome());
    }


    @Test
    @DisplayName("Deve retornar um header: Location, um dto de saída e o status 201 created")
    void novoEmprestimo() throws Exception {
        //ARRANGE
        when(emprestimoService.novoEmprestimo(jsonEntrada)).thenReturn(jsonSaida);
        //ACT + ASSERT
        mockMvc.perform(post("/emprestimos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(jsonEntrada)))
                .andExpect(header().exists("Location"))
                .andExpect(content().json(objectMapper.writeValueAsString(jsonSaida)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Verifica se o response contém determinados valores e o status 200")
    void listarEmprestimos() throws Exception {
        //ARRANGE
        Pageable pageable = PageRequest.of(0,20, Sort.by("inicio").ascending());
        Page<EmprestimoSaida> pageSaida = new PageImpl<>(List.of(
                new EmprestimoSaida(1L, LocalDate.now().minusDays(5), LocalDate.now().minusDays(5).plusWeeks(2), 0.0, livro.getTitulo(), StatusEmprestimo.ATIVO, usuario.getNome()),
                new EmprestimoSaida(2L, LocalDate.now(), LocalDate.now().plusWeeks(2), 0.0, livro.getTitulo(), StatusEmprestimo.ATIVO, usuario.getNome())),pageable,2);
        when(emprestimoService.listaEmprestimos(pageable)).thenReturn(pageSaida);
        //ACT + ASSERT
        mockMvc.perform(get("/emprestimos"))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Verifica se retorna o empréstimo com o id correto e o status 200")
    void buscarEmprestimoPorId() throws Exception {
        //ARRANGE
        when(emprestimoService.buscarEmprestimoPorId(1L)).thenReturn(jsonSaida);
        //ACT + ASSERT
        mockMvc.perform(get("/emprestimos/emprestimo/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Verifica se retorna a mensagem de sucesso e o status 200")
    void renovarEmprestimoCenario1() throws Exception {
        //ACT + ASSERT
        mockMvc.perform(put("/emprestimos/emprestimo/1/renovar"))
                .andExpect(content().string("Empréstimo renovado com sucesso!"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Verifica se retorna mensagem de erro e o status 500")
    void renovarEmprestimoCenario2() throws Exception {
        //ARRANGE
        doThrow(new RenovacaoEmprestimoException("Erro ao renovar empréstimo")).when(emprestimoService).renovarEmprestimo(1L);
        //ACT + ASSERT
        mockMvc.perform(put("/emprestimos/emprestimo/1/renovar"))
                .andExpect(content().string("Erro ao renovar empréstimo"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Verifica se retorna a mensagem de sucesso e o status 200")
    void devolverEmprestimo() throws Exception {
        //ACT + ASSERT
        mockMvc.perform(patch("/emprestimos/emprestimo/1/devolucao"))
                .andExpect(content().string("Empréstimo devolvido com sucesso!"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Verifica se retorna o status 204")
    void excluirEmprestimo() throws Exception {
        //ACT + ASSERT
        mockMvc.perform(delete("/emprestimos/emprestimo/1"))
                .andExpect(status().isNoContent());
    }
}