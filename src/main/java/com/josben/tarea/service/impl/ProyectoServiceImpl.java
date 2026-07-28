package com.josben.tarea.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.josben.tarea.entity.Proyecto;
import com.josben.tarea.enums.EstadoProyecto;
import com.josben.tarea.exception.BusinessException;
import com.josben.tarea.repository.ProyectoRepository;
import com.josben.tarea.service.ProyectoService;

@Service
@Transactional
public class ProyectoServiceImpl implements ProyectoService {

    private final ProyectoRepository proyectoRepository;

    @Autowired
    public ProyectoServiceImpl(ProyectoRepository proyectoRepository) {
        this.proyectoRepository = proyectoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Proyecto> listarProyectos() {
        List<Proyecto> proyectos = proyectoRepository.findAll();
        return proyectos.stream()
                .sorted(Comparator.comparing(Proyecto::getEstado)
                        .thenComparing(Proyecto::getNombre))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Proyecto> buscarProyectos(String consulta, EstadoProyecto estado, String orden) {
        String texto = consulta == null ? "" : consulta.trim().toLowerCase();
        Comparator<Proyecto> comparador = switch (orden == null ? "nombre" : orden) {
            case "fecha" -> Comparator.comparing(Proyecto::getFechaCreacion).reversed()
                    .thenComparing(Proyecto::getNombre, String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(Proyecto::getNombre, String.CASE_INSENSITIVE_ORDER);
        };

        return proyectoRepository.findAll().stream()
                .filter(proyecto -> estado == null || proyecto.getEstado() == estado)
                .filter(proyecto -> texto.isBlank()
                        || proyecto.getNombre().toLowerCase().contains(texto)
                        || (proyecto.getDescripcion() != null && proyecto.getDescripcion().toLowerCase().contains(texto)))
                .sorted(comparador)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Proyecto> buscarPorId(Long id) {
        return proyectoRepository.findById(id);
    }

    @Override
    public Proyecto crearProyecto(Proyecto proyecto) {
        if (existePorNombre(proyecto.getNombre())) {
            throw new BusinessException("Ya existe un proyecto con el nombre: " + proyecto.getNombre());
        }
        return proyectoRepository.save(proyecto);
    }

    @Override
    public Proyecto actualizarProyecto(Long id, Proyecto proyecto) {
        Proyecto proyectoExistente = buscarPorId(id)
                .orElseThrow(() -> new BusinessException("Proyecto no encontrado con ID: " + id));

        if (existePorNombreConIdDiferente(proyecto.getNombre(), id)) {
            throw new BusinessException("Ya existe otro proyecto con el nombre: " + proyecto.getNombre());
        }

        proyectoExistente.setNombre(proyecto.getNombre());
        proyectoExistente.setDescripcion(proyecto.getDescripcion());
        proyectoExistente.setEstado(proyecto.getEstado());

        return proyectoRepository.save(proyectoExistente);
    }

    @Override
    public void eliminarProyecto(Long id) {
        Proyecto proyecto = buscarPorId(id)
                .orElseThrow(() -> new BusinessException("Proyecto no encontrado con ID: " + id));

        proyectoRepository.delete(proyecto);
    }

    @Override
    public void cambiarEstado(Long id, EstadoProyecto estado) {
        Proyecto proyecto = buscarPorId(id)
                .orElseThrow(() -> new BusinessException("Proyecto no encontrado con ID: " + id));

        proyecto.setEstado(estado);
        proyectoRepository.save(proyecto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorNombre(String nombre) {
        return proyectoRepository.existsByNombre(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorNombreConIdDiferente(String nombre, Long id) {
        Optional<Proyecto> proyecto = proyectoRepository.findByNombre(nombre);
        return proyecto.isPresent() && !proyecto.get().getId().equals(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarProyectosActivos() {
        return proyectoRepository.countByEstadoNot(EstadoProyecto.CERRADO);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarProyectosPorEstado(EstadoProyecto estado) {
        return proyectoRepository.countByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Proyecto> listarProyectosRecientes() {
        return proyectoRepository.findTop5ByOrderByFechaCreacionDescIdDesc();
    }
}
