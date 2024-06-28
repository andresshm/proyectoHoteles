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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Hotel API", description = "Esta API sirve para gestionar los hoteles")
@RestController
@RequestMapping("/hotel")
public class HotelRestController {
    @Autowired
    HotelRepository hotelRepository;

    @Autowired
    ServicioHotel servicio;

    @Operation(summary = "Devuelve una lista con todos los hoteles")
    @GetMapping()
    public ResponseEntity<?>/* List<Hotel> */ findAll() {
        // return hotelRepository.findAll();
        if (!hotelRepository.findAll().isEmpty()) {
            return new ResponseEntity<>(hotelRepository.findAll(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @Operation(summary = "Devuelve el hotel con el id seleccionado")
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Optional<Hotel> hotel = hotelRepository.findById(id);
        if (hotel.isPresent()) {
            return new ResponseEntity<>(hotel.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Permite actualizar un elemento completo")
    @PutMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hotel updated successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request, check phone is numeric")
    })
    public ResponseEntity<?> put(@PathVariable Long id, @RequestBody Hotel input) {
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
            Hotel save = hotelRepository.save(newhotel);
            return new ResponseEntity<>(save, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Permite actualizar un campo concreto")
    @PatchMapping("/{id}")
    public Hotel patch(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
        return servicio.updateHotelByFields(id, fields);
    }

    @Operation(summary = "Registra un hotel en la base de datos")
    @PostMapping()
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Hotel added successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request, check phone is numeric")
    })
    public ResponseEntity<?> post(@RequestBody Hotel input) {
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

    @Operation(summary = "Elimina el hotel con el id seleccionado")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        /*
         * hotelRepository.deleteById(id);
         * return new ResponseEntity<>(HttpStatus.OK);
         */
        Optional<Hotel> hotel = hotelRepository.findById(id);
        if (hotel.isPresent()) {
            hotelRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Elimina todos los hoteles de la base de datos")
    @DeleteMapping("/full")
    public ResponseEntity<?> deleteAll() {
        hotelRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Permite buscar un hotel filtrando por sus campos")
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
        List<Boolean> vaciaPorNotFound = new ArrayList<>();
        boolean p = false, q = false, r = false, s = false;
        Set<Hotel> hostsFound = new HashSet<>();
        if (p = nombre != null) {
            hostsFound.addAll(hostsByName);
            vaciaPorNotFound.add(p);
        }

        if (q = direccion != null) {
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByAddress, vaciaPorNotFound);
            vaciaPorNotFound.add(q);
        }

        if (r = telefono != null) {
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByPhone, vaciaPorNotFound);
            vaciaPorNotFound.add(r);
        }

        if (s = email != null) {
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByMail, vaciaPorNotFound);
            vaciaPorNotFound.add(s);
        }

        if (web != null) {
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByWebsite, vaciaPorNotFound);
        }

        return new ArrayList<>(hostsFound);
    }

    @Operation(summary = "Permite añadir habitaciones al hotel")
    @PostMapping("/{hotelId}/rooms/{roomId}")
    public void addRoomToHotel(@PathVariable Long hotelId, @PathVariable Long roomId) {
        servicio.addRoomToHotel(hotelId, roomId);
    }

    @Operation(summary = "Permite añadir servicios al hotel")
    @PostMapping("/{hotelId}/services/{serviceId}")
    public void addServiceToHotel(@PathVariable Long hotelId, @PathVariable Long serviceId) {
        servicio.addServiceToHotel(hotelId, serviceId);
    }

}
