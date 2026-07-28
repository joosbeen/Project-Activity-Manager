package com.josben.tarea.controller;

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
import com.josben.tarea.enums.EstadoTarea;
import com.josben.tarea.enums.Prioridad;
import com.josben.tarea.exception.BusinessException;
import com.josben.tarea.service.ProyectoService;
import com.josben.tarea.service.TareaService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/tareas")
public class TareaController {

    private final TareaService tareaService;
    private final ProyectoService proyectoService;

    @Autowired
    public TareaController(TareaService tareaService, ProyectoService proyectoService) {
        this.tareaService = tareaService;
        this.proyectoService = proyectoService;
    }

    @GetMapping("/nuevo/{proyectoId}")
    public String mostrarFormularioNuevo(@PathVariable Long proyectoId, Model model, RedirectAttributes redirectAttributes) {
        Tarea tarea = new Tarea();
        model.addAttribute("tarea", tarea);
        model.addAttribute("proyectoId", proyectoId);
        model.addAttribute("estados", EstadoTarea.values());
        model.addAttribute("prioridades", Prioridad.values());
        return "tareas/formulario";
    }

    @PostMapping("/guardar")
    public String guardarTarea(@Valid @ModelAttribute Tarea tarea,
                               BindingResult result,
                               @RequestParam Long proyectoId,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("proyectoId", proyectoId);
            model.addAttribute("estados", EstadoTarea.values());
            model.addAttribute("prioridades", Prioridad.values());
            return "tareas/formulario";
        }

        try {
            Proyecto proyecto = proyectoService.buscarPorId(proyectoId)
                    .orElseThrow(() -> new BusinessException("Proyecto no encontrado"));
            tarea.setProyecto(proyecto);
            tareaService.crearTarea(tarea);
            redirectAttributes.addFlashAttribute("mensaje", "Tarea creada exitosamente");
            return "redirect:/proyectos/detalle/" + proyectoId;
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("proyectoId", proyectoId);
            model.addAttribute("estados", EstadoTarea.values());
            model.addAttribute("prioridades", Prioridad.values());
            return "tareas/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Tarea tarea = tareaService.buscarPorId(id)
                    .orElseThrow(() -> new BusinessException("Tarea no encontrada"));
            model.addAttribute("tarea", tarea);
            model.addAttribute("proyectoId", tarea.getProyecto().getId());
            model.addAttribute("estados", EstadoTarea.values());
            model.addAttribute("prioridades", Prioridad.values());
            return "tareas/formulario";
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/proyectos";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarTarea(@PathVariable Long id,
                                   @Valid @ModelAttribute Tarea tarea,
                                   BindingResult result,
                                   @RequestParam Long proyectoId,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("proyectoId", proyectoId);
            model.addAttribute("estados", EstadoTarea.values());
            model.addAttribute("prioridades", Prioridad.values());
            return "tareas/formulario";
        }

        try {
            tareaService.actualizarTarea(id, tarea);
            redirectAttributes.addFlashAttribute("mensaje", "Tarea actualizada exitosamente");
            return "redirect:/proyectos/detalle/" + proyectoId;
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("proyectoId", proyectoId);
            model.addAttribute("estados", EstadoTarea.values());
            model.addAttribute("prioridades", Prioridad.values());
            return "tareas/formulario";
        }
    }

    @GetMapping("/detalle/{id}")
    public String verDetalle(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Tarea tarea = tareaService.buscarPorId(id)
                    .orElseThrow(() -> new BusinessException("Tarea no encontrada"));
            model.addAttribute("tarea", tarea);
            return "tareas/detalle";
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/proyectos";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarTarea(@PathVariable Long id, @RequestParam Long proyectoId, RedirectAttributes redirectAttributes) {
        try {
            tareaService.eliminarTarea(id);
            redirectAttributes.addFlashAttribute("mensaje", "Tarea eliminada exitosamente");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/proyectos/detalle/" + proyectoId;
    }

    @GetMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id,
                                 @RequestParam EstadoTarea estado,
                                 @RequestParam Long proyectoId,
                                 RedirectAttributes redirectAttributes) {
        try {
            tareaService.cambiarEstado(id, estado);
            redirectAttributes.addFlashAttribute("mensaje", "Estado de la tarea actualizado exitosamente");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/proyectos/detalle/" + proyectoId;
    }
}