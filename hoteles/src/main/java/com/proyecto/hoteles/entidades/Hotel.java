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

/*
 * HOTEL
 */
@Data
@Entity
@Schema(name = "Hotel", description = "Representa un hotel en la base de datos")
public class Hotel {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;
    private List<Long> services = new ArrayList<>();
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

    /* CONSTRUCTOR NECESARIO SOLO PARA HACER TEST */
    public Hotel() {
        // Este constructor es necesario para swagger porque necesita uno por defecto
        // sin params
    }

    public Hotel(String nombre, String direccion, String telefono, String email, String sitioWeb) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.sitioWeb = sitioWeb;
    }

    public Hotel(long id) {
        this.id = id;
    }

    /* MÉTODOS */
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

    public void removeService(Servicio service) {
        servicios.remove(service);
        List<Hotel> hoteles = service.getHoteles();
        hoteles.remove(this);
        service.setHoteles(hoteles);

    }

}
