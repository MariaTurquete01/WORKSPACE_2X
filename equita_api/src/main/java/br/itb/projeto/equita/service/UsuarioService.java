package br.itb.projeto.equita.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.itb.projeto.equita.dto.UsuarioDTO;
import br.itb.projeto.equita.model.entity.Usuario;
import br.itb.projeto.equita.model.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* ================= CREATE ================= */
    public Usuario create(Usuario usuario) {

        usuario.setId(null);

        if (usuario.getUsername() != null
                && usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new RuntimeException("Username já cadastrado");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        if (usuario.getNivelAcesso() == null) {
            usuario.setNivelAcesso("USER");
        }
        usuario.setStatusUsuario("ATIVO");
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setDataAtualizacao(null);

        return usuarioRepository.save(usuario);
    }

    /* ================= EDITAR ================= */
    public Usuario editar(Long id, Usuario usuario) {

        Usuario _usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (usuario.getUsername() != null
                && !usuario.getUsername().equals(_usuario.getUsername())
                && usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new RuntimeException("Username já cadastrado");
        }

        _usuario.setNome(usuario.getNome());
        _usuario.setUsername(usuario.getUsername());
        _usuario.setNivelAcesso(usuario.getNivelAcesso());
        // a senha não é alterada por aqui → usar /alterar-senha
        _usuario.setDataAtualizacao(LocalDateTime.now());

        return usuarioRepository.save(_usuario);
    }

    /* ================= ALTERAR SENHA ================= */
    public Usuario alterarSenha(Long id, String novaSenha) {

        Usuario _usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        _usuario.setPassword(passwordEncoder.encode(novaSenha));
        if ("TROCAR_SENHA".equals(_usuario.getStatusUsuario())) {
            _usuario.setStatusUsuario("ATIVO");
        }
        _usuario.setDataAtualizacao(LocalDateTime.now());

        return usuarioRepository.save(_usuario);
    }

    /* ========= DELETE substituído por UPDATE de statusUsuario ========= */
    public Usuario inativar(Long id) {

        Usuario _usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        _usuario.setStatusUsuario("INATIVO");
        _usuario.setDataAtualizacao(LocalDateTime.now());

        return usuarioRepository.save(_usuario);
    }

    public Usuario ativar(Long id) {

        Usuario _usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        _usuario.setStatusUsuario("ATIVO");
        _usuario.setDataAtualizacao(LocalDateTime.now());

        return usuarioRepository.save(_usuario);
    }

    /* ================= LOGIN ================= */
    // valida credenciais no banco (bcrypt) e devolve o DTO sem senha
    public UsuarioDTO login(String username, String password) {

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new RuntimeException("Informe e-mail e senha");
        }

        Usuario usuario = usuarioRepository.findByUsername(username.trim())
                .orElseThrow(() -> new RuntimeException("Usuário ou senha inválidos"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Usuário ou senha inválidos");
        }
        if (!"ATIVO".equals(usuario.getStatusUsuario())) {
            throw new RuntimeException("Usuário inativo. Contate o administrador.");
        }

        return toDTO(usuario);
    }

    /* ================= CONSULTAS ================= */
    public List<UsuarioDTO> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public UsuarioDTO findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return toDTO(usuario);
    }

    // o DTO esconde a senha (nunca devolve o hash para o frontend)
    private UsuarioDTO toDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getUsername(),
                usuario.getNivelAcesso(),
                usuario.getDataCadastro(),
                usuario.getStatusUsuario()
        );
    }
}
