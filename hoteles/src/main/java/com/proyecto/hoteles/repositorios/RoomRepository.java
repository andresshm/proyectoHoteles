package com.proyecto.hoteles.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.proyecto.hoteles.entidades.Habitacion;

public interface RoomRepository extends JpaRepository<Habitacion, Long>,  JpaSpecificationExecutor<Habitacion>{

}
