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

import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("/huesped")
public class HostRestController {
    @Autowired
    HostRepository hostRepository;

    @Autowired
    ServicioHuesped servicio;


    @GetMapping()
    public List<Huesped> findAll() {
        return hostRepository.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Optional<Huesped> host = hostRepository.findById(id);
        if(host.isPresent()){
            return new ResponseEntity<>(host.get(), HttpStatus.OK); 
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable Long id, @RequestBody Huesped input) {
        Optional<Huesped> optionalHuesped = hostRepository.findById(id);
        if(optionalHuesped.isPresent()){
            Huesped newHuesped = optionalHuesped.get();
            newHuesped.setNombre(input.getNombre());
            newHuesped.setApellido(input.getApellido());
            newHuesped.setDniPasaporte(input.getDniPasaporte());
            newHuesped.setFechaCheckin(input.getFechaCheckin());
            newHuesped.setFechaCheckout(input.getFechaCheckout());
            Huesped save = hostRepository.save(newHuesped);
            return new ResponseEntity<>(save, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PatchMapping("/{id}")
    public Huesped patch(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
        return servicio.updateHostByFields(id, fields);     
    }



    @PostMapping()
    public ResponseEntity<?> post(@RequestBody Huesped input) {
        //un huesped no puede salir antes de haberse registrado ni puede salir antes de haber entrado
        if(input.getFechaCheckout().isBefore(LocalDate.now()) || input.getFechaCheckin().isAfter(input.getFechaCheckout())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Huesped save = hostRepository.save(input);
        return ResponseEntity.ok(save);
    }
    

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        hostRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/full")
    public ResponseEntity<?> deleteAll(){
        hostRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }


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
            if (nombre != null) {
                hostsFound.addAll(hostsByName);
            }

            if (apellido != null) {
                ListsUtil.interseccionSinListaVacia(hostsFound, hostsBySurname);
            }

            if (documento != null) {
                ListsUtil.interseccionSinListaVacia(hostsFound, hostsByDocument);
            }

            if (checkIn != null) {
                hostsByCheckin = servicio.findByCheckIn(checkIn);
                ListsUtil.interseccionSinListaVacia(hostsFound, hostsByCheckin);
            }

            if (checkOut != null) {
                hostsByCheckout = servicio.findByCheckOut(checkOut);
                ListsUtil.interseccionSinListaVacia(hostsFound, hostsByCheckout);
            }
        
            return new ArrayList<>(hostsFound);
        }

}
