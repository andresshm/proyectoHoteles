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
import com.proyecto.hoteles.entidades.Hotel;
import com.proyecto.hoteles.entidades.Servicio;
import com.proyecto.hoteles.repositorios.HotelRepository;
import com.proyecto.hoteles.repositorios.RoomRepository;
import com.proyecto.hoteles.repositorios.ServiceRepository;

import jakarta.transaction.Transactional;

@Service
public class ServicioHotel {
 @Autowired
    private HotelRepository hotelRepository;

    @Autowired 
    private RoomRepository roomRepository;

    @Autowired 
    private ServiceRepository serviceRepository;

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
        return hotelRepository.findAll().stream()
                                        .filter(h -> h.getNombre().equalsIgnoreCase(name))
                                        .collect(Collectors.toList());
}


public List<Hotel> findByAddress(String dir){
    return hotelRepository.findAll().stream()
                                        .filter(h -> h.getDireccion().equalsIgnoreCase(dir))
                                        .collect(Collectors.toList());
}


public List<Hotel> findByPhone(String telefono){
    return hotelRepository.findAll().stream()
                                        .filter(h -> h.getTelefono().equalsIgnoreCase(telefono))
                                        .collect(Collectors.toList());
}


public List<Hotel> findByMail(String email){
    return hotelRepository.findAll().stream()
                                        .filter(h -> h.getEmail().equalsIgnoreCase(email))
                                        .collect(Collectors.toList());
}


public List<Hotel> findByWebsite(String web){
    return hotelRepository.findAll().stream()
                                        .filter(h -> h.getSitioWeb().equalsIgnoreCase(web))
                                        .collect(Collectors.toList());
}


@Transactional
public void addRoomToHotel(long idHotel, long idRoom){
    Hotel hotel = hotelRepository.findById(idHotel).orElseThrow(() -> new RuntimeException("Hotel not found"));
    Habitacion habitacion = roomRepository.findById(idRoom).orElseThrow(() -> new RuntimeException("Room not found"));

    hotel.addRoom(habitacion);
    hotelRepository.save(hotel);
}


@Transactional
public void addServiceToHotel(long idHotel, long idService){
    Hotel hotel = hotelRepository.findById(idHotel).orElseThrow(() -> new RuntimeException("Hotel not found"));
    Servicio service = serviceRepository.findById(idService).orElseThrow(() -> new RuntimeException("Service not found"));

    hotel.addService(service);
    hotelRepository.save(hotel);
}


}
