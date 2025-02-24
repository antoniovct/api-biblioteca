package com.antoniovictor.biblioteca.services;

import com.antoniovictor.biblioteca.dto.UsuarioAtualizacao;
import com.antoniovictor.biblioteca.dto.UsuarioEntrada;
import com.antoniovictor.biblioteca.entities.Usuario;
import com.antoniovictor.biblioteca.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;
    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Verifica se a saída do método não é nula")
    void loadUserByUsernameCenario1() {
        //ARRANGE
        Usuario usuario = mock(Usuario.class);
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        //ACT
        var usuarioSaida = usuarioService.loadUserByUsername(anyString());
        //ASSERT
        assertNotNull(usuarioSaida);
    }

    @Test
    @DisplayName("Erro: usuario não encontrado")
    void loadUserByUsernameCenario2() {
        //ARRANGE
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        //ACT + ASSERT
        assertThrows(UsernameNotFoundException.class, () -> usuarioService.loadUserByUsername(anyString()));
    }

    @Test
    @DisplayName("Verifica se o método save do repositório do usuario foi chamado e se a saída não é nula")
    void cadastrar() {
        //ARRANGE
        UsuarioEntrada usuarioEntrada = mock(UsuarioEntrada.class);
        when(usuarioEntrada.senha()).thenReturn("123456");
        //ACT
        var usuarioSaida = usuarioService.cadastrar(usuarioEntrada);
        //ASSERT
        verify(usuarioRepository).save(new Usuario());
        assertNotNull(usuarioSaida);
    }

    @Test
    @DisplayName("Verifica se a saída do método não é nula")
    void listar() {
        //ARRANGE
        Pageable pageable = PageRequest.of(0,10);
        when(usuarioRepository.findAll(pageable)).thenReturn(new PageImpl<Usuario>(new ArrayList<>()));
        //ACT
        var usuarioSaida = usuarioService.listar(pageable);
        //ASSERT
        assertNotNull(usuarioSaida);
    }


    @Test
    @DisplayName("Verifica se a saída do método não é nula e se o método setSenha foi chamado")
    void atualizar() {
        //ARRANGE
        Usuario usuario = mock(Usuario.class);
        UsuarioAtualizacao usuarioAtualizacao = mock(UsuarioAtualizacao.class);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioAtualizacao.senha()).thenReturn("123456");
        //ACT
        var usuarioSaida = usuarioService.atualizar(1L, usuarioAtualizacao);
        //ASSERT
        assertNotNull(usuarioSaida);
        verify(usuario).setSenha(anyString());
    }

}