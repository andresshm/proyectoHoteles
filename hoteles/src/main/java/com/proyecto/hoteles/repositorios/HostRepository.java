package com.proyecto.hoteles.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.proyecto.hoteles.entidades.Huesped;
// import java.util.List;


public interface HostRepository extends JpaRepository<Huesped, Long>,  JpaSpecificationExecutor<Huesped>{
    // List<Huesped> findByNombre(String nombre);
    // List<Huesped> findByApellido(String apellido);
    // List<Huesped> findByDniPasaporte(String dniPasaporte);
    // List<Huesped> findByProcedencia(String procedencia);
    
}
