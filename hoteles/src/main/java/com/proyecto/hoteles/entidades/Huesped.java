package com.proyecto.hoteles.entidades;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
@Schema(name = "Huesped", description = "Representa un huesped en la base de datos")
public class Huesped {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;
    private long idHabitacion;
    @Schema(example = "Juan Carlos")
    private String nombre;
    @Schema(example = "Torres")
    private String apellido;
    @Schema(example = "48759851-L")
    private String dniPasaporte;
    @Schema(example = "2024-06-15")
    private LocalDate fechaCheckin;
    @Schema(example = "2024-06-30")
    private LocalDate fechaCheckout;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Habitacion.class)
    @JoinColumn(name = "habitacionId", nullable = true)
    private Habitacion habitacion;

}
