package com.josben.tarea.service;

import com.josben.tarea.entity.Comentario;
import com.josben.tarea.enums.EstadoComentario;

import java.util.List;
import java.util.Optional;

public interface ComentarioService {

    List<Comentario> listarComentariosPorTarea(Long tareaId);

    Optional<Comentario> buscarPorId(Long id);

    Optional<Comentario> buscarPorIdYTarea(Long id, Long tareaId);

    Comentario crearComentario(Comentario comentario);

    Comentario actualizarComentario(Long id, Comentario comentario);

    void eliminarComentario(Long id);

    void cambiarEstado(Long id, EstadoComentario estado);
}
