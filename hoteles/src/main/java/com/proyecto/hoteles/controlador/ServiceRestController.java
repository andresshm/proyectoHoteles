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

import com.proyecto.hoteles.entidades.Servicio;
import com.proyecto.hoteles.repositorios.ServiceRepository;
import com.proyecto.hoteles.servicios.ServicioService;
import com.proyecto.hoteles.utils.ListsUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Service API", description = "Esta API sirve para gestionar los servicios")
@RestController
@RequestMapping("/servicio")
public class ServiceRestController {
    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    ServicioService servicio;

    @Operation(summary = "Devuelve una lista con todos los servicios")
    @GetMapping()
    public ResponseEntity<?>/* List<Servicio> */ findAll() {
        // return serviceRepository.findAll();
        if (!serviceRepository.findAll().isEmpty()) {
            return new ResponseEntity<>(serviceRepository.findAll(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }

    @Operation(summary = "Devuelve el servicio con el id seleccionado")
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Optional<Servicio> service = serviceRepository.findById(id);
        if (service.isPresent()) {
            return new ResponseEntity<>(service.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Permite actualizar un elemento completo")
    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable Long id, @RequestBody Servicio input) {
        Optional<Servicio> optionalService = serviceRepository.findById(id);
        if (optionalService.isPresent()) {
            Servicio newService = optionalService.get();
            newService.setNombre(input.getNombre());
            newService.setDescripcion(input.getDescripcion());
            Servicio save = serviceRepository.save(newService);
            return new ResponseEntity<>(save, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Permite actualizar un campo concreto")
    @PatchMapping("/{id}")
    public Servicio patch(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
        return servicio.updateServiceByFields(id, fields);
    }

    @Operation(summary = "Registra un servicio en la base de datos")
    @PostMapping()
    public ResponseEntity<?> post(@RequestBody Servicio input) {
        Servicio save = serviceRepository.save(input);
        return ResponseEntity.ok(save);
    }

    @Operation(summary = "Elimina el servicio con el id seleccionado")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Servicio> service = serviceRepository.findById(id);
        if (service.isPresent()) {
            serviceRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @Operation(summary = "Elimina todos los servicios de la base de datos")
    @DeleteMapping("/full")
    public ResponseEntity<?> deleteAll() {
        serviceRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Permite buscar un servicio filtrando por sus campos")
    @GetMapping("/filter")
    public List<Servicio> getByParams(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String descripcion) {
        List<Servicio> hostsByName = servicio.findByName(nombre);
        List<Boolean> vaciaPorNotFound = new ArrayList<>();
        boolean p = false;

        Set<Servicio> hostsFound = new HashSet<>();
        if (p = nombre != null) {
            hostsFound.addAll(hostsByName);
            vaciaPorNotFound.add(p);
        }

        if (descripcion != null) {
            List<Servicio> hostsByDescription = servicio.findByDescription(descripcion);
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByDescription, vaciaPorNotFound);
        }

        return new ArrayList<>(hostsFound);
    }

    @Operation(summary = "Permite añadir hoteles como clientes del servicio")
    @PostMapping("/{serviceId}/hotels/{hotelId}")
    public void addHotelToService(@PathVariable Long serviceId, @PathVariable Long hotelId) {
        servicio.addHotelToService(serviceId, hotelId);
    }
}
