package com.proyecto.hoteles.controlador;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.proyecto.hoteles.servicios.ServicioHistorial;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Historial API", description = "Esta API sirve para gestionar los huéspedes")
@RestController
@CrossOrigin(origins = "http://localhost:4200")//"http://localhost:4200")
@RequestMapping("/history")
public class HistoryController {

    @Autowired
    private ServicioHistorial servicio;


    //2024-06-17T12:30
   @GetMapping("/count")
    public int contarHuespedes(@RequestParam Long idHotel, @RequestParam String fecha) {

        
        LocalDate fechaIngreso = LocalDate.parse(fecha);
        System.out.println(fechaIngreso);
        return servicio.contarHuespedes(idHotel, fechaIngreso);
        
    }
    
}
