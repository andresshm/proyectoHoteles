package com.proyecto.hoteles.entidades;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Usuario {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @Schema(example = "Juan Carlos")
    private String nombre;
    
    @Schema(example = "1234")
    private String password;
    
    @Schema(example = "usuario", defaultValue = "usuario")
    private String rol;

}
