package com.proyecto.hoteles.entidades;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "history")
@Data
@NoArgsConstructor
@Schema(name = "Historial", description = "Representa el historial")
public class Historial {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String hotel;

    @Column(nullable = false)
    private LocalDate fechaIngreso;

}
