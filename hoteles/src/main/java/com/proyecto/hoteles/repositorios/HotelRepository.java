package com.proyecto.hoteles.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.hoteles.entidades.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Long>{
    /*@Query("SELECT n FROM Hotel h WHERE h.nombre = ?1")
    public Hotel getByName(String name);*/
}
