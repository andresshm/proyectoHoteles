package com.proyecto.hoteles.servicios;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.proyecto.hoteles.entidades.Filtro;
import com.proyecto.hoteles.entidades.Habitacion;
import com.proyecto.hoteles.entidades.Hotel;
import com.proyecto.hoteles.entidades.Huesped;
import com.proyecto.hoteles.exception.BussinesRuleException;
import com.proyecto.hoteles.repositorios.HostRepository;
import com.proyecto.hoteles.repositorios.HotelRepository;
import com.proyecto.hoteles.repositorios.RoomRepository;
import com.proyecto.hoteles.utils.ListsUtil;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

@Service
public class ServicioHabitacion {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HostRepository hostRepository;

    @Autowired
    private HotelRepository hotelRepository;

    public Habitacion updateRoomByFields(long id, Map<String, Object> fields) {
        Optional<Habitacion> optRoom = roomRepository.findById(id);

        if (optRoom.isPresent()) {
            fields.forEach((key, value) -> {
                Field field = ReflectionUtils.findField(Habitacion.class, key);
                field.setAccessible(true);
                ReflectionUtils.setField(field, optRoom.get(), value);
            });
            return roomRepository.save(optRoom.get());
        } else {
            return null;
        }
    }

    public ResponseEntity<?> getAll() {
        if (!roomRepository.findAll().isEmpty()) {
            return new ResponseEntity<>(roomRepository.findAll(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    public ResponseEntity<?> getById(long id) throws BussinesRuleException {
        Optional<Habitacion> room = roomRepository.findById(id);
        if (room.isPresent()) {
            return new ResponseEntity<>(room.get(), HttpStatus.OK);
        } else {
            throw new BussinesRuleException("404", "Not Found",
                    "Error validacion, la habitacion con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
    }

    private void addRoomToHotel(long idHotel, long idRoom) {
        Hotel hotel = hotelRepository.findById(idHotel).orElseThrow(() -> new RuntimeException("Hotel not found"));
        Habitacion habitacion = roomRepository.findById(idRoom)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        hotel.addRoom(habitacion);
        hotelRepository.save(hotel);
    }

    public ResponseEntity<?> put(long id, Habitacion input) throws BussinesRuleException {
        // Busca en el repo por id
        Optional<Habitacion> optionalRoom = roomRepository.findById(id);
        // Si está...
        if (optionalRoom.isPresent()) {
            Habitacion newRoom = optionalRoom.get();
            // Actualizamos los atributos
            if (!validRoom(input.getNumero(), input.getPrecioNoche())) {// si es valida...
                newRoom.setNumero(input.getNumero());
                newRoom.setPrecioNoche(input.getPrecioNoche());
                newRoom.setTipo(input.getTipo());

                /* Asociar la habitación con un hotel */
                if (input.getIdHotel() != 0) {
                    // Si ya pertenecia a un hotel -> Bad Request
                    if (newRoom.getIdHotel() != 0 && newRoom.getIdHotel() != input.getIdHotel())
                        throw new BussinesRuleException("400", "Bad request",
                                "Esa habitación ya está vinculada a un hotel", HttpStatus.BAD_REQUEST);
                    // Si el id del hotel no existe -> Not FOund
                    if (!hotelRepository.existsById(input.getIdHotel()))
                        throw new BussinesRuleException("404", "Not Found",
                                "Error validacion, el hotel con id " + input.getIdHotel() + " no existe",
                                HttpStatus.NOT_FOUND);

                    // Asociar
                    newRoom.setIdHotel(input.getIdHotel());
                    newRoom.setHotel(input.getHotel());
                    addRoomToHotel(input.getIdHotel(), id);
                }
            } else {
                // Letras donde no deberia -> Bad Request
                throw new BussinesRuleException("400", "Bad request",
                        "Precio o número de habitación no validos, estos parámetros no admiten caracteres",
                        HttpStatus.BAD_REQUEST);
            }
            // Guardar hotel actualizado en la BD
            Habitacion save = roomRepository.save(newRoom);
            return new ResponseEntity<>(save, HttpStatus.OK);
        } else {
            // Id de la habitacion inexistente -> Not Found
            throw new BussinesRuleException("404", "Not Found",
                    "Error validacion, la habitacion con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> post(Habitacion input) throws BussinesRuleException {
        if (validRoom(input.getNumero(), input.getPrecioNoche())) {// si no es valida
            throw new BussinesRuleException("400", "Bad request", "Precio no valido, el precio no admite caracteres",
                    HttpStatus.BAD_REQUEST);
        }

        // En principio esto es para que se cree en cascada
        input.getHuespedes().forEach(x -> {
            x.setHabitacion(input);
        });
        /*
         * Si no lo creo antes del if de abajo, el huesped aun no esta en el repositorio
         * y el metodo addHostToRoom no lo puede encontrar y salta excepcion
         */
        Habitacion save = roomRepository.save(input);

        /*
         * Si la habitacion con la que lo quiero relacionar no es 0 (valor por defecto)
         * entonces asocialo con esa habitacion
         */
        if (input.getIdHotel() != 0) {
            addRoomToHotel(input.getIdHotel(), input.getId());
        }

        /* Esto es para que los huespedes guarden el ID */
        input.getHuespedes().forEach(x -> {
            x.setIdHabitacion(x.getHabitacion().getId());
            // Si no meto save y flush no actualiza el huesped en la BD
            hostRepository.saveAndFlush(x);
        });

        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }

    public ResponseEntity<?> deleteById(long id) throws BussinesRuleException {
        Optional<Habitacion> room = roomRepository.findById(id);
        if (room.isPresent()) {
            roomRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            throw new BussinesRuleException("404", "Not Found",
                    "Error validacion, la habitacion con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> deleteAll() {
        roomRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    private void addHostToRoom(long idRoom, long idHost) {
        Habitacion habitacion = roomRepository.findById(idRoom)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        Huesped host = hostRepository.findById(idHost).orElseThrow(() -> new RuntimeException("Host not found"));

        habitacion.addHost(host);
        roomRepository.save(habitacion);
    }

    private boolean isNumeric(String n) {
        return n.matches("\\d+");
    }

    private boolean isGreaterThanCero(float n) {
        return n > 0;
    }

    private boolean validRoom(String s, float n) {
        return !(isNumeric(s) && isGreaterThanCero(n));

    }

    public List<Habitacion> filter(String numero, String tipo, Float precio) {
        List<Habitacion> hostsByNumber = new ArrayList<>();
        List<Habitacion> hostsByType = new ArrayList<>();

        // float no puede ser null porque es un tipo primitivo, pero Float si
        // si es nulo, asignamos 0$ a precio para que no encuentre ninguna habitacion
        // y no entorpezca la busqueda
        Float floatWrapper = precio;
        if (floatWrapper == null) {
            precio = 0.0f;
        } else {
            floatWrapper = precio;
        }
        List<Habitacion> hostsByPrice = findByPrice(precio);

        List<Boolean> vaciaPorNotFound = new ArrayList<>();
        boolean p = false, q = false;
        Set<Habitacion> hostsFound = new HashSet<>();
        if (p = numero != null) {
            hostsByNumber = findByNumber(numero);
            hostsFound.addAll(hostsByNumber);
            vaciaPorNotFound.add(p);
        }

        if (q = tipo != null) {
            hostsByType = findByType(tipo);
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByType, vaciaPorNotFound);
            vaciaPorNotFound.add(q);
        }

        if (floatWrapper != null) {
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByPrice, vaciaPorNotFound);
        }

        return new ArrayList<>(hostsFound);
    }

    private List<Habitacion> findByNumber(String number) {
        return roomRepository.findAll().stream()
                .filter(h -> h.getNumero().equals(number))
                .collect(Collectors.toList());

    }

    private List<Habitacion> findByType(String type) {
        return roomRepository.findAll().stream()
                .filter(h -> h.getTipo().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    private List<Habitacion> findByPrice(float price) {
        return roomRepository.findAll().stream()
                .filter(h -> h.getPrecioNoche() == price)
                .collect(Collectors.toList());
    }




     public static Specification<Habitacion> getSongsByFilters(List<Filtro.SearchCriteria> searchCriteriaList) {
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

    public static Specification<Habitacion> hasNumber(String numero) {
        return (root, query, criteriaBuilder) -> 
        numero == null ? null : criteriaBuilder.like(root.get("numero"), "%" + numero + "%");
    }

    public static Specification<Habitacion> hasType(String tipo) {
        return (root, query, criteriaBuilder) -> 
            tipo == null ? null : criteriaBuilder.equal(root.get("tipo"), tipo);
    }

    public static Specification<Habitacion> hasDni(Float precio) {
        return (root, query, criteriaBuilder) -> 
            precio == null ? null : criteriaBuilder.equal(root.get("precioNoche"), precio);
    }

}
