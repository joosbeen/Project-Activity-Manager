package com.josben.tarea.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.josben.tarea.entity.Proyecto;
import com.josben.tarea.enums.EstadoProyecto;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    Optional<Proyecto> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<Proyecto> findByEstadoOrderByNombreAsc(EstadoProyecto estado);

    List<Proyecto> findAllByOrderByEstadoAscNombreAsc();

    long countByEstadoNot(EstadoProyecto estado);

    long countByEstado(EstadoProyecto estado);

    List<Proyecto> findTop5ByOrderByFechaCreacionDescIdDesc();
}
