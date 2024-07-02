package com.proyecto.hoteles.servicios;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.stereotype.Service;

import com.proyecto.hoteles.entidades.Habitacion;
import com.proyecto.hoteles.entidades.Huesped;
import com.proyecto.hoteles.exception.BussinesRuleException;
import com.proyecto.hoteles.repositorios.HostRepository;
import com.proyecto.hoteles.repositorios.RoomRepository;
import com.proyecto.hoteles.utils.ListsUtil;

@Service
public class ServicioHuesped {

    @Autowired
    private HostRepository hostRepository;

    @Autowired
    private RoomRepository roomRepository;

    public Huesped updateHostByFields(long id, Map<String, Object> fields) {
        Optional<Huesped> optHost = hostRepository.findById(id);

        if (optHost.isPresent()) {
            fields.forEach((key, value) -> {
                // Falta comprobar que la fecha de checkout no sean nunca inferior a la de
                // checkin 
                if (key.equals("fechaCheckin")) {
                    String dateString = (String) value;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("d-M-yyyy");
                    LocalDate fecha = null;//LocalDate.parse(dateString, formatter);
                    try {
                        fecha = LocalDate.parse(dateString, formatter);
                    } catch (DateTimeParseException e) {
                        fecha = LocalDate.parse(dateString, formatter2);
                    }
                    optHost.get().setFechaCheckin(fecha);

                } else if (key.equals("fechaCheckout")) {
                    String dateString = (String) value;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("d-M-yyyy");
                    LocalDate fecha = null;//LocalDate.parse(dateString, formatter);
                    try {
                        fecha = LocalDate.parse(dateString, formatter);
                    } catch (DateTimeParseException e) {
                        fecha = LocalDate.parse(dateString, formatter2);
                    }
                    optHost.get().setFechaCheckout(fecha);

                } else {
                    // Esto esta bien, lo de arriba es tremenda ñapa
                    Field field = ReflectionUtils.findField(Huesped.class, key);
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, optHost.get(), value);
                }

            });

            return hostRepository.save(optHost.get());
        } else {
            return null;
        }
    }

    public List<Huesped> findByName(String name) {
        return hostRepository.findAll().stream()
                .filter(h -> h.getNombre().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    public List<Huesped> findBySurname(String surname) {
        return hostRepository.findAll().stream()
                .filter(h -> h.getApellido().equalsIgnoreCase(surname))
                .collect(Collectors.toList());
        /*for (Huesped h : hostRepository.findAll()) {
            if (h.getApellido().equalsIgnoreCase(surname)) {
                hosts.add(h);
            }
        }*/
        
    }

    public List<Huesped> findByDniPassport(String document) {
        return hostRepository.findAll().stream()
                .filter(h -> h.getDniPasaporte().equalsIgnoreCase(document))
                .collect(Collectors.toList());
        /*for (Huesped h : hostRepository.findAll()) {
            if (h.getDniPasaporte().equalsIgnoreCase(document)) {
                hosts.add(h);
            }
        }*/
    }

    public List<Huesped> findByCheckIn(LocalDate checkIn) {
        
        return hostRepository.findAll().stream()
        .filter(h -> h.getFechaCheckin().equals(checkIn))
        .collect(Collectors.toList());
        /*for (Huesped h : hostRepository.findAll()) {
            if (h.getFechaCheckin().isAfter(checkIn)) {
                hosts.add(h);
            }
        }*/
    }

    public List<Huesped> findByCheckOut(LocalDate checkOut) {
        return hostRepository.findAll().stream()
        .filter(h -> h.getFechaCheckout().equals(checkOut))
        .collect(Collectors.toList());
        /*for (Huesped h : hostRepository.findAll()) {
            if (h.getFechaCheckin().isAfter(checkOut)) {
                hosts.add(h); 
            }
        }*/
    }



    public List<Huesped> filter(String nombre, String apellido, String documento, String checkIn, String checkOut) throws BussinesRuleException{
        List<Huesped> hostsByName = new ArrayList<>();
        List<Huesped> hostsBySurname = new ArrayList<>();
        List<Huesped> hostsByDocument = new ArrayList<>();
        List<Huesped> hostsByCheckin = new ArrayList<>();
        List<Huesped> hostsByCheckout = new ArrayList<>();
        Set<Huesped> hostsFound = new HashSet<>();
        List<Boolean> vaciaPorNotFound = new ArrayList<>();
        boolean p = false, q = false, r = false, s = false;

        LocalDate checkInDate = null;
        LocalDate checkOutDate = null;



        if (p = nombre != null) {
            hostsByName = findByName(nombre);
            hostsFound.addAll(hostsByName);
            vaciaPorNotFound.add(p);
        }

        if (q = apellido != null) {
            hostsBySurname = findBySurname(apellido);
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsBySurname, vaciaPorNotFound);
            vaciaPorNotFound.add(q);
        }

        if (r = documento != null) {
            hostsByDocument = findByDniPassport(documento);
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByDocument, vaciaPorNotFound);
            vaciaPorNotFound.add(r);
        }

        if (s = checkIn != null) {
            checkInDate = stringToDate(checkIn);
            hostsByCheckin = findByCheckIn(checkInDate);
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByCheckin, vaciaPorNotFound);
            vaciaPorNotFound.add(s);
        }

        if (checkOut != null) {
            checkOutDate = stringToDate(checkOut);
            hostsByCheckout = findByCheckOut(checkOutDate);
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByCheckout, vaciaPorNotFound);
        }

        return new ArrayList<>(hostsFound);
    }


    private LocalDate stringToDate(String fecha) throws BussinesRuleException{
        /*DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[dd-MM-yyyy][d-M-yyyy]");
        LocalDate f =  LocalDate.parse(fecha, formatter);
        return f;*/

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[dd-MM-yyyy][d-M-yyyy]");
        LocalDate f = null;
        try {
            f = LocalDate.parse(fecha, formatter);
            return f;
        } catch (Exception e) {
            throw new BussinesRuleException("400", "Bad request", "Error al introducir la fecha. El formato es: [dd-mm-yyyy]", HttpStatus.BAD_REQUEST);
        }
        
    }



    public ResponseEntity<?> getAll(){
        if (!hostRepository.findAll().isEmpty()) {
            return new ResponseEntity<>(hostRepository.findAll(), HttpStatus.OK);
        } else {
            //podria hacer aqui otra excepcion pero es mucha farandolo xq habria q hacerlo para todos, mas adelante
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }



    public ResponseEntity<?> getById(long id) throws BussinesRuleException{
        Optional<Huesped> host = hostRepository.findById(id);
        if (host.isPresent()) {
            return new ResponseEntity<>(host.get(), HttpStatus.OK);
        } else {
            throw new BussinesRuleException("404", "Not Found", "Error validacion, el huesped con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
    }


private void addHostToRoom(long idRoom, long idHost){
    Habitacion habitacion = roomRepository.findById(idRoom).orElseThrow(() -> new RuntimeException("Host not found"));
    Huesped host = hostRepository.findById(idHost).orElseThrow(() -> new RuntimeException("Host not found"));
    
    habitacion.addHost(host);
    roomRepository.save(habitacion);
}


    public ResponseEntity<?> put(long id, Huesped input) throws BussinesRuleException{
        Optional<Huesped> optionalHuesped = hostRepository.findById(id);
        if (optionalHuesped.isPresent()) {
            Huesped newHuesped = optionalHuesped.get();
            newHuesped.setNombre(input.getNombre());
            newHuesped.setApellido(input.getApellido());
            newHuesped.setDniPasaporte(input.getDniPasaporte());
            newHuesped.setFechaCheckin(input.getFechaCheckin());
            newHuesped.setFechaCheckout(input.getFechaCheckout());
            //si cambio el idHabitacion el huesped se asocia a esa habitacion
            if(newHuesped.getIdHabitacion()==0){
                newHuesped.setIdHabitacion(input.getIdHabitacion());
                newHuesped.setHabitacion(input.getHabitacion());
                addHostToRoom(input.getIdHabitacion(), id);
            }else{
                //Si el huesped ya esta vinculado a una habitacion no se puede vincular a otra
                throw new BussinesRuleException("400", "Bad request", "Error en la peticion", HttpStatus.BAD_REQUEST);
            }
            Huesped save = hostRepository.save(newHuesped);
            return new ResponseEntity<>(save, HttpStatus.OK);
        } else {
            throw new BussinesRuleException("404", "Not Found", "Error validacion, el huesped con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
    }



    public ResponseEntity<?> post(Huesped input) throws BussinesRuleException{
        
        if (input.getFechaCheckout().isBefore(LocalDate.now())
                || input.getFechaCheckin().isAfter(input.getFechaCheckout())) {
            throw new BussinesRuleException("400", "Bad request", "Error en la peticion", HttpStatus.BAD_REQUEST);
        }
        
        Huesped save = hostRepository.save(input);
        return ResponseEntity.ok(save);
    }


    public ResponseEntity<?> deleteById (long id) throws BussinesRuleException{
        Optional<Huesped> host = hostRepository.findById(id);
        if (host.isPresent()) {
            hostRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            throw new BussinesRuleException("404", "Not Found", "Error validacion, el huesped con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
    }


    public ResponseEntity<?> deleteAll(){
        hostRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
