package com.josben.tarea.entity;

import com.josben.tarea.enums.EstadoComentario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "comentarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comentario {

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
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoComentario estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarea_id", nullable = false)
    private Tarea tarea;

    @PrePersist
    protected void onCreate() {
        fecha = LocalDate.now();
        if (estado == null) {
            estado = EstadoComentario.ABIERTO;
        }
    }
}
