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
//import com.proyecto.hoteles.entidades.Hotel;
import com.proyecto.hoteles.entidades.Servicio;
import com.proyecto.hoteles.exception.BussinesRuleException;
//import com.proyecto.hoteles.repositorios.HotelRepository;
import com.proyecto.hoteles.repositorios.ServiceRepository;
import com.proyecto.hoteles.utils.ListsUtil;

import jakarta.persistence.criteria.Predicate;

//import jakarta.transaction.Transactional;

@Service
public class ServicioService {

    @Autowired
    private ServiceRepository serviceRepository;

    /*@Autowired
    private HotelRepository hotelRepository;*/

    public Servicio updateServiceByFields(long id, Map<String, Object> fields){
		Optional<Servicio> optService = serviceRepository.findById(id);

		if(optService.isPresent()){
			fields.forEach((key, value) -> {
				Field field = ReflectionUtils.findField(Servicio.class, key);
				field.setAccessible(true);
				ReflectionUtils.setField(field, optService.get(), value);
			});
			return serviceRepository.save(optService.get());
		}else{
			return null;
		}
 	}


    public List<Servicio> findByName(String name){
        return serviceRepository.findAll().stream()
                                    .filter(h -> h.getNombre().equalsIgnoreCase(name))
                                    .collect(Collectors.toList());
    }


    public List<Servicio> findByDescription(String description){
        return serviceRepository.findAll().stream()
                                    .filter(s -> s.getDescripcion().toLowerCase().contains(description.toLowerCase()))
                                    .collect(Collectors.toList());
    }


    public List<Servicio> filter(String nombre, String descripcion){
        List<Servicio> hostsByName = new ArrayList<>();
        List<Boolean> vaciaPorNotFound = new ArrayList<>();
        boolean p = false;

        Set<Servicio> hostsFound = new HashSet<>();
        if (p = nombre != null) {
            hostsByName = findByName(nombre);
            hostsFound.addAll(hostsByName);
            vaciaPorNotFound.add(p);
        }

        if (descripcion != null) {
            List<Servicio> hostsByDescription = findByDescription(descripcion);
            ListsUtil.interseccionSinListaVacia(hostsFound, hostsByDescription, vaciaPorNotFound);
        }

        return new ArrayList<>(hostsFound);
    }


    
 public ResponseEntity<?> getAll(){
    if (!serviceRepository.findAll().isEmpty()) {
        return new ResponseEntity<>(serviceRepository.findAll(), HttpStatus.OK);
    } else {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    }



    public ResponseEntity<?> getById(long id) throws BussinesRuleException{
        Optional<Servicio> service = serviceRepository.findById(id);
        if (service.isPresent()) {
            return new ResponseEntity<>(service.get(), HttpStatus.OK);
        } else {
            throw new BussinesRuleException("404","Bad request", "Error validacion, el servicio con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
    }



    public ResponseEntity<?> put(long id, Servicio input) throws BussinesRuleException{
        Optional<Servicio> optionalService = serviceRepository.findById(id);
        if (optionalService.isPresent()) {
            Servicio newService = optionalService.get();
            newService.setNombre(input.getNombre());
            newService.setDescripcion(input.getDescripcion());
            Servicio save = serviceRepository.save(newService);
            return new ResponseEntity<>(save, HttpStatus.OK);
        } else {
            throw new BussinesRuleException("404", "Not Found", "Error validacion, el servicio con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
    }



    public ResponseEntity<?> post(Servicio input){
        Servicio save = serviceRepository.save(input);
        return ResponseEntity.ok(save);
    }


    public ResponseEntity<?> deleteById (long id) throws BussinesRuleException{
        Optional<Servicio> service = serviceRepository.findById(id);
        if (service.isPresent()) {
            serviceRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            throw new BussinesRuleException("404","Not Found", "Error validacion, el servicio con id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
    }


    public ResponseEntity<?> deleteAll(){
        serviceRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }


    public static Specification<Servicio> getSongsByFilters(List<Filtro.SearchCriteria> searchCriteriaList) {
        return (root, query, criteriaBuilder) -> {
            
            Predicate[] predicates = searchCriteriaList.stream()
                    .map(searchCriteria -> {
                        switch (searchCriteria.getOperation()) {
                            case EQUALS -> {
                            // if ("nombre".equals(searchCriteria.getKey())) {
                            //     Join<Servicio, Album> albumJoin = root.join("album", JoinType.INNER);
                            //     return criteriaBuilder.equal(albumJoin.get("id"), Long.valueOf(searchCriteria.getValue()));
                            // } else {
                            //     return criteriaBuilder.equal(root.get(searchCriteria.getKey()), searchCriteria.getValue());
                            // }
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

    public static Specification<Servicio> hasName(String nombre) {
        return (root, query, criteriaBuilder) -> 
        nombre == null ? null : criteriaBuilder.like(root.get("nombre"), "%" + nombre + "%");
    }

    public static Specification<Servicio> hasDesc(String desc) {
        return (root, query, criteriaBuilder) -> 
            desc == null ? null : criteriaBuilder.equal(root.get("desc"), desc);
    }

    // public static Specification<Servicio> hasUrl(String url) {
    //     return (root, query, criteriaBuilder) -> 
    //         url == null ? null : criteriaBuilder.equal(root.get("url"), url);
    // }

    // public static Specification<Servicio> hasAlbum(Integer albumId) {
    //     return (root, query, criteriaBuilder) -> 
    //         albumId == null ? null : criteriaBuilder.equal(root.get("album").get("id"), albumId);
    // }

}
