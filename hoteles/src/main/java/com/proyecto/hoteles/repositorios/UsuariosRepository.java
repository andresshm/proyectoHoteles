package com.proyecto.hoteles.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;


import com.proyecto.hoteles.entidades.Usuario;


public interface UsuariosRepository extends JpaRepository<Usuario, Long>{

}
