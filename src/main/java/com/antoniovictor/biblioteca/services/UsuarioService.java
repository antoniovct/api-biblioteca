package com.antoniovictor.biblioteca.services;

import com.antoniovictor.biblioteca.dto.UsuarioAtualizacao;
import com.antoniovictor.biblioteca.dto.UsuarioEntrada;
import com.antoniovictor.biblioteca.dto.UsuarioSaida;
import com.antoniovictor.biblioteca.entities.Usuario;
import com.antoniovictor.biblioteca.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    @Transactional
    public UsuarioSaida cadastrar(UsuarioEntrada usuarioEntrada) {
        var senha = BCrypt.hashpw(usuarioEntrada.senha(), BCrypt.gensalt());
        Usuario usuario = new Usuario(usuarioEntrada, senha);
        usuarioRepository.save(usuario);
        return new UsuarioSaida(usuario);
    }

    public List<UsuarioSaida> listar() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioSaida::new).toList();
    }

    public UsuarioSaida buscarPorId(long id) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return new UsuarioSaida(usuario);
    }

    @Transactional
    public UsuarioSaida atualizar(long id,UsuarioAtualizacao usuarioAtualizacao) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        if(usuarioAtualizacao.nome() != null) {
            usuario.setNome(usuarioAtualizacao.nome());
        } else if(usuarioAtualizacao.senha() != null) {
            var senha = BCrypt.hashpw(usuarioAtualizacao.senha(), BCrypt.gensalt());
            usuario.setSenha(senha);
        } else if(usuarioAtualizacao.email() != null) {
            usuario.setEmail(usuarioAtualizacao.email());
        }
        return new UsuarioSaida(usuario);
    }

    @Transactional
    public void remover(long id) {
        usuarioRepository.deleteById(id);
    }
}
