package com.proyecto.hoteles.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = "*"/*"http://localhost:4200" */)
@RequestMapping("/habitacion")
public class RoomRestController {
    @Autowired
    RoomRepository roomRepository;

    @Autowired
    ServicioHabitacion servicio;

    /**
     * GET-ALL
     * 
     * @return
     */
    @Operation(summary = "Devuelve una lista con todos las habitaciones")
    @GetMapping()
    public ResponseEntity<?> findAll() {
        return servicio.getAll();
    }

    /**
     * GET-BY-ID
     * 
     * @param id
     * @return
     * @throws BussinesRuleException
     */
    @Operation(summary = "Devuelve la habitacion con el id seleccionado")
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) throws BussinesRuleException {
        return servicio.getById(id);
    }

    /**
     * PUT
     * 
     * @param id
     * @param input
     * @return
     * @throws BussinesRuleException
     */
    @Operation(summary = "Permite actualizar un elemento completo")
    @PutMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, check number is numeric OR price is positive")
    })
    public ResponseEntity<?> put(@PathVariable Long id, @RequestBody Habitacion input) throws BussinesRuleException {
        return servicio.put(id, input);
    }

    /**
     * PATCH
     * 
     * @param id
     * @param fields
     * @return
     */
    @Operation(summary = "Permite actualizar un campo concreto")
    @PatchMapping("/{id}")
    public Habitacion patch(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
        return servicio.updateRoomByFields(id, fields);
    }

    /**
     * POST
     * 
     * @param input
     * @return
     * @throws BussinesRuleException
     */
    @Operation(summary = "Registra una habitacion en la base de datos")
    @PostMapping()
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Room added successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, check number is numeric OR price is positive")
    })
    public ResponseEntity<?> post(@RequestBody Habitacion input) throws BussinesRuleException {
        return servicio.post(input);
    }

    /**
     * DELETE--BY-ID
     * 
     * @param id
     * @return
     * @throws BussinesRuleException
     */
    @Operation(summary = "Elimina la habitacion con el id seleccionado")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws BussinesRuleException {
        return servicio.deleteById(id);
    }

    /**
     * DELETE-ALL
     * 
     * @return
     */
    @Operation(summary = "Elimina todos las habitaciones de la base de datos")
    @DeleteMapping("/full")
    public ResponseEntity<?> deleteAll() {
        return servicio.deleteAll();
    }

    /**
     * FILTER
     * 
     * @param numero
     * @param tipo
     * @param precio
     * @return
     */
    @Operation(summary = "Permite buscar una habitacion filtrando por sus campos")
    @GetMapping("/filter")
    public List<Habitacion> getByParams(
            @RequestParam(required = false) String numero,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Float precio) {
        return servicio.filter(numero, tipo, precio);
    }

}
