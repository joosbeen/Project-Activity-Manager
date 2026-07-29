package com.josben.tarea.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.josben.tarea.entity.Tarea;
import com.josben.tarea.enums.EstadoTarea;
import com.josben.tarea.enums.EstadoProyecto;
import com.josben.tarea.enums.Prioridad;
import com.josben.tarea.service.ProyectoService;
import com.josben.tarea.service.TareaService;

@Controller
public class HomeController {

    private final TareaService tareaService;
    private final ProyectoService proyectoService;

    @Autowired
    public HomeController(TareaService tareaService, ProyectoService proyectoService) {
        this.tareaService = tareaService;
        this.proyectoService = proyectoService;
    }

    @GetMapping("/")
    public String home(@RequestParam(required = false) String q,
                       @RequestParam(required = false) Long proyectoId,
                       @RequestParam(required = false) EstadoTarea estado,
                       @RequestParam(required = false) Prioridad prioridad,
                       @RequestParam(defaultValue = "fecha") String orden,
                       Model model) {
        List<Tarea> tareasAbiertas = tareaService.buscarTareas(q, proyectoId, estado, prioridad, orden);
        long cantidadTareasAbiertas = tareaService.contarTareasAbiertas();
        long cantidadProyectosActivos = proyectoService.contarProyectosActivos();

        model.addAttribute("tareas", tareasAbiertas);
        model.addAttribute("cantidadTareasAbiertas", cantidadTareasAbiertas);
        model.addAttribute("cantidadProyectosActivos", cantidadProyectosActivos);
        model.addAttribute("proyectos", proyectoService.listarProyectos());
        model.addAttribute("estados", EstadoTarea.values());
        model.addAttribute("prioridades", Prioridad.values());
        model.addAttribute("q", q);
        model.addAttribute("proyectoIdSeleccionado", proyectoId);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("prioridadSeleccionada", prioridad);
        model.addAttribute("ordenSeleccionado", orden);
        model.addAttribute("proyectosPendientes", proyectoService.contarProyectosPorEstado(EstadoProyecto.PENDIENTE));
        model.addAttribute("proyectosEnProceso", proyectoService.contarProyectosPorEstado(EstadoProyecto.PROCESO));
        model.addAttribute("proyectosDetenidos", proyectoService.contarProyectosPorEstado(EstadoProyecto.DETENIDO));
        model.addAttribute("proyectosCerrados", proyectoService.contarProyectosPorEstado(EstadoProyecto.CERRADO));
        model.addAttribute("tareasAbiertas", tareaService.contarTareasPorEstado(EstadoTarea.ABIERTO));
        model.addAttribute("tareasEnProceso", tareaService.contarTareasPorEstado(EstadoTarea.PROCESO));
        model.addAttribute("tareasQA", tareaService.contarTareasPorEstado(EstadoTarea.QA));
        model.addAttribute("tareasDetenidas", tareaService.contarTareasPorEstado(EstadoTarea.DETENIDO));
        model.addAttribute("tareasCerradas", tareaService.contarTareasPorEstado(EstadoTarea.CERRADO));
        model.addAttribute("tareasAltaPrioridad", tareaService.contarTareasPorPrioridad(Prioridad.ALTA));
        model.addAttribute("tareasMediaPrioridad", tareaService.contarTareasPorPrioridad(Prioridad.MEDIA));
        model.addAttribute("tareasBajaPrioridad", tareaService.contarTareasPorPrioridad(Prioridad.BAJA));
        model.addAttribute("proyectosRecientes", proyectoService.listarProyectosRecientes());
        model.addAttribute("tareasRecientes", tareaService.listarTareasRecientes());

        return "home";
    }
}
