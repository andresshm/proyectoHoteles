package com.proyecto.hoteles.controlador;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.hoteles.entidades.Hotel;
import com.proyecto.hoteles.repositorios.HotelRepository;
import com.proyecto.hoteles.servicios.ServicioHotel;
import com.proyecto.hoteles.utils.ListsUtil;


@RestController
@RequestMapping("/hotel")
public class HotelRestController {
  @Autowired
    HotelRepository hotelRepository;

    @Autowired
    ServicioHotel servicio;

   
    @GetMapping()
    public List<Hotel> findAll() {
        return hotelRepository.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Optional<Hotel> hotel = hotelRepository.findById(id);
        if(hotel.isPresent()){
            return new ResponseEntity<>(hotel.get(), HttpStatus.OK); 
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable Long id, @RequestBody Hotel input) {
        Optional<Hotel> optionalhotel = hotelRepository.findById(id);
        if(optionalhotel.isPresent()){            
            Hotel newhotel = optionalhotel.get();
            newhotel.setNombre(input.getNombre());
            newhotel.setDireccion(input.getDireccion());
            newhotel.setEmail(input.getEmail());
            newhotel.setSitioWeb(input.getSitioWeb());
            newhotel.setTelefono(input.getTelefono());
            Hotel save = hotelRepository.save(newhotel);
            return new ResponseEntity<>(save, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PatchMapping("/{id}")
    public Hotel patch(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
        return servicio.updateHotelByFields(id, fields);     
    }

    @PostMapping()
    public ResponseEntity<?> post(@RequestBody Hotel input) {
        input.getHabitaciones().forEach(x -> x.setHotel(input));
        Hotel save = hotelRepository.save(input);
        return ResponseEntity.ok(save);
    }
    

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        hotelRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/full")
    public ResponseEntity<?> deleteAll(){
        hotelRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/filter")
    public List<Hotel> getByParams(
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) String direccion,
        @RequestParam(required = false) String telefono,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String web) {
            List<Hotel> hostsByName = servicio.findByName(nombre);
            List<Hotel> hostsByAddress = servicio.findByAddress(direccion);
            List<Hotel> hostsByPhone = servicio.findByPhone(telefono);
            List<Hotel> hostsByMail = servicio.findByMail(email);
            List<Hotel> hostsByWebsite = servicio.findByWebsite(web);

            Set<Hotel> hostsFound = new HashSet<>();
            if (nombre != null) {
                hostsFound.addAll(hostsByName);
            }

            if (direccion != null) {
                ListsUtil.interseccionSinListaVacia(hostsFound, hostsByAddress);
            }

            if (telefono != null) {
                ListsUtil.interseccionSinListaVacia(hostsFound, hostsByPhone);
            }

            if (email != null) {
                ListsUtil.interseccionSinListaVacia(hostsFound, hostsByMail);
            }

            if (web != null) {
                ListsUtil.interseccionSinListaVacia(hostsFound, hostsByWebsite);
            }
        
            return new ArrayList<>(hostsFound);
        }
    
}
