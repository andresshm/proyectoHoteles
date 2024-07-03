package com.proyecto.hoteles.servicios;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
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

    public Huesped updateHostByFields(long id, Map<String, Object> fields)  {
        Optional<Huesped> optHost = hostRepository.findById(id);

        if (optHost.isPresent()) {
            fields.forEach((key, value) -> {
                //FALTA COMPROBAR QUE LA FECHA ESTE BIEN FORMATEADA, PERO NO SE NI COMO
                /*Estos dos if se encargan de parsear las fechas */
                if (key.equals("fechaCheckin")) {
                    String dateString = (String) value;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("d-M-yyyy H:m");
                    LocalDateTime fecha = null;
                    try {
                        fecha = LocalDateTime.parse(dateString, formatter);
                    } catch (DateTimeParseException e) {
                        fecha = LocalDateTime.parse(dateString, formatter2);
                    }
                    optHost.get().setFechaCheckin(fecha);

                } else if (key.equals("fechaCheckout")) {
                    String dateString = (String) value;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("d-M-yyyy H:m");
                    LocalDateTime fecha = null;
                    try {
                        fecha = LocalDateTime.parse(dateString, formatter);
                    } catch (DateTimeParseException e) {
                        fecha = LocalDateTime.parse(dateString, formatter2);                       
                    }

                    /*Esto comprueba que la fecha de checkout no sea antes de este momento
                     * ni antes de la fecha de salida. No se porque no tira la excepcion en el swagger pero algo hara
                     */
                    if(optHost.get().getFechaCheckin().isAfter(fecha) || fecha.isBefore(LocalDateTime.now())){
                        try {
                            throw new BussinesRuleException("400", "Bad request", "Error al introducir la fecha. Introdujo una fecha de salida anterior a la fecha de entrada", HttpStatus.BAD_REQUEST);
                        } catch (BussinesRuleException e) {
                            e.printStackTrace();
                        }
                    }else{

                        optHost.get().setFechaCheckout(fecha);
                    }


                }else if(key.equals("idHabitacion")){
                    /*Este else-if es para gestionar que un huesped solo este en una habitacion
                     * y que se pueda cambiar desde el PATCH
                     */
                    if(Long.parseLong(value.toString())!=0){
                        optHost.get().setIdHabitacion(Long.parseLong(value.toString()));
                        addHostToRoom(optHost.get().getIdHabitacion(), optHost.get().getId());
                    } 
                }else {
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
    }

    public List<Huesped> findByDniPassport(String document) {
        return hostRepository.findAll().stream()
                .filter(h -> h.getDniPasaporte().equalsIgnoreCase(document))
                .collect(Collectors.toList());
    }

    public List<Huesped> findByCheckIn(LocalDateTime checkIn) {
        
        return hostRepository.findAll().stream()
        .filter(h -> h.getFechaCheckin().equals(checkIn))
        .collect(Collectors.toList());
    }

    public List<Huesped> findByCheckOut(LocalDateTime checkOut) {
        return hostRepository.findAll().stream()
        .filter(h -> h.getFechaCheckout().equals(checkOut))
        .collect(Collectors.toList());
    }


//llevar al final este y los de arriba
    public List<Huesped> filter(String nombre, String apellido, String documento, String checkIn, String checkOut) throws BussinesRuleException{
        List<Huesped> hostsByName = new ArrayList<>();
        List<Huesped> hostsBySurname = new ArrayList<>();
        List<Huesped> hostsByDocument = new ArrayList<>();
        List<Huesped> hostsByCheckin = new ArrayList<>();
        List<Huesped> hostsByCheckout = new ArrayList<>();
        Set<Huesped> hostsFound = new HashSet<>();
        List<Boolean> vaciaPorNotFound = new ArrayList<>();
        boolean p = false, q = false, r = false, s = false;

        LocalDateTime checkInDate = null;
        LocalDateTime checkOutDate = null;



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


    private LocalDateTime stringToDate(String fecha) throws BussinesRuleException{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[dd-MM-yyyy HH:mm][d-M-yyyy H:m]");
        LocalDateTime f = null;
        try {
            f = LocalDateTime.parse(fecha, formatter);
            return f;
        } catch (Exception e) {
            throw new BussinesRuleException("400", "Bad request", "Error al introducir la fecha. El formato es: [dd-mm-yyyy HH:mm]", HttpStatus.BAD_REQUEST);
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
            newHuesped.setIdHabitacion(input.getIdHabitacion());

            /*Si la habitacion con la que lo quiero relacionar no es 0 (valor por defecto)
            * entonces asocialo con esa habitacion
            */
            if(newHuesped.getIdHabitacion()!=0){
                if(roomRepository.existsById(newHuesped.getIdHabitacion()))
                    throw new BussinesRuleException("404","Not Found", "Error validacion, la habitacion con id " + newHuesped.getIdHabitacion() + " no existe", HttpStatus.NOT_FOUND);
                newHuesped.setIdHabitacion(input.getIdHabitacion());
                newHuesped.setHabitacion(input.getHabitacion());
                addHostToRoom(input.getIdHabitacion(), id);
            }

            Huesped save = hostRepository.save(newHuesped);
            return new ResponseEntity<>(save, HttpStatus.OK);
        } else {
            throw new BussinesRuleException("404", "Not Found", "Error validacion, el huesped con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
    }



    public ResponseEntity<?> post(Huesped input) throws BussinesRuleException{
        
        /*Compruebo que un cliente no se cree con una fecha de salida anterior al momento actual
         * y que la fecha de salida no sea antes de la fecha de entrada
         */
        if (input.getFechaCheckout().isBefore(LocalDateTime.now())
                || input.getFechaCheckin().isAfter(input.getFechaCheckout())) {
            throw new BussinesRuleException("400", "Bad request", "Error en la peticion", HttpStatus.BAD_REQUEST);
        }

        
        /*Si no lo creo antes del if de abajo, el huesped aun no esta en el repositorio
         * y el metodo addHostToRoom no lo puede encontrar y salta excepcion
         */
        Huesped save = hostRepository.save(input);

        /*Si la habitacion con la que lo quiero relacionar no es 0 (valor por defecto)
         * entonces asocialo con esa habitacion
         */
        if(input.getIdHabitacion()!=0){
            addHostToRoom(input.getIdHabitacion(), input.getId());
        }
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
