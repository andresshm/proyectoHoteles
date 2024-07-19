package com.proyecto.hoteles.entidades;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.proyecto.hoteles.utils.CustomLocalDateDeserializer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

/*
 * Huésped
 */
@Data
@Entity
@Schema(name = "Huesped", description = "Representa un huesped en la base de datos")
public class Huesped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;
    private long idHabitacion;
    @Schema(example = "Juan Carlos")
    private String nombre;
    @Schema(example = "Torres")
    private String apellido;
    @Schema(example = "48759851-L")
    private String dniPasaporte;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    @Schema(type = "string", pattern = "dd-MM-yyyy HH:mm", example = "17-06-2024 12:30")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDateTime fechaCheckin;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    @Schema(type = "string", pattern = "dd-MM-yyyy HH:mm", example = "30-09-2024 14:00") // sin el type string salen
                                                                                         // cosas raras. Así trata el
                                                                                         // ejemplo como un string, que
                                                                                         // no da problemas
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDateTime fechaCheckout;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Habitacion.class)
    @JoinColumn(name = "habitacionId", nullable = true)
    private Habitacion habitacion;

    /* CONSTRUCTOR NECESARIO SOLO PARA HACER TEST */

    public Huesped() {
        // Este constructor es necesario para swagger porque necesita uno por defecto
        // sin params
    }

    public Huesped(String nombre, String apellido, String dniPasaporte) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dniPasaporte = dniPasaporte;
    }

    public Huesped(long id) {
        this.id = id;
    }

    public Huesped(LocalDateTime fechaCheckin, LocalDateTime fechaCheckout) {
        this.fechaCheckin = fechaCheckin;
        this.fechaCheckout = fechaCheckout;
    }

}
