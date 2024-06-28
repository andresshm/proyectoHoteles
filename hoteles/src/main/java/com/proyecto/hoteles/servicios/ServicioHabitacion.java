package com.proyecto.hoteles.servicios;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.proyecto.hoteles.entidades.Habitacion;
import com.proyecto.hoteles.entidades.Huesped;
import com.proyecto.hoteles.repositorios.HostRepository;
import com.proyecto.hoteles.repositorios.RoomRepository;

import jakarta.transaction.Transactional;

@Service
public class ServicioHabitacion {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HostRepository hostRepository;

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
        return roomRepository.findAll().stream()
                                    .filter(h -> h.getNumero().equals(number))
                                    .collect(Collectors.toList());
    
}



public List<Habitacion> findByType(String type){
    return roomRepository.findAll().stream()
                                    .filter(h -> h.getTipo().equalsIgnoreCase(type))
                                    .collect(Collectors.toList());
}



public List<Habitacion> findByPrice(float price){
    return roomRepository.findAll().stream()
                                    .filter(h -> h.getPrecioNoche() == price)
                                    .collect(Collectors.toList());
}




@Transactional
public void addHostToRoom(long idRoom, long idHost){
    Habitacion habitacion = roomRepository.findById(idRoom).orElseThrow(() -> new RuntimeException("Room not found"));
    Huesped host = hostRepository.findById(idHost).orElseThrow(() -> new RuntimeException("Host not found"));
    
    habitacion.addHost(host);
    roomRepository.save(habitacion);
}



private boolean isNumeric(String n){
    return n.matches("\\d+");
}

private boolean isGreaterThanCero(float n){
    return n > 0;
}


public boolean validRoom(String s, float n){
    return !(isNumeric(s) && isGreaterThanCero(n));

}


}
