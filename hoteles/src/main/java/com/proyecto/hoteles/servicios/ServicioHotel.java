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
import com.proyecto.hoteles.entidades.Servicio;
import com.proyecto.hoteles.exception.BussinesRuleException;
import com.proyecto.hoteles.repositorios.HotelRepository;
import com.proyecto.hoteles.repositorios.RoomRepository;
import com.proyecto.hoteles.repositorios.ServiceRepository;
import com.proyecto.hoteles.utils.ListsUtil;

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



public List<Hotel> filter(String nombre, String direccion, String telefono, String email, String web){
    List<Hotel> hostsByName = new ArrayList<>();
        List<Hotel> hostsByAddress = new ArrayList<>();
        List<Hotel> hostsByPhone = new ArrayList<>();
        List<Hotel> hostsByMail = new ArrayList<>();
        List<Hotel> hostsByWebsite = new ArrayList<>();
        List<Boolean> vaciaPorNotFound = new ArrayList<>();
        boolean p = false, q = false, r = false, s = false;
        Set<Hotel> hostsFound = new HashSet<>();
        if (p = nombre != null) {
            hostsByName = findByName(nombre);
            hostsFound.addAll(hostsByName);
            vaciaPorNotFound.add(p);
        }

        if (q = direccion != null) {
            hostsByAddress = findByAddress(direccion);
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByAddress, vaciaPorNotFound);
            vaciaPorNotFound.add(q);
        }

        if (r = telefono != null) {
            hostsByPhone = findByPhone(telefono);
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByPhone, vaciaPorNotFound);
            vaciaPorNotFound.add(r);
        }

        if (s = email != null) {
            hostsByMail = findByMail(email);
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByMail, vaciaPorNotFound);
            vaciaPorNotFound.add(s);
        }

        if (web != null) {
            hostsByWebsite = findByWebsite(web);
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByWebsite, vaciaPorNotFound);
        }

        return new ArrayList<>(hostsFound);
}


@Transactional
public void addRoomToHotel(long idHotel, long idRoom){
    Hotel hotel = hotelRepository.findById(idHotel).orElseThrow(() -> new RuntimeException("Hotel not found"));
    Habitacion habitacion = roomRepository.findById(idRoom).orElseThrow(() -> new RuntimeException("Room not found"));

    hotel.addRoom(habitacion);
    hotelRepository.save(hotel);
}


@Transactional
private void addServiceToHotel(long idHotel, List<Long> idServices){
    Hotel hotel = hotelRepository.findById(idHotel).orElseThrow(() -> new RuntimeException("Hotel not found"));
    for(long id : idServices){
        Servicio service = serviceRepository.findById(id).orElseThrow(() -> new RuntimeException("Service not found"));
        hotel.addService(service);
    }
    
    hotelRepository.save(hotel);
}


 public ResponseEntity<?> getAll(){
        if (!hotelRepository.findAll().isEmpty()) {
            return new ResponseEntity<>(hotelRepository.findAll(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }



    public ResponseEntity<?> getById(long id) throws BussinesRuleException{
        Optional<Hotel> hotel = hotelRepository.findById(id);
        if (hotel.isPresent()) {
            return new ResponseEntity<>(hotel.get(), HttpStatus.OK);
        } else {
            throw new BussinesRuleException("404","Not Found", "Error validacion, el hotel con id " + id + " no existe", HttpStatus.NOT_FOUND);
            //return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    public ResponseEntity<?> put(long id, Hotel input) throws BussinesRuleException{
        Optional<Hotel> optionalhotel = hotelRepository.findById(id);
        if (optionalhotel.isPresent()) {
            Hotel newhotel = optionalhotel.get();
            if(!input.getTelefono().matches("^\\d+( \\d+)*$")){//lo suyo seria poner que hasta 9 nums
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            newhotel.setNombre(input.getNombre());
            newhotel.setDireccion(input.getDireccion());
            newhotel.setEmail(input.getEmail());
            newhotel.setSitioWeb(input.getSitioWeb());
            newhotel.setTelefono(input.getTelefono());
            // newhotel.setHabitaciones(input.getHabitaciones());
            newhotel.setServices(input.getServices());
            addServiceToHotel(id, input.getServices());

            Hotel save = hotelRepository.save(newhotel);
            return new ResponseEntity<>(save, HttpStatus.OK);
        } else {
            throw new BussinesRuleException("404","Not Found", "Error validacion, el hotel con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
    }



    public ResponseEntity<?> post(Hotel input){
        if(!input.getTelefono().matches("^\\d+( \\d+)*$")){//lo suyo seria poner que hasta 9 nums
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        input.getHabitaciones().forEach(x -> x.setHotel(input));
        input.getHabitaciones().forEach(x -> x.getHuespedes().forEach(z -> z.setHabitacion(x)));

        input.getServicios().forEach(x -> {
            List<Hotel> hoteles = new ArrayList<>();
            hoteles.add(input);
            x.setHoteles(hoteles);

        });
        Hotel save = hotelRepository.save(input);
        return ResponseEntity.ok(save);
    }


    public ResponseEntity<?> deleteById (long id) throws BussinesRuleException{
        Optional<Hotel> hotel = hotelRepository.findById(id);
        if (hotel.isPresent()) {
            hotelRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            throw new BussinesRuleException("404", "Not Found","Error validacion, el hotel con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
    }


    public ResponseEntity<?> deleteAll(){
        hotelRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
