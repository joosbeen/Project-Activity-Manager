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

import com.josben.tarea.entity.Comentario;
import com.josben.tarea.entity.Tarea;
import com.josben.tarea.enums.EstadoComentario;
import com.josben.tarea.exception.BusinessException;
import com.josben.tarea.service.ComentarioService;
import com.josben.tarea.service.TareaService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;
    private final TareaService tareaService;

    @Autowired
    public ComentarioController(ComentarioService comentarioService, TareaService tareaService) {
        this.comentarioService = comentarioService;
        this.tareaService = tareaService;
    }

    @PostMapping("/guardar")
    public String guardarComentario(@Valid @ModelAttribute Comentario comentario,
                                    BindingResult result,
                                    @RequestParam Long tareaId,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("tareaId", tareaId);
            model.addAttribute("estadosComentario", EstadoComentario.values());
            return "redirect:/tareas/editar/" + tareaId;
        }

        try {
            Tarea tarea = tareaService.buscarPorId(tareaId)
                    .orElseThrow(() -> new BusinessException("Tarea no encontrada"));
            comentario.setTarea(tarea);
            comentarioService.crearComentario(comentario);
            redirectAttributes.addFlashAttribute("mensaje", "Comentario creado exitosamente");
            return "redirect:/tareas/editar/" + tareaId;
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("tareaId", tareaId);
            model.addAttribute("estadosComentario", EstadoComentario.values());
            return "redirect:/tareas/editar/" + tareaId;
        }
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarComentario(@PathVariable Long id,
                                       @Valid @ModelAttribute Comentario comentario,
                                       BindingResult result,
                                       @RequestParam Long tareaId,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("tareaId", tareaId);
            model.addAttribute("estadosComentario", EstadoComentario.values());
            return "redirect:/tareas/editar/" + tareaId;
        }

        try {
            comentarioService.actualizarComentario(id, comentario);
            redirectAttributes.addFlashAttribute("mensaje", "Comentario actualizado exitosamente");
            return "redirect:/tareas/editar/" + tareaId;
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("tareaId", tareaId);
            model.addAttribute("estadosComentario", EstadoComentario.values());
            return "redirect:/tareas/editar/" + tareaId;
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarComentario(@PathVariable Long id, 
                                     @RequestParam Long tareaId,
                                     RedirectAttributes redirectAttributes) {
        try {
            comentarioService.eliminarComentario(id);
            redirectAttributes.addFlashAttribute("mensaje", "Comentario eliminado exitosamente");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tareas/editar/" + tareaId;
    }

    @GetMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id,
                               @RequestParam EstadoComentario estado,
                               @RequestParam Long tareaId,
                               RedirectAttributes redirectAttributes) {
        try {
            comentarioService.cambiarEstado(id, estado);
            redirectAttributes.addFlashAttribute("mensaje", "Estado del comentario actualizado exitosamente");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tareas/editar/" + tareaId;
    }
}
