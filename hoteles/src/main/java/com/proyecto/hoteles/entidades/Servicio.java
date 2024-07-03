package com.proyecto.hoteles.entidades;

import java.util.ArrayList;
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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Data;

@Data
@Entity
@Schema(name = "Servicio", description = "Representa un servicio en la base de datos")
public class Servicio {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;
    @Schema(example = "Catering")
    private String nombre;
    @Schema(example = "Servicio de desayunos y comidas personalizado")
    private String descripcion;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER, targetEntity = Hotel.class, cascade =  CascadeType.ALL)
    @JoinTable(
        name = "servicio_hotel", joinColumns = @JoinColumn(name = "servicio_id", referencedColumnName = "id", nullable = true),
        inverseJoinColumns = @JoinColumn(name = "hotel_id", referencedColumnName = "id", nullable = true))
    private List<Hotel> hoteles = new ArrayList<>();


/*CONSTRUCTORES NECESARIOS SOLO PARA TESTS */

    public Servicio(){
        //Este constructor es necesario para swagger porque necesita uno por defecto sin params

    }

    public Servicio(String nombre, String descripcion){
        this.nombre=nombre;
        this.descripcion = descripcion;
    }

    public Servicio(long id){
        this.id = id;
    }

    public Servicio(long id, String nombre, String descripcion){
        this.id = id;
        this.nombre=nombre;
        this.descripcion = descripcion;
    }




    public void addHotel(Hotel hotel) {
        hoteles.add(hotel);
        List<Servicio> servicios = hotel.getServicios();
        servicios.add(this);
        hotel.setServicios(servicios);
    }


}
