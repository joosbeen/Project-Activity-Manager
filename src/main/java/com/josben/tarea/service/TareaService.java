package com.josben.tarea.service;

import java.util.List;
import java.util.Optional;

import com.josben.tarea.entity.Tarea;
import com.josben.tarea.enums.EstadoTarea;
import com.josben.tarea.enums.Prioridad;

public interface TareaService {

    List<Tarea> listarTareasPorProyecto(Long proyectoId);

    List<Tarea> listarTareasActivasPorProyecto(Long proyectoId);

    List<Tarea> listarTareasAbiertas();

    List<Tarea> buscarTareas(String consulta, Long proyectoId, EstadoTarea estado, Prioridad prioridad, String orden);

    Optional<Tarea> buscarPorId(Long id);

    Optional<Tarea> buscarPorIdYProyecto(Long id, Long proyectoId);

    Tarea crearTarea(Tarea tarea);

    Tarea actualizarTarea(Long id, Tarea tarea);

    void eliminarTarea(Long id);

    void cambiarEstado(Long id, EstadoTarea estado);

    long contarTareasPorProyecto(Long proyectoId);

    long contarTareasAbiertas();

    long contarTareasPorEstado(EstadoTarea estado);

    long contarTareasPorPrioridad(Prioridad prioridad);

    List<Tarea> listarTareasRecientes();

    List<Tarea> listarTareasCerradasRecientes();
}
