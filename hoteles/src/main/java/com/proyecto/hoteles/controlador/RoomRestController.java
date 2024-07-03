package com.proyecto.hoteles.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.proyecto.hoteles.entidades.Habitacion;
import com.proyecto.hoteles.exception.BussinesRuleException;
import com.proyecto.hoteles.repositorios.RoomRepository;
import com.proyecto.hoteles.servicios.ServicioHabitacion;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Room API", description = "Esta API sirve para gestionar las habitaciones")
@RestController
@RequestMapping("/habitacion")
public class RoomRestController {
    @Autowired
    RoomRepository roomRepository;

    @Autowired
    ServicioHabitacion servicio;

    @Operation(summary = "Devuelve una lista con todos las habitaciones")
    @GetMapping()
    public ResponseEntity<?> findAll() {
        /*
         * List<Habitacion> rooms = roomRepository.findAll();
         * return new ResponseEntity<>(rooms, HttpStatus.OK);
         */
        /*if (!roomRepository.findAll().isEmpty()) {
            return new ResponseEntity<>(roomRepository.findAll(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }*/
        return servicio.getAll();
    }

    @Operation(summary = "Devuelve la habitacion con el id seleccionado")
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) throws BussinesRuleException {
        /*Optional<Habitacion> room = roomRepository.findById(id);
        if (room.isPresent()) {
            return new ResponseEntity<>(room.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }*/
        return servicio.getById(id);
    }

    @Operation(summary = "Permite actualizar un elemento completo")
    @PutMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Room updated successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request, check number is numeric OR price is positive")
    })
    public ResponseEntity<?> put(@PathVariable Long id, @RequestBody Habitacion input) throws BussinesRuleException {
        /*Optional<Habitacion> optionalRoom = roomRepository.findById(id);
        if (optionalRoom.isPresent()) {
            Habitacion newRoom = optionalRoom.get();
            // Inicializamos los atributos
            if(!servicio.validRoom(input.getNumero(), input.getPrecioNoche())){//si es valida...
            newRoom.setNumero(input.getNumero());
            newRoom.setPrecioNoche(input.getPrecioNoche());
            newRoom.setTipo(input.getTipo());
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            // newRoom.setIdHotel(input.getHotel().getId());
            // newRoom.setHuespedes(input.getHuespedes());
            Habitacion save = roomRepository.save(newRoom);
            return new ResponseEntity<>(save, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }*/
        return servicio.put(id, input);
    }

    @Operation(summary = "Permite actualizar un campo concreto")
    @PatchMapping("/{id}")
    public Habitacion patch(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
        return servicio.updateRoomByFields(id, fields);
    }

    @Operation(summary = "Registra una habitacion en la base de datos")
    @PostMapping()
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Room added successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request, check number is numeric OR price is positive")
    })
    public ResponseEntity<?> post(@RequestBody Habitacion input) {
        /*if (servicio.validRoom(input.getNumero(), input.getPrecioNoche())) {//si no es valida
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        input.getHuespedes().forEach(x -> x.setHabitacion(input));
        Habitacion save = roomRepository.save(input);
        return new ResponseEntity<>(save, HttpStatus.CREATED);*/
        return servicio.post(input);
    }

    @Operation(summary = "Elimina la habitacion con el id seleccionado")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws BussinesRuleException {
        /*
         * roomRepository.deleteById(id);
         * return new ResponseEntity<>(HttpStatus.OK);
         */
        /*Optional<Habitacion> room = roomRepository.findById(id);
        if (room.isPresent()) {
            roomRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }*/
        return servicio.deleteById(id);
    }

    @Operation(summary = "Elimina todos las habitaciones de la base de datos")
    @DeleteMapping("/full")
    public ResponseEntity<?> deleteAll() {
        /*roomRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);*/
        return servicio.deleteAll();
    }

    @Operation(summary = "Permite buscar una habitacion filtrando por sus campos")
    @GetMapping("/filter")
    public List<Habitacion> getByParams(
            @RequestParam(required = false) String numero,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Float precio) {
                return servicio.filter(numero, tipo, precio);
        /*List<Habitacion> hostsByNumber = servicio.findByNumber(numero);
        List<Habitacion> hostsByType = servicio.findByType(tipo);

        // float no puede ser null porque es un tipo primitivo, pero Float si
        // si es nulo, asignamos 0$ a precio para que no encuentre ninguna habitacion
        // y no entorpezca la busqueda 
        Float floatWrapper = precio;
        if (floatWrapper == null) {
            precio = 0.0f;
        } else {
            floatWrapper = precio;
        }
        List<Habitacion> hostsByPrice = servicio.findByPrice(precio);
        List<Boolean> vaciaPorNotFound = new ArrayList<>();
        boolean p = false, q = false;
        Set<Habitacion> hostsFound = new HashSet<>();
        if (p = numero != null) {
            hostsFound.addAll(hostsByNumber);
            vaciaPorNotFound.add(p);
        }

        if (q = tipo != null) {
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByType, vaciaPorNotFound);
            vaciaPorNotFound.add(q);
        }

        if (floatWrapper != null) {
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByPrice, vaciaPorNotFound);
        }

        return new ArrayList<>(hostsFound);*/

    }

    /*@Operation(summary = "Permite añadir huespedes a la habitacion")
    @PostMapping("/{roomId}/hosts/{hostId}")
    public void addHostToRoom(@PathVariable Long roomId, @PathVariable Long hostId) {
        servicio.addHostToRoom(roomId, hostId);
    }*/

}
