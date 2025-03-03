package com.antoniovictor.biblioteca.services;

import com.antoniovictor.biblioteca.dto.UsuarioAtualizacao;
import com.antoniovictor.biblioteca.dto.UsuarioEntrada;
import com.antoniovictor.biblioteca.dto.UsuarioSaida;
import com.antoniovictor.biblioteca.entities.Usuario;
import com.antoniovictor.biblioteca.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Deve carregar um usuário pelo email com sucesso")
    void loadUserByUsernameCenario1() {
        // ARRANGE
        Usuario usuario = mock(Usuario.class);
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));

        // ACT
        var usuarioSaida = usuarioService.loadUserByUsername("teste@email.com");

        // ASSERT
        assertNotNull(usuarioSaida);
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException ao buscar usuário inexistente")
    void loadUserByUsernameCenario2() {
        // ARRANGE
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UsernameNotFoundException.class, () -> usuarioService.loadUserByUsername("naoexiste@email.com"));
    }

    @Test
    @DisplayName("Deve retornar uma página de usuários")
    void listarCenario1() {
        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);
        Usuario usuario = new Usuario(); // Criando um usuário simulado
        Page<Usuario> paginaUsuarios = new PageImpl<>(List.of(usuario), pageable, 1); // Criando uma página real

        when(usuarioRepository.findAll(pageable)).thenReturn(paginaUsuarios);

        // ACT
        Page<UsuarioSaida> resultado = usuarioService.listar(pageable);

        // ASSERT
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }


    @Test
    @DisplayName("Deve retornar um usuário quando o ID for válido")
    void buscarPorIdCenario1() {
        // ARRANGE
        Usuario usuario = mock(Usuario.class);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // ACT
        UsuarioSaida resultado = usuarioService.buscarPorId(1L);

        // ASSERT
        assertNotNull(resultado);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao buscar usuário com ID inválido")
    void buscarPorIdCenario2() {
        // ARRANGE
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(EntityNotFoundException.class, () -> usuarioService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve atualizar o nome do usuário quando o ID for válido")
    void atualizarCenario1() {
        // ARRANGE
        Usuario usuario = mock(Usuario.class);
        UsuarioAtualizacao usuarioAtualizacao = new UsuarioAtualizacao("Novo Nome", null, null, null);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // ACT
        UsuarioSaida resultado = usuarioService.atualizar(1L, usuarioAtualizacao);

        // ASSERT
        assertNotNull(resultado);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao tentar atualizar um usuário inexistente")
    void atualizarCenario2() {
        // ARRANGE
        UsuarioAtualizacao usuarioAtualizacao = new UsuarioAtualizacao("Novo Nome", null, null, null);
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(EntityNotFoundException.class, () -> usuarioService.atualizar(99L, usuarioAtualizacao));
    }

    @Test
    @DisplayName("Deve remover um usuário quando o ID for válido")
    void removerCenario1() {
        // ARRANGE
        doNothing().when(usuarioRepository).deleteById(1L);

        // ACT & ASSERT
        assertDoesNotThrow(() -> usuarioService.remover(1L));
    }

    @Test
    @DisplayName("Deve bloquear um usuário quando o ID for válido")
    void bloquearCenario1() {
        // ARRANGE
        Usuario usuario = mock(Usuario.class);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // ACT
        usuarioService.bloquear(1L);

        // ASSERT
        verify(usuario).setAtivo(false);
    }

    @Test
    @DisplayName("Deve enviar um email de verificação para o usuário")
    void enviarEmailCenario1() {
        // ARRANGE
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@email.com");

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // ACT
        usuarioService.enviarEmail(usuario);

        // ASSERT
        assertNotNull(usuario.getCodigoVerificacao());
    }

    @Test
    @DisplayName("Deve validar o email corretamente quando o código for válido")
    void validarEmailCenario1() {
        // ARRANGE
        Usuario usuario = new Usuario();
        usuario.setCodigoVerificacao("codigo123");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // ACT
        String resultado = usuarioService.validarEmail(1L, "codigo123");

        // ASSERT
        assertEquals("Email verificado com sucesso", resultado);
    }

    @Test
    @DisplayName("Deve retornar mensagem de erro ao validar email com código inválido")
    void validarEmailCenario2() {
        // ARRANGE
        Usuario usuario = new Usuario();
        usuario.setCodigoVerificacao("codigo123");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // ACT
        String resultado = usuarioService.validarEmail(1L, "codigoErrado");

        // ASSERT
        assertEquals("Código inválido", resultado);
    }
}
