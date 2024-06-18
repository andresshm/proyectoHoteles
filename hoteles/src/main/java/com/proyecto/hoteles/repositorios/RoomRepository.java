package com.proyecto.hoteles.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.hoteles.entidades.Habitacion;

public interface RoomRepository extends JpaRepository<Habitacion, Long>{

}
