package com.proyecto.hoteles.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.hoteles.entidades.Servicio;

public interface ServiceRepository extends JpaRepository<Servicio, Long>{

}
