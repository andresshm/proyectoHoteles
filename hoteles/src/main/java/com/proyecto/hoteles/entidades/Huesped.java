package com.proyecto.hoteles.entidades;

import java.time.LocalDate;

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
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Schema(type = "string", pattern = "dd-MM-yyyy", example = "17-06-2024")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate fechaCheckin;
   
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Schema(type = "string", pattern = "dd-MM-yyyy", example = "30-09-2024")//sin el type string salen cosas raras, trata el ejemplo como un string, que no da problemas
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate fechaCheckout;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Habitacion.class)
    @JoinColumn(name = "habitacionId", nullable = true)
    private Habitacion habitacion;

}
