package br.itb.projeto.equita.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import br.itb.projeto.equita.model.entity.Curso;
import br.itb.projeto.equita.model.repository.CursoRepository;
import br.itb.projeto.equita.model.repository.UsuarioRepository;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;

    public CursoService(CursoRepository cursoRepository,
            UsuarioRepository usuarioRepository) {
        this.cursoRepository = cursoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /* ================= CREATE ================= */
    public Curso create(Curso curso) {

        curso.setId(null);
        validarVoluntario(curso.getVoluntarioId());

        if (curso.getTitulo() == null || curso.getTitulo().isBlank()) {
            throw new RuntimeException("O título do curso é obrigatório");
        }

        if (curso.getStatusAprovacao() == null) {
            curso.setStatusAprovacao("PENDENTE");
        }
        // todo curso novo nasce não publicado (aparece só após aprovação)
        if (curso.getPublicado() == null) {
            curso.setPublicado(false);
        }
        curso.setDataCadastro(LocalDateTime.now());
        curso.setDataAtualizacao(null);

        return cursoRepository.save(curso);
    }

    /* ================= EDITAR ================= */
    public Curso editar(Long id, Curso curso) {

        Curso _curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        validarVoluntario(curso.getVoluntarioId());

        if (curso.getTitulo() == null || curso.getTitulo().isBlank()) {
            throw new RuntimeException("O título do curso é obrigatório");
        }

        _curso.setVoluntarioId(curso.getVoluntarioId());
        _curso.setTitulo(curso.getTitulo());
        _curso.setDescricao(curso.getDescricao());
        _curso.setCargaHoraria(curso.getCargaHoraria());
        _curso.setStatusAprovacao(curso.getStatusAprovacao());
        _curso.setDataAtualizacao(LocalDateTime.now());

        return cursoRepository.save(_curso);
    }

    private void validarVoluntario(Long voluntarioId) {
        if (voluntarioId == null || !usuarioRepository.existsById(voluntarioId)) {
            throw new RuntimeException("Usuário voluntário não encontrado");
        }
    }

    /* === DELETE substituído por UPDATE de publicado = 0 === */
    public Curso inativar(Long id) {

        Curso _curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        _curso.setPublicado(false);
        _curso.setDataAtualizacao(LocalDateTime.now());

        return cursoRepository.save(_curso);
    }

    public Curso ativar(Long id) {

        Curso _curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        _curso.setPublicado(true);
        _curso.setDataAtualizacao(LocalDateTime.now());

        return cursoRepository.save(_curso);
    }

    /* ================= CONSULTAS ================= */
    public List<Curso> findAll() {
        return cursoRepository.findAll();
    }

    public Curso findById(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));
    }

    // catálogo: só cursos publicados
    public List<Curso> findPublicados() {
        return cursoRepository.findByPublicadoTrue();
    }

    public List<Curso> findByVoluntarioId(Long voluntarioId) {
        return cursoRepository.findByVoluntarioId(voluntarioId);
    }
}
