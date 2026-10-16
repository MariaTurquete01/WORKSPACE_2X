package br.itb.projeto.equita.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import br.itb.projeto.equita.model.entity.Instituicao;
import br.itb.projeto.equita.model.repository.CategoriaInstituicaoRepository;
import br.itb.projeto.equita.model.repository.InstituicaoRepository;

@Service
public class InstituicaoService {

    private final InstituicaoRepository instituicaoRepository;
    private final CategoriaInstituicaoRepository categoriaInstituicaoRepository;

    public InstituicaoService(InstituicaoRepository instituicaoRepository,
            CategoriaInstituicaoRepository categoriaInstituicaoRepository) {
        this.instituicaoRepository = instituicaoRepository;
        this.categoriaInstituicaoRepository = categoriaInstituicaoRepository;
    }

    /* ================= CREATE ================= */
    public Instituicao create(Instituicao instituicao) {

        instituicao.setId(null);

        if (instituicao.getNome() == null || instituicao.getNome().isBlank()) {
            throw new RuntimeException("O nome da instituição é obrigatório");
        }
        if (instituicao.getCategoriaInstituicaoId() == null
                || !categoriaInstituicaoRepository
                        .existsById(instituicao.getCategoriaInstituicaoId())) {
            throw new RuntimeException("Categoria de instituição não encontrada");
        }

        if (instituicao.getAtiva() == null) {
            instituicao.setAtiva(true);
        }
        instituicao.setDataCadastro(LocalDateTime.now());
        instituicao.setDataAtualizacao(null);

        return instituicaoRepository.save(instituicao);
    }

    /* ================= EDITAR ================= */
    public Instituicao editar(Long id, Instituicao instituicao) {

        Instituicao _instituicao = instituicaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada"));

        if (instituicao.getNome() == null || instituicao.getNome().isBlank()) {
            throw new RuntimeException("O nome da instituição é obrigatório");
        }
        if (instituicao.getCategoriaInstituicaoId() == null
                || !categoriaInstituicaoRepository
                        .existsById(instituicao.getCategoriaInstituicaoId())) {
            throw new RuntimeException("Categoria de instituição não encontrada");
        }

        _instituicao.setCategoriaInstituicaoId(instituicao.getCategoriaInstituicaoId());
        _instituicao.setNome(instituicao.getNome());
        _instituicao.setDescricao(instituicao.getDescricao());
        _instituicao.setEndereco(instituicao.getEndereco());
        _instituicao.setCidade(instituicao.getCidade());
        _instituicao.setEstado(instituicao.getEstado());
        _instituicao.setCep(instituicao.getCep());
        _instituicao.setTelefone(instituicao.getTelefone());
        _instituicao.setEmail(instituicao.getEmail());
        _instituicao.setHorarioFuncionamento(instituicao.getHorarioFuncionamento());
        _instituicao.setLatitude(instituicao.getLatitude());
        _instituicao.setLongitude(instituicao.getLongitude());
        _instituicao.setDataAtualizacao(LocalDateTime.now());

        return instituicaoRepository.save(_instituicao);
    }

    /* ===== DELETE substituído por UPDATE de ativa = 0 ===== */
    public Instituicao inativar(Long id) {

        Instituicao _instituicao = instituicaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada"));

        _instituicao.setAtiva(false);
        _instituicao.setDataAtualizacao(LocalDateTime.now());

        return instituicaoRepository.save(_instituicao);
    }

    public Instituicao ativar(Long id) {

        Instituicao _instituicao = instituicaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada"));

        _instituicao.setAtiva(true);
        _instituicao.setDataAtualizacao(LocalDateTime.now());

        return instituicaoRepository.save(_instituicao);
    }

    /* ================= CONSULTAS ================= */
    public List<Instituicao> findAll() {
        return instituicaoRepository.findAll();
    }

    public Instituicao findById(Long id) {
        return instituicaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instituição não encontrada"));
    }

    public List<Instituicao> findByCidade(String cidade) {
        return instituicaoRepository.findByCidade(cidade);
    }
}
