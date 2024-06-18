package com.proyecto.hoteles.entidades;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Data;

@Data
@Entity
public class Servicio {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;
    private String nombre;
    private String descripcion;


    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
        name = "servicio_hotel", joinColumns = @JoinColumn(name = "servicio_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "hotel_id", referencedColumnName = "id")
    )
    private List<Hotel> hoteles = new ArrayList<>();

}
