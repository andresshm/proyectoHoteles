package com.proyecto.hoteles.entidades;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class Huesped {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;
    private String nombre;
    private String apellido;
    private String dniPasaporte;
    private LocalDate fechaCheckin;
    private LocalDate fechaCheckout;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Habitacion.class)
    @JoinColumn(name = "habitacionId", nullable = true)
    private Habitacion habitacion;
    
}

