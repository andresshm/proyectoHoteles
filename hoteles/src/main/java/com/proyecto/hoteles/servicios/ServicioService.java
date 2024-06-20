package com.proyecto.hoteles.servicios;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.proyecto.hoteles.entidades.Servicio;
import com.proyecto.hoteles.repositorios.ServiceRepository;

@Service
public class ServicioService {

    @Autowired
    ServiceRepository serviceRepository;

    public Servicio updateServiceByFields(long id, Map<String, Object> fields){
		Optional<Servicio> optService = serviceRepository.findById(id);

		if(optService.isPresent()){
			fields.forEach((key, value) -> {
				Field field = ReflectionUtils.findField(Servicio.class, key);
				field.setAccessible(true);
				ReflectionUtils.setField(field, optService.get(), value);
			});
			return serviceRepository.save(optService.get());
		}else{
			return null;
		}
 	}


    public List<Servicio> findByName(String name){
        List<Servicio> services = new ArrayList<>();
        for(Servicio h : serviceRepository.findAll()){
            if(h.getNombre().equalsIgnoreCase(name)){
                services.add(h);
            }
        }
        return services;
    }


    public List<Servicio> findByDescription(String description){
        List<Servicio> services = new ArrayList<>();
        for(Servicio s : serviceRepository.findAll()){
            if(s.getDescripcion().toLowerCase().contains(description.toLowerCase())/*h.getDescripcion().equalsIgnoreCase(description)*/){
                services.add(s);
            }
        }
        return services;
    }


}
