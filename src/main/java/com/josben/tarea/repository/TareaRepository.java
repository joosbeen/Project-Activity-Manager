package com.josben.tarea.repository;

import com.josben.tarea.entity.Tarea;
import com.josben.tarea.enums.EstadoTarea;
import com.josben.tarea.enums.Prioridad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    List<Tarea> findByProyectoId(Long proyectoId);

    List<Tarea> findByProyectoIdOrderByPrioridadAscFechaInicioAscTituloAsc(Long proyectoId);

    List<Tarea> findByProyectoIdAndEstadoNotOrderByPrioridadAscFechaInicioAscTituloAsc(Long proyectoId, EstadoTarea estado);

    Optional<Tarea> findByIdAndProyectoId(Long id, Long proyectoId);

    long countByProyectoId(Long proyectoId);

    List<Tarea> findByEstadoNotOrderByPrioridadAscFechaInicioAscProyectoNombreAsc(EstadoTarea estado);

    long countByEstadoNot(EstadoTarea estado);

    long countByEstado(EstadoTarea estado);

    long countByPrioridad(Prioridad prioridad);

    List<Tarea> findTop5ByOrderByFechaInicioDescIdDesc();

    @Query("SELECT t FROM Tarea t WHERE t.estado = 'CERRADO' AND t.fechaCierre >= :fechaInicio ORDER BY t.fechaCierre DESC")
    List<Tarea> findTareasCerradasRecientes(@Param("fechaInicio") LocalDate fechaInicio);
}
