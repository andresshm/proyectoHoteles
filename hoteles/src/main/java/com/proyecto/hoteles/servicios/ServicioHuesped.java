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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.stereotype.Service;

import com.proyecto.hoteles.entidades.Filtro;
import com.proyecto.hoteles.entidades.Habitacion;
import com.proyecto.hoteles.entidades.Huesped;
import com.proyecto.hoteles.exception.BussinesRuleException;
import com.proyecto.hoteles.repositorios.HostRepository;
import com.proyecto.hoteles.repositorios.RoomRepository;
import com.proyecto.hoteles.utils.ListsUtil;

import jakarta.persistence.criteria.Predicate;

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
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("d/M/yyyy H:m");
                    LocalDateTime fecha = null;
                    try {
                        fecha = LocalDateTime.parse(dateString, formatter);
                    } catch (DateTimeParseException e) {
                        fecha = LocalDateTime.parse(dateString, formatter2);
                    }
                    optHost.get().setFechaCheckin(fecha);

                } else if (key.equals("fechaCheckout")) {
                    String dateString = (String) value;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("d/M/yyyy H:m");
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
                    if(Long.parseLong(value.toString())!=0){//value es el id de la habitacion
                        if(roomRepository.existsById(Long.parseLong(value.toString()))){
                            optHost.get().setIdHabitacion(Long.parseLong(value.toString()));
                            addHostToRoom(optHost.get().getIdHabitacion(), optHost.get().getId());
                        }else{
                            System.out.println("no existe");
                        }
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

    public List<Huesped> findByOrigin(String procedencia) {
        return hostRepository.findAll().stream()
                .filter(h -> h.getProcedencia().equalsIgnoreCase(procedencia))
                .collect(Collectors.toList());
    }

    public List<Huesped> findByCheckIn(LocalDateTime checkInD, LocalDateTime checkInH) {
        
        return hostRepository.findAll().stream()
        .filter(h -> h.getFechaCheckin().isAfter(checkInD) && h.getFechaCheckin().isBefore(checkInH))
        .collect(Collectors.toList());
    }

    public List<Huesped> findByCheckOut(LocalDateTime checkOutD, LocalDateTime checkOutH) {
        return hostRepository.findAll().stream()
        .filter(h -> h.getFechaCheckout().isAfter(checkOutD) && h.getFechaCheckout().isBefore(checkOutH))
        .collect(Collectors.toList());
    }


//llevar al final este y los de arriba
    public List<Huesped> filter(String nombre, String apellido, String documento, String procedencia, String checkInD, String checkInH, String checkOutD, String checkOutH) throws BussinesRuleException{
        List<Huesped> hostsByName = new ArrayList<>();
        List<Huesped> hostsBySurname = new ArrayList<>();
        List<Huesped> hostsByDocument = new ArrayList<>();
        List<Huesped> hostsByOrigin = new ArrayList<>();
        List<Huesped> hostsByCheckin = new ArrayList<>();
        List<Huesped> hostsByCheckout = new ArrayList<>();
        Set<Huesped> hostsFound = new HashSet<>();
        List<Boolean> vaciaPorNotFound = new ArrayList<>();
        boolean p = false, q = false, r = false, s = false, t = false;

        LocalDateTime checkInDateD = null;
        LocalDateTime checkInDateH = null;
        LocalDateTime checkOutDateD = null;
        LocalDateTime checkOutDateH = null;



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
        
        if (t = procedencia != null) {
            hostsByOrigin = findByOrigin(procedencia);
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByOrigin, vaciaPorNotFound);
            vaciaPorNotFound.add(t);
        }

        if (s = (checkInD != null && checkInH != null)) {
            checkInDateD = stringToDate(checkInD);
            checkInDateH = stringToDate(checkInH);
            hostsByCheckin = findByCheckIn(checkInDateD, checkInDateH);
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByCheckin, vaciaPorNotFound);
            vaciaPorNotFound.add(s);
        }

        if (checkOutD != null && checkOutH != null) {
            checkOutDateD = stringToDate(checkOutD);
            checkOutDateH = stringToDate(checkOutH);
            hostsByCheckout = findByCheckOut(checkOutDateD, checkOutDateH);
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByCheckout, vaciaPorNotFound);
        }

        return new ArrayList<>(hostsFound);
    }


    private LocalDateTime stringToDate(String fecha) throws BussinesRuleException{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[dd/MM/yyyy HH:mm][d/M/yyyy H:m]");
        LocalDateTime f = null;
        try {
            f = LocalDateTime.parse(fecha, formatter);
            return f;
        } catch (Exception e) {
            throw new BussinesRuleException("400", "Bad request", "Error al introducir la fecha. El formato es: [dd/mm/yyyy HH:mm]", HttpStatus.BAD_REQUEST);
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
            newHuesped.setProcedencia(input.getProcedencia());
            newHuesped.setFechaCheckin(input.getFechaCheckin());
            newHuesped.setFechaCheckout(input.getFechaCheckout());
            newHuesped.setIdHabitacion(input.getIdHabitacion());

            /*Si la habitacion con la que lo quiero relacionar no es 0 (valor por defecto)
            * entonces asocialo con esa habitacion
            */
            if(newHuesped.getIdHabitacion()!=0){
                if(!roomRepository.existsById(newHuesped.getIdHabitacion()))
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





    public static Specification<Huesped> getSongsByFilters(List<Filtro.SearchCriteria> searchCriteriaList) {
        return (root, query, criteriaBuilder) -> {
            
            Predicate[] predicates = searchCriteriaList.stream()
                    .map(searchCriteria -> {
                        switch (searchCriteria.getOperation()) {
                            case EQUALS -> {
                            return criteriaBuilder.equal(root.get(searchCriteria.getKey()), searchCriteria.getValue());
                    }
                            case CONTAINS -> {
                                return criteriaBuilder.like(root.get(searchCriteria.getKey()), "%" + searchCriteria.getValue() + "%");
                    }
                            case GREATER_THAN -> {
                                return criteriaBuilder.greaterThan(root.get(searchCriteria.getKey()), searchCriteria.getValue());
                    }
                            case LESS_THAN -> {
                                return criteriaBuilder.lessThan(root.get(searchCriteria.getKey()), searchCriteria.getValue());
                    }
                            default -> throw new UnsupportedOperationException("Operation not supported");
                        }
                    })
                    .toArray(Predicate[]::new);
            return criteriaBuilder.and(predicates);
        };
    }

    public static Specification<Huesped> hasName(String nombre) {
        return (root, query, criteriaBuilder) -> 
        nombre == null ? null : criteriaBuilder.like(root.get("nombre"), "%" + nombre + "%");
    }

    public static Specification<Huesped> hasSurname(String apellido) {
        return (root, query, criteriaBuilder) -> 
            apellido == null ? null : criteriaBuilder.equal(root.get("apellido"), apellido);
    }

    public static Specification<Huesped> hasDni(String dni) {
        return (root, query, criteriaBuilder) -> 
            dni == null ? null : criteriaBuilder.equal(root.get("dniPasaporte"), dni);
    }

    public static Specification<Huesped> hasProcedence(String procedencia) {
        return (root, query, criteriaBuilder) -> 
        procedencia == null ? null : criteriaBuilder.equal(root.get("procedencia"), procedencia);
    }
    
    public static Specification<Huesped> hasCheckin(String fechaCheckin) {
        return (root, query, criteriaBuilder) -> 
        fechaCheckin == null ? null : criteriaBuilder.equal(root.get("fechaCheckin"), fechaCheckin);
    }
    
    public static Specification<Huesped> hasCheckout(String fechaCheckout) {
        return (root, query, criteriaBuilder) -> 
        fechaCheckout == null ? null : criteriaBuilder.equal(root.get("fechaCheckout"), fechaCheckout);
    }


}
