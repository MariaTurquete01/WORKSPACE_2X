package br.itb.projeto.equita.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.itb.projeto.equita.model.entity.Denuncia;
import br.itb.projeto.equita.model.repository.DenunciaRepository;
import br.itb.projeto.equita.model.repository.InstituicaoRepository;
import br.itb.projeto.equita.model.repository.UsuarioRepository;

@Service
public class DenunciaService {

    private final DenunciaRepository denunciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final InstituicaoRepository instituicaoRepository;

    public DenunciaService(DenunciaRepository denunciaRepository,
            UsuarioRepository usuarioRepository,
            InstituicaoRepository instituicaoRepository) {
        this.denunciaRepository = denunciaRepository;
        this.usuarioRepository = usuarioRepository;
        this.instituicaoRepository = instituicaoRepository;
    }

    /* ================= CREATE ================= */
    public Denuncia create(Denuncia denuncia) {

        denuncia.setId(null);

        validarForeignKeys(denuncia);

        if (denuncia.getTipoDenuncia() == null || denuncia.getTipoDenuncia().isBlank()) {
            throw new RuntimeException("O tipo da denúncia é obrigatório");
        }
        if (denuncia.getDataOcorrencia() == null) {
            throw new RuntimeException("A data da ocorrência é obrigatória");
        }

        if (denuncia.getDenunciaAnonima() == null) {
            denuncia.setDenunciaAnonima(true);
        }
        if (denuncia.getStatus() == null) {
            denuncia.setStatus("RECEBIDA");
        }
        if (denuncia.getAtivo() == null) {
            denuncia.setAtivo(true);
        }

        // protocolo gerado automaticamente: EQ-<32 caracteres>
        denuncia.setProtocolo(gerarProtocolo());
        denuncia.setDataRegistro(LocalDateTime.now());

        return denunciaRepository.save(denuncia);
    }

    /* ================= EDITAR ================= */
    public Denuncia editar(Long id, Denuncia denuncia) {

        Denuncia _denuncia = denunciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Denúncia não encontrada"));

        validarForeignKeys(denuncia);

        if (denuncia.getTipoDenuncia() == null || denuncia.getTipoDenuncia().isBlank()) {
            throw new RuntimeException("O tipo da denúncia é obrigatório");
        }
        if (denuncia.getDataOcorrencia() == null) {
            throw new RuntimeException("A data da ocorrência é obrigatória");
        }

        // protocolo e dataRegistro são gerados no create e não mudam
        _denuncia.setUsuarioId(denuncia.getUsuarioId());
        _denuncia.setInstituicaoResponsavelId(denuncia.getInstituicaoResponsavelId());
        _denuncia.setTipoDenuncia(denuncia.getTipoDenuncia());
        _denuncia.setDescricao(denuncia.getDescricao());
        _denuncia.setDataOcorrencia(denuncia.getDataOcorrencia());
        _denuncia.setLocalOcorrencia(denuncia.getLocalOcorrencia());
        _denuncia.setCidadeOcorrencia(denuncia.getCidadeOcorrencia());
        _denuncia.setEstadoOcorrencia(denuncia.getEstadoOcorrencia());
        _denuncia.setDenunciaAnonima(denuncia.getDenunciaAnonima());
        _denuncia.setStatus(denuncia.getStatus());

        return denunciaRepository.save(_denuncia);
    }

    // valida as FKs opcionais só quando o id veio preenchido
    private void validarForeignKeys(Denuncia denuncia) {

        if (denuncia.getUsuarioId() != null
                && !usuarioRepository.existsById(denuncia.getUsuarioId())) {
            throw new RuntimeException("Usuário não encontrado");
        }
        if (denuncia.getInstituicaoResponsavelId() != null
                && !instituicaoRepository
                        .existsById(denuncia.getInstituicaoResponsavelId())) {
            throw new RuntimeException("Instituição não encontrada");
        }
    }

    // mesmo formato do DEFAULT do banco: 'EQ-' + NEWID() sem hífens
    private String gerarProtocolo() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return "EQ-" + uuid;
    }

    /* ===== DELETE substituído por UPDATE de ativo = 0 ===== */
    public Denuncia inativar(Long id) {

        Denuncia _denuncia = denunciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Denúncia não encontrada"));

        _denuncia.setAtivo(false);

        return denunciaRepository.save(_denuncia);
    }

    public Denuncia ativar(Long id) {

        Denuncia _denuncia = denunciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Denúncia não encontrada"));

        _denuncia.setAtivo(true);

        return denunciaRepository.save(_denuncia);
    }

    /* ================= CONSULTAS ================= */
    public List<Denuncia> findAll() {
        return denunciaRepository.findAll();
    }

    public Denuncia findById(Long id) {
        return denunciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Denúncia não encontrada"));
    }

    // usado pela página "Consultar protocolo" do frontend
    public Denuncia findByProtocolo(String protocolo) {
        return denunciaRepository.findByProtocolo(protocolo)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));
    }
}
