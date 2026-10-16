package br.itb.projeto.equita.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import br.itb.projeto.equita.model.entity.SolicitacaoVoluntario;
import br.itb.projeto.equita.model.repository.SolicitacaoVoluntarioRepository;
import br.itb.projeto.equita.model.repository.UsuarioRepository;

@Service
public class SolicitacaoVoluntarioService {

    private final SolicitacaoVoluntarioRepository solicitacaoVoluntarioRepository;
    private final UsuarioRepository usuarioRepository;

    public SolicitacaoVoluntarioService(
            SolicitacaoVoluntarioRepository solicitacaoVoluntarioRepository,
            UsuarioRepository usuarioRepository) {
        this.solicitacaoVoluntarioRepository = solicitacaoVoluntarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /* ================= CREATE ================= */
    public SolicitacaoVoluntario create(SolicitacaoVoluntario solicitacao) {

        solicitacao.setId(null);

        if (solicitacao.getUsuarioId() == null
                || !usuarioRepository.existsById(solicitacao.getUsuarioId())) {
            throw new RuntimeException("Usuário não encontrado");
        }
        if (solicitacao.getAdministradorId() != null
                && !usuarioRepository.existsById(solicitacao.getAdministradorId())) {
            throw new RuntimeException("Administrador não encontrado");
        }

        if (solicitacao.getStatus() == null) {
            solicitacao.setStatus("PENDENTE");
        }
        solicitacao.setDataSolicitacao(LocalDateTime.now());
        if (solicitacao.getAtivo() == null) {
            solicitacao.setAtivo(true);
        }

        return solicitacaoVoluntarioRepository.save(solicitacao);
    }

    /* ================= EDITAR ================= */
    public SolicitacaoVoluntario editar(Long id, SolicitacaoVoluntario solicitacao) {

        SolicitacaoVoluntario _solicitacao = solicitacaoVoluntarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));

        if (solicitacao.getUsuarioId() == null
                || !usuarioRepository.existsById(solicitacao.getUsuarioId())) {
            throw new RuntimeException("Usuário não encontrado");
        }
        if (solicitacao.getAdministradorId() != null
                && !usuarioRepository.existsById(solicitacao.getAdministradorId())) {
            throw new RuntimeException("Administrador não encontrado");
        }

        // dataSolicitacao não muda depois de criada
        _solicitacao.setUsuarioId(solicitacao.getUsuarioId());
        _solicitacao.setStatus(solicitacao.getStatus());
        _solicitacao.setJustificativa(solicitacao.getJustificativa());
        _solicitacao.setDataAnalise(solicitacao.getDataAnalise());
        _solicitacao.setAdministradorId(solicitacao.getAdministradorId());
        _solicitacao.setObservacaoAdministrador(solicitacao.getObservacaoAdministrador());

        return solicitacaoVoluntarioRepository.save(_solicitacao);
    }

    /* ===== DELETE substituído por UPDATE de ativo = 0 ===== */
    public SolicitacaoVoluntario inativar(Long id) {

        SolicitacaoVoluntario _solicitacao = solicitacaoVoluntarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));

        _solicitacao.setAtivo(false);

        return solicitacaoVoluntarioRepository.save(_solicitacao);
    }

    public SolicitacaoVoluntario ativar(Long id) {

        SolicitacaoVoluntario _solicitacao = solicitacaoVoluntarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));

        _solicitacao.setAtivo(true);

        return solicitacaoVoluntarioRepository.save(_solicitacao);
    }

    /* ================= CONSULTAS ================= */
    public List<SolicitacaoVoluntario> findAll() {
        return solicitacaoVoluntarioRepository.findAll();
    }

    public SolicitacaoVoluntario findById(Long id) {
        return solicitacaoVoluntarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));
    }

    public List<SolicitacaoVoluntario> findByUsuarioId(Long usuarioId) {
        return solicitacaoVoluntarioRepository.findByUsuarioId(usuarioId);
    }
}
