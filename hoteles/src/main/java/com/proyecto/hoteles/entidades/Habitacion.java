package com.proyecto.hoteles.entidades;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

/**
 * Habitacion
 */
@Data
@Entity
@Schema(name = "Habitacion", description = "Representa una habitacion en la base de datos")
public class Habitacion {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;
    private long idHotel;
    @Schema(example = "23", defaultValue = "10")
    private String numero;
    @Schema(example = "Simple")
    private String tipo;
    @Schema(example = "39.99")
    private float precioNoche;

    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "habitacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Huesped> huespedes;


    @JsonIgnore //Evita recursividad
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Hotel.class)
    @JoinColumn(name = "hotelId", nullable = true) //En name no se puede poner idHotel porque da un error
    private Hotel hotel;
  
    
    public void addHost(Huesped host) {
        huespedes.add(host);
        host.setHabitacion(this);
    }
}
