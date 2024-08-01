package com.proyecto.hoteles.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.proyecto.hoteles.entidades.Huesped;

public interface HostRepository extends JpaRepository<Huesped, Long>,  JpaSpecificationExecutor<Huesped>{
    
}
