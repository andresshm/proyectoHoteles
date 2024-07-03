package com.proyecto.hoteles.servicios;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.proyecto.hoteles.entidades.Habitacion;
import com.proyecto.hoteles.entidades.Hotel;
import com.proyecto.hoteles.entidades.Huesped;
import com.proyecto.hoteles.exception.BussinesRuleException;
import com.proyecto.hoteles.repositorios.HostRepository;
import com.proyecto.hoteles.repositorios.HotelRepository;
import com.proyecto.hoteles.repositorios.RoomRepository;
import com.proyecto.hoteles.utils.ListsUtil;

import jakarta.transaction.Transactional;

@Service
public class ServicioHabitacion {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HostRepository hostRepository;

    @Autowired
    private HotelRepository hotelRepository;

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


public List<Habitacion> filter(String numero, String tipo, Float precio){
    List<Habitacion> hostsByNumber = new ArrayList<>();
        List<Habitacion> hostsByType = new ArrayList<>();

        // float no puede ser null porque es un tipo primitivo, pero Float si
        // si es nulo, asignamos 0$ a precio para que no encuentre ninguna habitacion
        // y no entorpezca la busqueda 
        Float floatWrapper = precio;
        if (floatWrapper == null) {
            precio = 0.0f;
        } else {
            floatWrapper = precio;
        }
        List<Habitacion> hostsByPrice = findByPrice(precio);

        List<Boolean> vaciaPorNotFound = new ArrayList<>();
        boolean p = false, q = false;
        Set<Habitacion> hostsFound = new HashSet<>();
        if (p = numero != null) {
            hostsByNumber = findByNumber(numero);
            hostsFound.addAll(hostsByNumber);
            vaciaPorNotFound.add(p);
        }

        if (q = tipo != null) {
            hostsByType = findByType(tipo);
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByType, vaciaPorNotFound);
            vaciaPorNotFound.add(q);
        }

        if (floatWrapper != null) {
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByPrice, vaciaPorNotFound);
        }

        return new ArrayList<>(hostsFound);
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



 public ResponseEntity<?> getAll(){
    if (!roomRepository.findAll().isEmpty()) {
        return new ResponseEntity<>(roomRepository.findAll(), HttpStatus.OK);
    } else {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    }



    public ResponseEntity<?> getById(long id) throws BussinesRuleException{
        Optional<Habitacion> room = roomRepository.findById(id);
        if (room.isPresent()) {
            return new ResponseEntity<>(room.get(), HttpStatus.OK);
        } else {
            throw new BussinesRuleException("404","Not Found", "Error validacion, la habitacion con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
    }


    private void addRoomToHotel(long idHotel, long idRoom){
        Hotel hotel = hotelRepository.findById(idHotel).orElseThrow(() -> new RuntimeException("Hotel not found"));
        Habitacion habitacion = roomRepository.findById(idRoom).orElseThrow(() -> new RuntimeException("Room not found"));

        hotel.addRoom(habitacion);
        hotelRepository.save(hotel);
    }

    public ResponseEntity<?> put(long id, Habitacion input) throws BussinesRuleException{
        Optional<Habitacion> optionalRoom = roomRepository.findById(id);
        if (optionalRoom.isPresent()) {
            Habitacion newRoom = optionalRoom.get();
            // Inicializamos los atributos
            if(!validRoom(input.getNumero(), input.getPrecioNoche())){//si es valida...
            newRoom.setNumero(input.getNumero());
            newRoom.setPrecioNoche(input.getPrecioNoche());
            newRoom.setTipo(input.getTipo());
            
            //si cambio el idHotel la habitacion se asocia a ese hotel
            if(newRoom.getIdHotel()==0){
                newRoom.setIdHotel(input.getIdHotel());
                newRoom.setHotel(input.getHotel());
                addRoomToHotel(input.getIdHotel(), id);
            }else{
                //Si la habitacion ya esta vinculada a un hotel no se puede vincular a otro, por lo menos a dia d hoy
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Habitacion save = roomRepository.save(newRoom);
            return new ResponseEntity<>(save, HttpStatus.OK);
        } else {
            throw new BussinesRuleException("404","Not Found", "Error validacion, la habitacion con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
    }



    public ResponseEntity<?> post(Habitacion input){
        if (validRoom(input.getNumero(), input.getPrecioNoche())) {//si no es valida
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        input.getHuespedes().forEach(x -> x.setHabitacion(input));
        Habitacion save = roomRepository.save(input);
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }


    public ResponseEntity<?> deleteById (long id) throws BussinesRuleException{
        Optional<Habitacion> room = roomRepository.findById(id);
        if (room.isPresent()) {
            roomRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            throw new BussinesRuleException("404","Not Found", "Error validacion, la habitacion con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
    }


    public ResponseEntity<?> deleteAll(){
        roomRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
