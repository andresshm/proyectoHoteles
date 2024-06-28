package com.proyecto.hoteles.servicios;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;
import org.springframework.stereotype.Service;
import com.proyecto.hoteles.entidades.Huesped;
import com.proyecto.hoteles.repositorios.HostRepository;

@Service
public class ServicioHuesped {

    @Autowired
    private HostRepository hostRepository;

    public Huesped updateHostByFields(long id, Map<String, Object> fields) {
        Optional<Huesped> optHost = hostRepository.findById(id);

        if (optHost.isPresent()) {
            fields.forEach((key, value) -> {
                // Falta comprobar que la fecha de checkout no sean nunca inferior a la de
                // checkin
                if (key.equals("fechaCheckin")) {
                    String dateString = (String) value;
                    LocalDate fecha = null;
                    try {
                        fecha = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format: " + e.getMessage());
                    }
                    optHost.get().setFechaCheckin(fecha);

                } else if (key.equals("fechaCheckout")) {

                    String dateString = (String) value;
                    LocalDate fecha = null;
                    try {
                        fecha = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format: " + e.getMessage());
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
        .filter(h -> h.getFechaCheckin().isAfter(checkIn))
        .collect(Collectors.toList());
        /*for (Huesped h : hostRepository.findAll()) {
            if (h.getFechaCheckin().isAfter(checkIn)) {
                hosts.add(h);
            }
        }*/
    }

    public List<Huesped> findByCheckOut(LocalDate checkOut) {
        return hostRepository.findAll().stream()
        .filter(h -> h.getFechaCheckin().isAfter(checkOut))
        .collect(Collectors.toList());
        /*for (Huesped h : hostRepository.findAll()) {
            if (h.getFechaCheckin().isAfter(checkOut)) {
                hosts.add(h);
            }
        }*/
    }

}
