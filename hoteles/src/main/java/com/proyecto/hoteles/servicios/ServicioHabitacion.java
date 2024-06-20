package com.proyecto.hoteles.servicios;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.proyecto.hoteles.entidades.Habitacion;
import com.proyecto.hoteles.repositorios.RoomRepository;

@Service
public class ServicioHabitacion {

    @Autowired
    private RoomRepository roomRepository;

    public Habitacion updateRoomByFields(long id, Map<String, Object> fields){
		Optional<Habitacion> optRoom = roomRepository.findById(id);

		if(optRoom.isPresent()){
			fields.forEach((key, value) -> {
				Field field = ReflectionUtils.findField(Habitacion.class, key);
				field.setAccessible(true);
				ReflectionUtils.setField(field, optRoom.get(), value);
			} );
			return roomRepository.save(optRoom.get());
		}else{
			return null;
		}
 	}



    public List<Habitacion> findByNumber(String number){
    List<Habitacion> rooms = new ArrayList<>();
    for(Habitacion h : roomRepository.findAll()){
        if(h.getNumero().equals(number)){
            rooms.add(h);
        }
    }
    return rooms;
}



public List<Habitacion> findByType(String type){
    List<Habitacion> rooms = new ArrayList<>();
    for(Habitacion h : roomRepository.findAll()){
        if(h.getTipo().equalsIgnoreCase(type)){
            rooms.add(h);
        }
    }
    return rooms;
}



public List<Habitacion> findByPrice(float price){
    List<Habitacion> rooms = new ArrayList<>();
    for(Habitacion h : roomRepository.findAll()){
        if(h.getPrecioNoche() == price){
            rooms.add(h);
        }
    }
    return rooms;
}











}
