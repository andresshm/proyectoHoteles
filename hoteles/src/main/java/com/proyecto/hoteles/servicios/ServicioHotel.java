package com.proyecto.hoteles.servicios;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.proyecto.hoteles.entidades.Hotel;
import com.proyecto.hoteles.repositorios.HotelRepository;

@Service
public class ServicioHotel {
 @Autowired
    private HotelRepository hotelRepository;

    public Hotel updateHotelByFields(long id, Map<String, Object> fields){
		Optional<Hotel> optHotel = hotelRepository.findById(id);

		if(optHotel.isPresent()){
			fields.forEach((key, value) -> {
				Field field = ReflectionUtils.findField(Hotel.class, key);
				field.setAccessible(true);
				ReflectionUtils.setField(field, optHotel.get(), value);
			} );
			return hotelRepository.save(optHotel.get());
		}else{
			return null;
		}
 	}



    public List<Hotel> findByName(String name){
    List<Hotel> hoteles = new ArrayList<>();
    for(Hotel h : hotelRepository.findAll()){
        if(h.getNombre().equalsIgnoreCase(name)){
            hoteles.add(h);
        }
    }
    return hoteles;
}


public List<Hotel> findByAddress(String dir){
    List<Hotel> hoteles = new ArrayList<>();
    for(Hotel h : hotelRepository.findAll()){
        if(h.getDireccion().equalsIgnoreCase(dir)){
            hoteles.add(h);
        }
    }
    return hoteles;
}


public List<Hotel> findByPhone(String telefono){
    List<Hotel> hoteles = new ArrayList<>();
    for(Hotel h : hotelRepository.findAll()){
        if(h.getTelefono().equalsIgnoreCase(telefono)){
            hoteles.add(h);
        }
    }
    return hoteles;
}


public List<Hotel> findByMail(String email){
    List<Hotel> hoteles = new ArrayList<>();
    for(Hotel h : hotelRepository.findAll()){
        if(h.getEmail().equalsIgnoreCase(email)){
            hoteles.add(h);
        }
    }
    return hoteles;
}


public List<Hotel> findByWebsite(String web){
    List<Hotel> hoteles = new ArrayList<>();
    for(Hotel h : hotelRepository.findAll()){
        if(h.getSitioWeb().equalsIgnoreCase(web)){
            hoteles.add(h);
        }
    }
    return hoteles;
}


}
