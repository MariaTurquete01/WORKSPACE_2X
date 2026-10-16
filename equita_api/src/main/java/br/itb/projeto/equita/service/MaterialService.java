package br.itb.projeto.equita.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import br.itb.projeto.equita.model.entity.Material;
import br.itb.projeto.equita.model.repository.CursoRepository;
import br.itb.projeto.equita.model.repository.MaterialRepository;
import br.itb.projeto.equita.model.repository.UsuarioRepository;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;

    public MaterialService(MaterialRepository materialRepository,
            CursoRepository cursoRepository,
            UsuarioRepository usuarioRepository) {
        this.materialRepository = materialRepository;
        this.cursoRepository = cursoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /* ================= CREATE ================= */
    public Material create(Material material) {

        material.setId(null);
        validarCurso(material.getCursoId());
        validarVoluntario(material.getVoluntarioId());

        if (material.getTitulo() == null || material.getTitulo().isBlank()) {
            throw new RuntimeException("O título do material é obrigatório");
        }
        if (material.getTipoMaterial() == null || material.getTipoMaterial().isBlank()) {
            throw new RuntimeException("O tipo do material é obrigatório");
        }

        if (material.getStatusAprovacao() == null) {
            material.setStatusAprovacao("PENDENTE");
        }
        // todo material novo nasce não publicado (aparece só após aprovação)
        if (material.getPublicado() == null) {
            material.setPublicado(false);
        }
        material.setDataCadastro(LocalDateTime.now());
        material.setDataAtualizacao(null);

        return materialRepository.save(material);
    }

    /* ================= EDITAR ================= */
    public Material editar(Long id, Material material) {

        Material _material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        validarCurso(material.getCursoId());
        validarVoluntario(material.getVoluntarioId());

        if (material.getTitulo() == null || material.getTitulo().isBlank()) {
            throw new RuntimeException("O título do material é obrigatório");
        }
        if (material.getTipoMaterial() == null || material.getTipoMaterial().isBlank()) {
            throw new RuntimeException("O tipo do material é obrigatório");
        }

        _material.setCursoId(material.getCursoId());
        _material.setVoluntarioId(material.getVoluntarioId());
        _material.setTitulo(material.getTitulo());
        _material.setDescricao(material.getDescricao());
        _material.setTipoMaterial(material.getTipoMaterial());
        _material.setCaminhoArquivo(material.getCaminhoArquivo());
        _material.setLinkExterno(material.getLinkExterno());
        _material.setStatusAprovacao(material.getStatusAprovacao());
        _material.setDataAtualizacao(LocalDateTime.now());

        return materialRepository.save(_material);
    }

    private void validarCurso(Long cursoId) {
        if (cursoId == null || !cursoRepository.existsById(cursoId)) {
            throw new RuntimeException("Curso não encontrado");
        }
    }

    private void validarVoluntario(Long voluntarioId) {
        if (voluntarioId == null || !usuarioRepository.existsById(voluntarioId)) {
            throw new RuntimeException("Usuário voluntário não encontrado");
        }
    }

    /* === DELETE substituído por UPDATE de publicado = 0 === */
    public Material inativar(Long id) {

        Material _material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        _material.setPublicado(false);
        _material.setDataAtualizacao(LocalDateTime.now());

        return materialRepository.save(_material);
    }

    public Material ativar(Long id) {

        Material _material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        _material.setPublicado(true);
        _material.setDataAtualizacao(LocalDateTime.now());

        return materialRepository.save(_material);
    }

    /* ================= CONSULTAS ================= */
    public List<Material> findAll() {
        return materialRepository.findAll();
    }

    public Material findById(Long id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));
    }

    public List<Material> findByCursoId(Long cursoId) {
        return materialRepository.findByCursoId(cursoId);
    }
}
