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

import com.proyecto.hoteles.entidades.Habitacion;
import com.proyecto.hoteles.repositorios.RoomRepository;

@RestController
@RequestMapping("/habitacion")
public class RoomRestController {
    @Autowired
    RoomRepository roomRepository;


    @GetMapping()
    public ResponseEntity<List<Habitacion>> findAll() {
        List<Habitacion> rooms = roomRepository.findAll();
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Optional<Habitacion> room = roomRepository.findById(id);
        if(room.isPresent()){
            return new ResponseEntity<>(room.get(), HttpStatus.OK); 
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable Long id, @RequestBody Habitacion input) {
        Optional<Habitacion> optionalRoom = roomRepository.findById(id);
        if(optionalRoom.isPresent()){
            Habitacion newRoom = optionalRoom.get();
            //Inicializamos los atributos
            newRoom.setNumero(input.getNumero());
            newRoom.setPrecioNoche(input.getPrecioNoche());
            newRoom.setTipo(input.getTipo());
            //newRoom.setHuespedes(input.getHuespedes());
            Habitacion save = roomRepository.save(newRoom);
            return new ResponseEntity<>(save, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping()
    public ResponseEntity<Habitacion> post(@RequestBody Habitacion input) {
        input.getHuespedes().forEach(x -> x.setHabitacion(input));
        Habitacion save = roomRepository.save(input);
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }
    

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        roomRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/full")
    public ResponseEntity<?> deleteAll(){
        roomRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
