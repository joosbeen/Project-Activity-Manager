package com.josben.tarea.service;

import java.util.List;
import java.util.Optional;

import com.josben.tarea.entity.Proyecto;
import com.josben.tarea.enums.EstadoProyecto;

public interface ProyectoService {

    List<Proyecto> listarProyectos();

    List<Proyecto> buscarProyectos(String consulta, EstadoProyecto estado, String orden);

    Optional<Proyecto> buscarPorId(Long id);

    Proyecto crearProyecto(Proyecto proyecto);

    Proyecto actualizarProyecto(Long id, Proyecto proyecto);

    void eliminarProyecto(Long id);

    void cambiarEstado(Long id, EstadoProyecto estado);

    boolean existePorNombre(String nombre);

    boolean existePorNombreConIdDiferente(String nombre, Long id);

    long contarProyectosActivos();

    long contarProyectosPorEstado(EstadoProyecto estado);

    List<Proyecto> listarProyectosRecientes();
}
