package com.proyecto.hoteles.controlador;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.proyecto.hoteles.entidades.Servicio;
import com.proyecto.hoteles.repositorios.ServiceRepository;

@RestController
@RequestMapping("/servicio")
public class ServiceRestController {
  @Autowired
    ServiceRepository serviceRepository;

    @GetMapping()
    public List<Servicio> findAll() {
        return serviceRepository.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Optional<Servicio> service = serviceRepository.findById(id);
        if(service.isPresent()){
            return new ResponseEntity<>(service.get(), HttpStatus.OK); 
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable Long id, @RequestBody Servicio input) {
        Optional<Servicio> optionalService = serviceRepository.findById(id);
        if(optionalService.isPresent()){
            Servicio newService = optionalService.get();
            newService.setNombre(input.getNombre());
            newService.setDescripcion(input.getDescripcion());
            Servicio save = serviceRepository.save(newService);
            return new ResponseEntity<>(save, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping()
    public ResponseEntity<?> post(@RequestBody Servicio input) {
        //input.getHoteles().forEach(x -> x.setServicios());
        Servicio save = serviceRepository.save(input);
        return ResponseEntity.ok(save);
    }
    

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        serviceRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping("/full")
    public ResponseEntity<?> deleteAll(){
        serviceRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
