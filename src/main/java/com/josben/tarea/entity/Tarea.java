package com.josben.tarea.entity;

import com.josben.tarea.enums.EstadoTarea;
import com.josben.tarea.enums.Prioridad;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tareas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    @Column(nullable = false, length = 200)
    private String titulo;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    @Column(length = 1000)
    private String descripcion;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    private LocalDate fechaCierre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTarea estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridad prioridad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @OneToMany(mappedBy = "tarea", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaInicio = LocalDate.now();
        if (estado == null) {
            estado = EstadoTarea.ABIERTO;
        }
        if (prioridad == null) {
            prioridad = Prioridad.MEDIA;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (estado == EstadoTarea.CERRADO && fechaCierre == null) {
            fechaCierre = LocalDate.now();
        } else if (estado != EstadoTarea.CERRADO) {
            fechaCierre = null;
        }
    }
}
