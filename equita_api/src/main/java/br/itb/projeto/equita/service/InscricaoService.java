package br.itb.projeto.equita.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import br.itb.projeto.equita.model.entity.Inscricao;
import br.itb.projeto.equita.model.repository.CursoRepository;
import br.itb.projeto.equita.model.repository.InscricaoRepository;
import br.itb.projeto.equita.model.repository.UsuarioRepository;

@Service
public class InscricaoService {

    private final InscricaoRepository inscricaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;

    public InscricaoService(InscricaoRepository inscricaoRepository,
            UsuarioRepository usuarioRepository,
            CursoRepository cursoRepository) {
        this.inscricaoRepository = inscricaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.cursoRepository = cursoRepository;
    }

    /* ================= CREATE ================= */
    public Inscricao create(Inscricao inscricao) {

        inscricao.setId(null);
        validarUsuario(inscricao.getUsuarioId());
        validarCurso(inscricao.getCursoId());

        // regra de negócio: o mesmo usuário não se inscreve 2x no mesmo curso
        if (inscricaoRepository
                .findByUsuarioIdAndCursoId(inscricao.getUsuarioId(), inscricao.getCursoId())
                .isPresent()) {
            throw new RuntimeException("Usuário já inscrito neste curso");
        }

        inscricao.setDataInscricao(LocalDateTime.now());
        if (inscricao.getPercentualProgresso() == null) {
            inscricao.setPercentualProgresso(BigDecimal.ZERO);
        }
        if (inscricao.getConcluido() == null) {
            inscricao.setConcluido(false);
        }
        if (inscricao.getAtivo() == null) {
            inscricao.setAtivo(true);
        }

        return inscricaoRepository.save(inscricao);
    }

    /* ================= EDITAR ================= */
    public Inscricao editar(Long id, Inscricao inscricao) {

        Inscricao _inscricao = inscricaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));

        validarUsuario(inscricao.getUsuarioId());
        validarCurso(inscricao.getCursoId());

        if (inscricao.getPercentualProgresso() != null
                && (inscricao.getPercentualProgresso().compareTo(BigDecimal.ZERO) < 0
                        || inscricao.getPercentualProgresso().compareTo(new BigDecimal("100")) > 0)) {
            throw new RuntimeException("O percentual de progresso deve estar entre 0 e 100");
        }

        // dataInscricao não muda depois de criada
        _inscricao.setUsuarioId(inscricao.getUsuarioId());
        _inscricao.setCursoId(inscricao.getCursoId());
        _inscricao.setPercentualProgresso(inscricao.getPercentualProgresso());
        _inscricao.setConcluido(inscricao.getConcluido());
        _inscricao.setDataConclusao(inscricao.getDataConclusao());

        return inscricaoRepository.save(_inscricao);
    }

    private void validarUsuario(Long usuarioId) {
        if (usuarioId == null || !usuarioRepository.existsById(usuarioId)) {
            throw new RuntimeException("Usuário não encontrado");
        }
    }

    private void validarCurso(Long cursoId) {
        if (cursoId == null || !cursoRepository.existsById(cursoId)) {
            throw new RuntimeException("Curso não encontrado");
        }
    }

    /* ===== DELETE substituído por UPDATE de ativo = 0 ===== */
    public Inscricao inativar(Long id) {

        Inscricao _inscricao = inscricaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));

        _inscricao.setAtivo(false);

        return inscricaoRepository.save(_inscricao);
    }

    public Inscricao ativar(Long id) {

        Inscricao _inscricao = inscricaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));

        _inscricao.setAtivo(true);

        return inscricaoRepository.save(_inscricao);
    }

    /* ================= CONSULTAS ================= */
    public List<Inscricao> findAll() {
        return inscricaoRepository.findAll();
    }

    public Inscricao findById(Long id) {
        return inscricaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));
    }

    // cursos em que o usuário está inscrito
    public List<Inscricao> findByUsuarioId(Long usuarioId) {
        return inscricaoRepository.findByUsuarioId(usuarioId);
    }
}
