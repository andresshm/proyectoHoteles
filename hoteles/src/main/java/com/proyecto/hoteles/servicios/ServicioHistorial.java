package com.proyecto.hoteles.servicios;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.hoteles.entidades.Habitacion;
import com.proyecto.hoteles.entidades.Hotel;
import com.proyecto.hoteles.entidades.Huesped;
import com.proyecto.hoteles.repositorios.HotelRepository;

@Service
public class ServicioHistorial {

    @Autowired
    private HotelRepository hotelRepository;

    public int contarHuespedes(Long idHotel, LocalDate fechaIngreso) {
        Hotel hotel = hotelRepository.findById(idHotel).get();
        int cont=0;
        for(Habitacion h : hotel.getHabitaciones()){
            for(Huesped g : h.getHuespedes()){
                System.out.println(g.getFechaCheckin());
                LocalDateTime fecha = g.getFechaCheckin();
                if(LocalDate.of(fecha.getYear(), fecha.getMonth(), fecha.getDayOfMonth()).equals(fechaIngreso)){
                    cont++;
                }
            }
        }

        return cont;
    }
}
