package com.proyecto.hoteles.entidades;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
@Schema(name = "Hotel", description = "Representa un hotel en la base de datos")
public class Hotel {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;
    @Schema(example = "JC1")
    private String nombre;
    @Schema(example = "calle espinardo, nº 69")
    private String direccion;
    @Schema(example = "689 54 71 32")
    private String telefono;
    @Schema(example = "jc1@hotmail.com")
    private String email;
    @Schema(example = "jc1.org")
    private String sitioWeb;

    @ManyToMany(mappedBy = "hoteles", cascade = CascadeType.ALL)
    private List<Servicio> servicios = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Habitacion> habitaciones; // No puede ser una linkedlist porque en el swagger da un error 500 en el
                                           // POST

    public void addRoom(Habitacion room) {
        habitaciones.add(room);
        room.setHotel(this);
    }

    public void addService(Servicio service) {
        servicios.add(service);
        List<Hotel> hoteles = service.getHoteles();
        hoteles.add(this);
        service.setHoteles(hoteles);
    }

}
