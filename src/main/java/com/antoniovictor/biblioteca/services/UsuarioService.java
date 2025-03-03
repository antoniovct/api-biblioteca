package com.antoniovictor.biblioteca.services;

import com.antoniovictor.biblioteca.dto.UsuarioAtualizacao;
import com.antoniovictor.biblioteca.dto.UsuarioEntrada;
import com.antoniovictor.biblioteca.dto.UsuarioSaida;
import com.antoniovictor.biblioteca.entities.RoleUsuario;
import com.antoniovictor.biblioteca.entities.Usuario;
import com.antoniovictor.biblioteca.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;
    private final JavaMailSender javaMailSender;

    public UsuarioService(UsuarioRepository usuarioRepository, JavaMailSender javaMailSender) {
        this.usuarioRepository = usuarioRepository;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    @Transactional
    public UsuarioSaida cadastrar(UsuarioEntrada usuarioEntrada) {
        var roleExistente = Arrays.stream(RoleUsuario.values()).anyMatch(role -> role.name().equals(usuarioEntrada.role().toUpperCase()));
        if(roleExistente) {
            var senha = BCrypt.hashpw(usuarioEntrada.senha(), BCrypt.gensalt());
            Usuario usuario = new Usuario(usuarioEntrada, senha);
            usuarioRepository.save(usuario);
            enviarEmail(usuario);
            return new UsuarioSaida(usuario);
        } else {
            throw new IllegalArgumentException("Role não existente, escolha entre admin ou leitor");
        }

    }

    public Page<UsuarioSaida> listar(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(UsuarioSaida::new);
    }

    public UsuarioSaida buscarPorId(long id) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        return new UsuarioSaida(usuario);
    }

    @Transactional
    public UsuarioSaida atualizar(long id,UsuarioAtualizacao usuarioAtualizacao) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        if(usuarioAtualizacao.nome() != null) {
            usuario.setNome(usuarioAtualizacao.nome());
        } else if(usuarioAtualizacao.senha() != null) {
            var senha = BCrypt.hashpw(usuarioAtualizacao.senha(), BCrypt.gensalt());
            usuario.setSenha(senha);
        } else if(usuarioAtualizacao.email() != null) {
            usuario.setEmail(usuarioAtualizacao.email());
        } else if(usuarioAtualizacao.role() != null) {
            var roleExistente = Arrays.stream(RoleUsuario.values()).anyMatch(role -> role.name().equals(usuarioAtualizacao.role()));
            if(roleExistente) {
                usuario.setRole(RoleUsuario.valueOf(usuarioAtualizacao.role().toUpperCase()));
            } else {
                throw new IllegalArgumentException("Role não existente, escolha entre admin ou leitor");
            }
        }
        return new UsuarioSaida(usuario);
    }

    @Transactional
    public void remover(long id) {
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public void bloquear(long id) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        usuario.setAtivo(false);
    }

    @Transactional
    protected void enviarEmail(Usuario usuario) {
        SimpleMailMessage email = new SimpleMailMessage();
        var codigo = UUID.randomUUID().toString();
        usuario.setCodigoVerificacao(codigo);
        email.setTo(usuario.getEmail());
        email.setSubject("Confirmação de email");
        email.setText("Código de verificação: " + codigo);
        javaMailSender.send(email);
    }

    @Transactional
    public String validarEmail(Long idUsuario, String codigo) {
        var usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        if(usuario.getCodigoVerificacao().equals(codigo)) {
            usuario.setEmailVerificado(true);
            return "Email verificado com sucesso";
        } else {
            return "Código inválido";
        }

    }
}
