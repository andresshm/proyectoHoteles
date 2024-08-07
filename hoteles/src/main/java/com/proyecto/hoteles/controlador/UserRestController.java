package com.proyecto.hoteles.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.hoteles.entidades.Usuario;
import com.proyecto.hoteles.servicios.ServicioUsuario;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Tag(name = "User API", description = "Esta API sirve para gestionar los usuarios")
@RestController
@CrossOrigin(origins = "*")//"http://localhost:4200")
@RequestMapping("/users")
public class UserRestController {

    @Autowired
    ServicioUsuario servicio;

    @GetMapping()
    public List<Usuario> getAllUsers() {
        return servicio.getAllUsers();
    }

    @PostMapping()
    public ResponseEntity<?> postUser(@RequestBody Usuario input) {        
        return servicio.addUser(input);
    }
    
    

    

}
