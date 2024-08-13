package com.proyecto.hoteles.servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proyecto.hoteles.entidades.Usuario;
import com.proyecto.hoteles.repositorios.UsuariosRepository;

@Service
public class ServicioUsuario {

    @Autowired
    private UsuariosRepository userRepository;

    public List<Usuario> getAllUsers(){
        return userRepository.findAll();
    }

    public ResponseEntity<?> addUser(Usuario input){
        
        // if()
        Usuario save = userRepository.save(input);
        return ResponseEntity.ok(save);
    }
    }


