package com.proyecto.hoteles.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.proyecto.hoteles.entidades.Servicio;

public interface ServiceRepository extends JpaRepository<Servicio, Long>,  JpaSpecificationExecutor<Servicio>{

}
