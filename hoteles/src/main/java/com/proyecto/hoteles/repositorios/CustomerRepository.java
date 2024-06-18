package com.proyecto.hoteles.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.hoteles.entidades.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>{


}
