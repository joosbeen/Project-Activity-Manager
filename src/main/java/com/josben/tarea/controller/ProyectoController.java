package com.josben.tarea.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.josben.tarea.entity.Proyecto;
import com.josben.tarea.entity.Tarea;
import com.josben.tarea.enums.EstadoProyecto;
import com.josben.tarea.enums.EstadoTarea;
import com.josben.tarea.exception.BusinessException;
import com.josben.tarea.service.ProyectoService;
import com.josben.tarea.service.TareaService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/proyectos")
public class ProyectoController {

    private final ProyectoService proyectoService;
    private final TareaService tareaService;

    @Autowired
    public ProyectoController(ProyectoService proyectoService, TareaService tareaService) {
        this.proyectoService = proyectoService;
        this.tareaService = tareaService;
    }

    @GetMapping
    public String listarProyectos(@RequestParam(required = false) String q,
                                  @RequestParam(required = false) EstadoProyecto estado,
                                  @RequestParam(defaultValue = "nombre") String orden,
                                  Model model) {
        List<Proyecto> proyectos = proyectoService.buscarProyectos(q, estado, orden);
        model.addAttribute("proyectos", proyectos);
        model.addAttribute("estados", EstadoProyecto.values());
        model.addAttribute("q", q);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("ordenSeleccionado", orden);
        return "proyectos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("proyecto", new Proyecto());
        model.addAttribute("estados", EstadoProyecto.values());
        return "proyectos/formulario";
    }

    @PostMapping("/guardar")
    public String guardarProyecto(@Valid @ModelAttribute Proyecto proyecto,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("estados", EstadoProyecto.values());
            return "proyectos/formulario";
        }

        try {
            proyectoService.crearProyecto(proyecto);
            redirectAttributes.addFlashAttribute("mensaje", "Proyecto creado exitosamente");
            return "redirect:/proyectos";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("estados", EstadoProyecto.values());
            return "proyectos/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Proyecto proyecto = proyectoService.buscarPorId(id)
                    .orElseThrow(() -> new BusinessException("Proyecto no encontrado"));
            model.addAttribute("proyecto", proyecto);
            model.addAttribute("estados", EstadoProyecto.values());
            return "proyectos/formulario";
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/proyectos";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarProyecto(@PathVariable Long id,
                                      @Valid @ModelAttribute Proyecto proyecto,
                                      BindingResult result,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("estados", EstadoProyecto.values());
            return "proyectos/formulario";
        }

        try {
            proyectoService.actualizarProyecto(id, proyecto);
            redirectAttributes.addFlashAttribute("mensaje", "Proyecto actualizado exitosamente");
            return "redirect:/proyectos";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("estados", EstadoProyecto.values());
            return "proyectos/formulario";
        }
    }

    @GetMapping("/detalle/{id}")
    public String verDetalle(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Proyecto proyecto = proyectoService.buscarPorId(id)
                    .orElseThrow(() -> new BusinessException("Proyecto no encontrado"));
            List<Tarea> tareas = tareaService.listarTareasPorProyecto(id);
            Map<EstadoTarea, List<Tarea>> tareasPorEstado = tareas.stream()
                    .collect(Collectors.groupingBy(Tarea::getEstado));
            model.addAttribute("proyecto", proyecto);
            model.addAttribute("tareas", tareas);
            model.addAttribute("tareasAbiertas", tareasPorEstado.getOrDefault(EstadoTarea.ABIERTO, List.of()));
            model.addAttribute("tareasEnProceso", tareasPorEstado.getOrDefault(EstadoTarea.PROCESO, List.of()));
            model.addAttribute("tareasQA", tareasPorEstado.getOrDefault(EstadoTarea.QA, List.of()));
            model.addAttribute("tareasDetenidas", tareasPorEstado.getOrDefault(EstadoTarea.DETENIDO, List.of()));
            model.addAttribute("tareasCerradas", tareasPorEstado.getOrDefault(EstadoTarea.CERRADO, List.of()));
            return "proyectos/detalle";
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/proyectos";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProyecto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            proyectoService.eliminarProyecto(id);
            redirectAttributes.addFlashAttribute("mensaje", "Proyecto eliminado exitosamente");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/proyectos";
    }

    @GetMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id,
                                 @RequestParam EstadoProyecto estado,
                                 RedirectAttributes redirectAttributes) {
        try {
            proyectoService.cambiarEstado(id, estado);
            redirectAttributes.addFlashAttribute("mensaje", "Estado del proyecto actualizado exitosamente");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/proyectos";
    }
}
