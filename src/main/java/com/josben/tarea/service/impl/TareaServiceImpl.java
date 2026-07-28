package com.josben.tarea.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.josben.tarea.entity.Tarea;
import com.josben.tarea.enums.EstadoTarea;
import com.josben.tarea.enums.Prioridad;
import com.josben.tarea.exception.BusinessException;
import com.josben.tarea.repository.TareaRepository;
import com.josben.tarea.service.TareaService;

@Service
@Transactional
public class TareaServiceImpl implements TareaService {

    private final TareaRepository tareaRepository;

    @Autowired
    public TareaServiceImpl(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tarea> listarTareasPorProyecto(Long proyectoId) {
        return tareaRepository.findByProyectoIdOrderByPrioridadAscFechaInicioAscTituloAsc(proyectoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tarea> listarTareasActivasPorProyecto(Long proyectoId) {
        return tareaRepository.findByProyectoIdAndEstadoNotOrderByPrioridadAscFechaInicioAscTituloAsc(
                proyectoId, EstadoTarea.CERRADO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tarea> listarTareasAbiertas() {
        return tareaRepository.findByEstadoNotOrderByPrioridadAscFechaInicioAscProyectoNombreAsc(
                EstadoTarea.CERRADO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tarea> buscarTareas(String consulta, Long proyectoId, EstadoTarea estado, Prioridad prioridad, String orden) {
        String texto = consulta == null ? "" : consulta.trim().toLowerCase();
        Comparator<Tarea> comparador = switch (orden == null ? "fecha" : orden) {
            case "nombre" -> Comparator.comparing(Tarea::getTitulo, String.CASE_INSENSITIVE_ORDER);
            case "prioridad" -> Comparator.comparingInt((Tarea tarea) -> prioridadOrden(tarea.getPrioridad()))
                    .thenComparing(Tarea::getFechaInicio)
                    .thenComparing(Tarea::getTitulo, String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(Tarea::getFechaInicio).reversed()
                    .thenComparing(Tarea::getTitulo, String.CASE_INSENSITIVE_ORDER);
        };

        return tareaRepository.findAll().stream()
                .filter(tarea -> proyectoId == null || tarea.getProyecto().getId().equals(proyectoId))
                .filter(tarea -> estado == null ? tarea.getEstado() != EstadoTarea.CERRADO : tarea.getEstado() == estado)
                .filter(tarea -> prioridad == null || tarea.getPrioridad() == prioridad)
                .filter(tarea -> texto.isBlank()
                        || tarea.getTitulo().toLowerCase().contains(texto)
                        || (tarea.getDescripcion() != null && tarea.getDescripcion().toLowerCase().contains(texto)))
                .sorted(comparador)
                .toList();
    }

    private int prioridadOrden(Prioridad prioridad) {
        return switch (prioridad) {
            case ALTA -> 0;
            case MEDIA -> 1;
            case BAJA -> 2;
        };
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tarea> buscarPorId(Long id) {
        return tareaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tarea> buscarPorIdYProyecto(Long id, Long proyectoId) {
        return tareaRepository.findByIdAndProyectoId(id, proyectoId);
    }

    @Override
    public Tarea crearTarea(Tarea tarea) {
        if (tarea.getProyecto() == null || tarea.getProyecto().getId() == null) {
            throw new BusinessException("El proyecto es obligatorio para crear una tarea");
        }
        return tareaRepository.save(tarea);
    }

    @Override
    public Tarea actualizarTarea(Long id, Tarea tarea) {
        Tarea tareaExistente = buscarPorId(id)
                .orElseThrow(() -> new BusinessException("Tarea no encontrada con ID: " + id));

        tareaExistente.setTitulo(tarea.getTitulo());
        tareaExistente.setDescripcion(tarea.getDescripcion());
        tareaExistente.setEstado(tarea.getEstado());
        tareaExistente.setPrioridad(tarea.getPrioridad());

        return tareaRepository.save(tareaExistente);
    }

    @Override
    public void eliminarTarea(Long id) {
        Tarea tarea = buscarPorId(id)
                .orElseThrow(() -> new BusinessException("Tarea no encontrada con ID: " + id));

        tareaRepository.delete(tarea);
    }

    @Override
    public void cambiarEstado(Long id, EstadoTarea estado) {
        Tarea tarea = buscarPorId(id)
                .orElseThrow(() -> new BusinessException("Tarea no encontrada con ID: " + id));

        tarea.setEstado(estado);
        tareaRepository.save(tarea);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTareasPorProyecto(Long proyectoId) {
        return tareaRepository.countByProyectoId(proyectoId);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTareasAbiertas() {
        return tareaRepository.countByEstadoNot(EstadoTarea.CERRADO);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTareasPorEstado(EstadoTarea estado) {
        return tareaRepository.countByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTareasPorPrioridad(Prioridad prioridad) {
        return tareaRepository.countByPrioridad(prioridad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tarea> listarTareasRecientes() {
        return tareaRepository.findTop5ByOrderByFechaInicioDescIdDesc();
    }
}
