package com.josben.tarea.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.josben.tarea.entity.Comentario;
import com.josben.tarea.enums.EstadoComentario;
import com.josben.tarea.exception.BusinessException;
import com.josben.tarea.repository.ComentarioRepository;
import com.josben.tarea.service.ComentarioService;

@Service
@Transactional
public class ComentarioServiceImpl implements ComentarioService {

    private final ComentarioRepository comentarioRepository;

    @Autowired
    public ComentarioServiceImpl(ComentarioRepository comentarioRepository) {
        this.comentarioRepository = comentarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comentario> listarComentariosPorTarea(Long tareaId) {
        return comentarioRepository.findByTareaIdOrderByFechaDescIdDesc(tareaId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Comentario> buscarPorId(Long id) {
        return comentarioRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Comentario> buscarPorIdYTarea(Long id, Long tareaId) {
        return comentarioRepository.findByIdAndTareaId(id, tareaId);
    }

    @Override
    public Comentario crearComentario(Comentario comentario) {
        if (comentario.getTarea() == null || comentario.getTarea().getId() == null) {
            throw new BusinessException("La tarea es obligatoria para crear un comentario");
        }
        return comentarioRepository.save(comentario);
    }

    @Override
    public Comentario actualizarComentario(Long id, Comentario comentario) {
        Comentario comentarioExistente = buscarPorId(id)
                .orElseThrow(() -> new BusinessException("Comentario no encontrado con ID: " + id));

        comentarioExistente.setTitulo(comentario.getTitulo());
        comentarioExistente.setDescripcion(comentario.getDescripcion());
        comentarioExistente.setEstado(comentario.getEstado());

        return comentarioRepository.save(comentarioExistente);
    }

    @Override
    public void eliminarComentario(Long id) {
        Comentario comentario = buscarPorId(id)
                .orElseThrow(() -> new BusinessException("Comentario no encontrado con ID: " + id));

        comentarioRepository.delete(comentario);
    }

    @Override
    public void cambiarEstado(Long id, EstadoComentario estado) {
        Comentario comentario = buscarPorId(id)
                .orElseThrow(() -> new BusinessException("Comentario no encontrado con ID: " + id));

        comentario.setEstado(estado);
        comentarioRepository.save(comentario);
    }
}
