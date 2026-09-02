package com.josben.tarea.repository;

import com.josben.tarea.entity.Comentario;
import com.josben.tarea.enums.EstadoComentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    List<Comentario> findByTareaIdOrderByFechaDescIdDesc(Long tareaId);

    Optional<Comentario> findByIdAndTareaId(Long id, Long tareaId);

    List<Comentario> findByTareaIdAndEstado(Long tareaId, EstadoComentario estado);
}
