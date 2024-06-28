package com.proyecto.hoteles.servicios;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.proyecto.hoteles.entidades.Hotel;
import com.proyecto.hoteles.entidades.Servicio;
import com.proyecto.hoteles.repositorios.HotelRepository;
import com.proyecto.hoteles.repositorios.ServiceRepository;

import jakarta.transaction.Transactional;

@Service
public class ServicioService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private HotelRepository hotelRepository;

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
        return serviceRepository.findAll().stream()
                                    .filter(h -> h.getNombre().equalsIgnoreCase(name))
                                    .collect(Collectors.toList());
    }


    public List<Servicio> findByDescription(String description){
        return serviceRepository.findAll().stream()
                                    .filter(s -> s.getDescripcion().toLowerCase().contains(description.toLowerCase()))
                                    .collect(Collectors.toList());
    }


    @Transactional
    public void addHotelToService(long idService, long idHotel){
    Servicio service = serviceRepository.findById(idService).orElseThrow(() -> new RuntimeException("Service not found"));
    Hotel hotel = hotelRepository.findById(idHotel).orElseThrow(() -> new RuntimeException("Hotel not found"));
    

    service.addHotel(hotel);
    serviceRepository.save(service);
}

}
