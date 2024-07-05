package com.proyecto.hoteles.servicios;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.proyecto.hoteles.entidades.Habitacion;
import com.proyecto.hoteles.entidades.Hotel;
import com.proyecto.hoteles.entidades.Servicio;
import com.proyecto.hoteles.exception.BussinesRuleException;
import com.proyecto.hoteles.repositorios.HostRepository;
import com.proyecto.hoteles.repositorios.HotelRepository;
import com.proyecto.hoteles.repositorios.RoomRepository;
import com.proyecto.hoteles.repositorios.ServiceRepository;
import com.proyecto.hoteles.utils.ListsUtil;

import jakarta.transaction.Transactional;

@Service
public class ServicioHotel {
    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private HostRepository hostRepository;

    /* PATCH */
    public Hotel updateHotelByFields(long id, Map<String, Object> fields) {
        Optional<Hotel> optHotel = hotelRepository.findById(id);

        if (optHotel.isPresent()) {
            fields.forEach((key, value) -> {
                if (key.equals("services")) {

                    String lts = value.toString();
                    String cleanInput = lts.replaceAll("\\[|\\]", "");
                    System.out.println(cleanInput);
                    List<String> stringList = Arrays.asList(cleanInput.split(","));
                    stringList = stringList.stream()
                    .map(String::trim)
                    .collect(Collectors.toList());

                    List<Long> longList = new ArrayList<>();
                      for (String element : stringList) {
                      System.out.println(element);
                      longList.add(Long.parseLong(element));
                      }
                    

                }

                Field field = ReflectionUtils.findField(Hotel.class, key);
                field.setAccessible(true);
                ReflectionUtils.setField(field, optHotel.get(), value);

            });
            return hotelRepository.save(optHotel.get());
        } else {
            return null;
        }
    }

    /* GET-ALL */
    public ResponseEntity<?> getAll() throws BussinesRuleException {
        if (!hotelRepository.findAll().isEmpty()) {
            return new ResponseEntity<>(hotelRepository.findAll(), HttpStatus.OK);
        } else {
            throw new BussinesRuleException("204", "La BD de Hotel está vacía.", HttpStatus.NO_CONTENT);
            //return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    /* GET-BY-ID */
    public ResponseEntity<?> getById(long id) throws BussinesRuleException {
        Optional<Hotel> hotel = hotelRepository.findById(id);
        if (hotel.isPresent()) {
            return new ResponseEntity<>(hotel.get(), HttpStatus.OK);
        } else {
            throw new BussinesRuleException("404", "Not Found",
                    "Error validacion, el hotel con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
    }

    /* PUT */
    public ResponseEntity<?> put(long id, Hotel input) throws BussinesRuleException {
        Optional<Hotel> optionalhotel = hotelRepository.findById(id);
        if (optionalhotel.isPresent()) {
            Hotel newhotel = optionalhotel.get();
            if (!input.getTelefono().matches("^\\d+( \\d+)*$")) {// lo suyo seria poner que hasta 9 nums
                throw new BussinesRuleException("400", "Bad request",
                        "Error en el número de teléfono. No se admiten caracteres", HttpStatus.BAD_REQUEST);
            }
            newhotel.setNombre(input.getNombre());
            newhotel.setDireccion(input.getDireccion());
            newhotel.setEmail(input.getEmail());
            newhotel.setSitioWeb(input.getSitioWeb());
            newhotel.setTelefono(input.getTelefono());
            /* newhotel.setHabitaciones(input.getHabitaciones()); */
            newhotel.setServices(input.getServices());
            System.out.println(newhotel.getServices().size());
            // newhotel.setServicios(input.getServicios());
            // addServiceToHotel(id, input.getServices());
            actualizarServicios(id, input.getServices());

            Hotel save = hotelRepository.save(newhotel);
            return new ResponseEntity<>(save, HttpStatus.OK);
        } else {
            throw new BussinesRuleException("404", "Not Found",
                    "Error validacion, el hotel con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
    }

    private boolean esTelefonoValido(Hotel hotel){
        return hotel.getTelefono().matches("^\\d+( \\d+)*$");
    }
    /* POST */
    public ResponseEntity<?> post(Hotel input) throws BussinesRuleException {
        //if (!input.getTelefono().matches("^\\d+( \\d+)*$")) {// lo suyo seria poner que hasta 9 nums
        if(!esTelefonoValido(input)){
            throw new BussinesRuleException("400", "Bad request",
                    "Error en el número de teléfono. No se admiten caracteres", HttpStatus.BAD_REQUEST);
        }

        // En principio esto es para que se cree en cascada
        input.getHabitaciones().forEach(x -> x.setHotel(input));
        input.getHabitaciones().forEach(x -> x.getHuespedes().forEach(z -> z.setHabitacion(x)));

        // Para cada servicio, añadimos este hotel a su lista de hoteles
        input.getServicios().forEach(x -> {
            List<Hotel> hoteles = new ArrayList<>();
            hoteles = x.getHoteles();
            hoteles.add(input);
            x.setHoteles(hoteles);

        });

        //Guardamos ya para generar el ID y poder pasarlo a las entidades hijas
        Hotel save = hotelRepository.save(input);

        //Asociamos las habitaciones con el hotel a través de su ID
        input.getHabitaciones().forEach(x -> {
            x.setIdHotel(x.getHotel().getId());
            // Si no meto save y flush no actualiza el huesped en la BD
            roomRepository.saveAndFlush(x);

            //Asociamos los huespedes con las habitaciones a través de su ID
            x.getHuespedes().forEach(h -> {
                h.setIdHabitacion(h.getHabitacion().getId());
                // Si no meto save y flush no actualiza el huesped en la BD
                hostRepository.saveAndFlush(h);
            });
        });

        // Uso un hashSet para que no se repitan los servicios que se meten como objetos
        // y luego lo paso a List porque el parametro de la funcion es de ese tipo
        HashSet<Long> servicesAux = new HashSet<>();
        input.getServices().forEach(x -> {
            if (serviceRepository.existsById(x))
                servicesAux.add(x);
        });

        List<Long> services = new ArrayList<>(servicesAux);
        input.setServices(services);
        addServiceToHotel(save.getId(), services);
        input.getServicios().forEach(x -> servicesAux.add(x.getId()));
        services = new ArrayList<>(servicesAux);
        input.setServices(services);

        save = hotelRepository.saveAndFlush(input);

        return ResponseEntity.ok(save);

    }

    /* DELETE-BY-ID */
    public ResponseEntity<?> deleteById(long id) throws BussinesRuleException {
        Optional<Hotel> hotel = hotelRepository.findById(id);
        if (hotel.isPresent()) {
            hotelRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            throw new BussinesRuleException("404", "Not Found",
                    "Error validacion, el hotel con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
    }

    /* DELETE-ALL */
    public ResponseEntity<?> deleteAll() {
        hotelRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    private void addRoomToHotel(long idHotel, long idRoom) throws BussinesRuleException {
        Hotel hotel = hotelRepository.findById(idHotel).orElseThrow(() -> new BussinesRuleException("404", "Not Found",
                "Error validacion, el hotel con id " + idHotel + " no existe", HttpStatus.NOT_FOUND));
        Habitacion habitacion = roomRepository.findById(idRoom).orElseThrow(() -> new BussinesRuleException("404",
                "Not Found", "Error validacion, el hotel con id " + idRoom + " no existe", HttpStatus.NOT_FOUND));

        hotel.addRoom(habitacion);
        hotelRepository.save(hotel);
    }

    @Transactional
    private void addServiceToHotel(long idHotel, List<Long> idServices) throws BussinesRuleException {
        Hotel hotel = hotelRepository.findById(idHotel).orElseThrow(() -> new BussinesRuleException("404", "Not Found",
        "Error validacion, el hotel con id " + idHotel + " no existe", HttpStatus.NOT_FOUND));
        for (long id : idServices) {
            Servicio service = serviceRepository.findById(id)
                    .orElseThrow(() -> new BussinesRuleException("404", "Not Found",
                    "Error validacion, el servicio con id " + id + " no existe", HttpStatus.NOT_FOUND));
            hotel.addService(service);
        }
        hotelRepository.save(hotel);
    }

    private void actualizarServicios(long idHotel, List<Long> idServices) throws BussinesRuleException {
        Hotel hotel = hotelRepository.findById(idHotel).orElseThrow(() -> new BussinesRuleException("404", "Not Found",
                "Error validacion, el hotel con id " + idHotel + " no existe", HttpStatus.NOT_FOUND));

        // Esto hace una lista de los Id's descartando los que ya estaban para no
        // duplicar
        List<Long> nonMatchingIds = idServices.stream()
                .filter(id -> hotel.getServicios().stream().noneMatch(s -> s.getId() == id))
                .collect(Collectors.toList());

        // AÑADIR SERVICIOS
        for (long id : nonMatchingIds) {
            Servicio service = serviceRepository.findById(id).orElseThrow(() -> new BussinesRuleException("404",
                    "Not Found", "Error validacion, el servicio con id " + id + " no existe", HttpStatus.NOT_FOUND));
            hotel.addService(service);
        }

        // ELIMINAR SERVICIO
        // Esta lista obtiene los Id's de los servicios de un hotel
        List<Long> servicesToRemove = hotel.getServicios().stream().map(Servicio::getId).collect(Collectors.toList());
        for (long id : servicesToRemove) {
            Servicio service = serviceRepository.findById(id).orElseThrow(() -> new BussinesRuleException("404",
                    "Not Found", "Error validacion, el servicio con id " + id + " no existe", HttpStatus.NOT_FOUND));
            // Si un Id de la antigua lista NO está en la lista 'services' (input) ->
            // Eliminar
            if (!idServices.contains(id))
                hotel.removeService(service);
        }

    }

    public List<Hotel> filter(String nombre, String direccion, String telefono, String email, String web) {
        List<Hotel> hostsByName = new ArrayList<>();
        List<Hotel> hostsByAddress = new ArrayList<>();
        List<Hotel> hostsByPhone = new ArrayList<>();
        List<Hotel> hostsByMail = new ArrayList<>();
        List<Hotel> hostsByWebsite = new ArrayList<>();
        List<Boolean> vaciaPorNotFound = new ArrayList<>();
        boolean p = false, q = false, r = false, s = false;
        Set<Hotel> hostsFound = new HashSet<>();
        if (p = nombre != null) {
            hostsByName = findByName(nombre);
            hostsFound.addAll(hostsByName);
            vaciaPorNotFound.add(p);
        }

        if (q = direccion != null) {
            hostsByAddress = findByAddress(direccion);
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByAddress, vaciaPorNotFound);
            vaciaPorNotFound.add(q);
        }

        if (r = telefono != null) {
            hostsByPhone = findByPhone(telefono);
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByPhone, vaciaPorNotFound);
            vaciaPorNotFound.add(r);
        }

        if (s = email != null) {
            hostsByMail = findByMail(email);
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByMail, vaciaPorNotFound);
            vaciaPorNotFound.add(s);
        }

        if (web != null) {
            hostsByWebsite = findByWebsite(web);
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByWebsite, vaciaPorNotFound);
        }

        return new ArrayList<>(hostsFound);
    }

    private List<Hotel> findByName(String name) {
        return hotelRepository.findAll().stream()
                .filter(h -> h.getNombre().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    private List<Hotel> findByAddress(String dir) {
        return hotelRepository.findAll().stream()
                .filter(h -> h.getDireccion().equalsIgnoreCase(dir))
                .collect(Collectors.toList());
    }

    private List<Hotel> findByPhone(String telefono) {
        return hotelRepository.findAll().stream()
                .filter(h -> h.getTelefono().equalsIgnoreCase(telefono))
                .collect(Collectors.toList());
    }

    private List<Hotel> findByMail(String email) {
        return hotelRepository.findAll().stream()
                .filter(h -> h.getEmail().equalsIgnoreCase(email))
                .collect(Collectors.toList());
    }

    private List<Hotel> findByWebsite(String web) {
        return hotelRepository.findAll().stream()
                .filter(h -> h.getSitioWeb().equalsIgnoreCase(web))
                .collect(Collectors.toList());
    }

}
