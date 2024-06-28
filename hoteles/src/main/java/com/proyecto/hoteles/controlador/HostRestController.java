package com.proyecto.hoteles.controlador;

import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RestController;
import com.proyecto.hoteles.entidades.Huesped;
import com.proyecto.hoteles.repositorios.HostRepository;
import com.proyecto.hoteles.servicios.ServicioHuesped;
import com.proyecto.hoteles.utils.ListsUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Host API", description = "Esta API sirve para gestionar los huespedes")
@RestController
@RequestMapping("/huesped")
public class HostRestController {
    @Autowired
    HostRepository hostRepository;

    @Autowired
    ServicioHuesped servicio;

    @Operation(summary = "Devuelve una lista con todos los huespedes")
    @GetMapping()
    public ResponseEntity<?>/* List<Huesped> */ findAll() {
        // return hostRepository.findAll();
        if (!hostRepository.findAll().isEmpty()) {
            return new ResponseEntity<>(hostRepository.findAll(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @Operation(summary = "Devuelve el huesped con el id seleccionado")
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Optional<Huesped> host = hostRepository.findById(id);
        if (host.isPresent()) {
            return new ResponseEntity<>(host.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Permite actualizar un elemento completo")
    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable Long id, @RequestBody Huesped input) {
        Optional<Huesped> optionalHuesped = hostRepository.findById(id);
        if (optionalHuesped.isPresent()) {
            Huesped newHuesped = optionalHuesped.get();
            newHuesped.setNombre(input.getNombre());
            newHuesped.setApellido(input.getApellido());
            newHuesped.setDniPasaporte(input.getDniPasaporte());
            newHuesped.setFechaCheckin(input.getFechaCheckin());
            newHuesped.setFechaCheckout(input.getFechaCheckout());
            Huesped save = hostRepository.save(newHuesped);
            return new ResponseEntity<>(save, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Permite actualizar un campo concreto")
    @PatchMapping("/{id}")
    public Huesped patch(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
        return servicio.updateHostByFields(id, fields);
    }

    @Operation(summary = "Registra un huesped en la base de datos")
    @PostMapping()
    public ResponseEntity<?> post(@RequestBody Huesped input) {
        // un huesped no puede salir antes de haberse registrado ni puede salir antes de
        // haber entrado
        if (input.getFechaCheckout().isBefore(LocalDate.now())
                || input.getFechaCheckin().isAfter(input.getFechaCheckout())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Huesped save = hostRepository.save(input);
        return ResponseEntity.ok(save);
    }

    @Operation(summary = "Elimina el huesped con el id seleccionado")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        /*
         * hostRepository.deleteById(id);
         * return new ResponseEntity<>(HttpStatus.OK);
         */
        Optional<Huesped> host = hostRepository.findById(id);
        if (host.isPresent()) {
            hostRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Elimina todos los huespedes de la base de datos")
    @DeleteMapping("/full")
    public ResponseEntity<?> deleteAll() {
        hostRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Permite buscar un huesped filtrando por sus campos")
    @GetMapping("/filter")
    public List<Huesped> getByParams(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) LocalDate checkIn,
            @RequestParam(required = false) LocalDate checkOut) {
        List<Huesped> hostsByName = servicio.findByName(nombre);
        List<Huesped> hostsBySurname = servicio.findBySurname(apellido);
        List<Huesped> hostsByDocument = servicio.findByDniPassport(documento);
        List<Huesped> hostsByCheckin = new ArrayList<>();
        List<Huesped> hostsByCheckout = new ArrayList<>();

        Set<Huesped> hostsFound = new HashSet<>();
        List<Boolean> vaciaPorNotFound = new ArrayList<>();
        boolean p = false, q = false, r = false, s = false;
        if (p = nombre != null) {
            hostsFound.addAll(hostsByName);
            vaciaPorNotFound.add(p);
        }

        if (q = apellido != null) {
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsBySurname, vaciaPorNotFound);
            vaciaPorNotFound.add(q);
        }

        if (r = documento != null) {
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByDocument, vaciaPorNotFound);
            vaciaPorNotFound.add(r);
        }

        if (s = checkIn != null) {
            hostsByCheckin = servicio.findByCheckIn(checkIn);
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByCheckin, vaciaPorNotFound);
            vaciaPorNotFound.add(s);
        }

        if (checkOut != null) {
            hostsByCheckout = servicio.findByCheckOut(checkOut);
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByCheckout, vaciaPorNotFound);
        }

        return new ArrayList<>(hostsFound);
    }

}
