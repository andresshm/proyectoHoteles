package com.proyecto.hoteles.controlador;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.hoteles.entidades.Filtro;
import com.proyecto.hoteles.entidades.Servicio;
import com.proyecto.hoteles.exception.BussinesRuleException;
import com.proyecto.hoteles.repositorios.ServiceRepository;
import com.proyecto.hoteles.servicios.ServicioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Service API", description = "Esta API sirve para gestionar los servicios")
@RestController
@CrossOrigin(origins = "http://localhost:4200"/*"http://localhost:4200" */)
@RequestMapping("/servicio")
public class ServiceRestController {
    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    ServicioService servicio;

    /**
     * GET-ALL
     * 
     * @return
     */
    @Operation(summary = "Devuelve una lista con todos los servicios")
    @GetMapping()
    public ResponseEntity<?> findAll() {
        return servicio.getAll();

    }

    /**
     * GET-BY-ID
     * 
     * @param id
     * @return
     * @throws BussinesRuleException
     */
    @Operation(summary = "Devuelve el servicio con el id seleccionado")
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) throws BussinesRuleException {
        return servicio.getById(id);
    }

    /**
     * PUT
     * 
     * @param id
     * @param input
     * @return
     * @throws BussinesRuleException
     */
    @Operation(summary = "Permite actualizar un elemento completo")
    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable Long id, @RequestBody Servicio input) throws BussinesRuleException {
        return servicio.put(id, input);
    }

    /**
     * PATCH
     * 
     * @param id
     * @param fields
     * @return
     */
    @Operation(summary = "Permite actualizar un campo concreto")
    @PatchMapping("/{id}")
    public Servicio patch(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
        return servicio.updateServiceByFields(id, fields);
    }

    /**
     * POST
     * 
     * @param input
     * @return
     */
    @Operation(summary = "Registra un servicio en la base de datos")
    @PostMapping()
    public ResponseEntity<?> post(@RequestBody Servicio input) {
        return servicio.post(input);
    }

    /**
     * DELETE-BY-ID
     * 
     * @param id
     * @return
     * @throws BussinesRuleException
     */
    @Operation(summary = "Elimina el servicio con el id seleccionado")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws BussinesRuleException {
        return servicio.deleteById(id);

    }

    /**
     * DELETE-ALL
     * 
     * @return
     */
    @Operation(summary = "Elimina todos los servicios de la base de datos")
    @DeleteMapping("/full")
    public ResponseEntity<?> deleteAll() {
        return servicio.deleteAll();
    }

    /**
     * FILTER (unused)
     * 
     * @param nombre
     * @param descripcion
     * @return
     */
    // @Operation(summary = "Permite buscar un servicio filtrando por sus campos")
    // @GetMapping("/filter")
    // public List<Servicio> getByParams(
    //         @RequestParam(required = false) String nombre,
    //         @RequestParam(required = false) String descripcion) {
    //     return servicio.filter(nombre, descripcion);
    // }





     @PostMapping("/filterv2")
    public ResponseEntity<?> filterBy(@RequestBody Filtro struct) {

        // Construir el objeto Sort a partir de los criterios de ordenación
        Sort sort = Sort.unsorted();
        for (Filtro.SortCriteria sortCriteria : struct.getCriteriosOrden()) {
            Sort.Direction direction = sortCriteria.getSentidoOrden() == Filtro.SortValue.ASC ? 
                                       Sort.Direction.ASC : Sort.Direction.DESC;
            sort = sort.and(Sort.by(direction, sortCriteria.getSortBy()));
        }

        // Construir el objeto Pageable a partir de la información de paginación y ordenación
        Pageable pageable = PageRequest.of(struct.getPage().getPageIndex(), struct.getPage().getPageSize(), sort);

        // Construir la especificación a partir de los criterios de búsqueda
        Specification<Servicio> specification = ServicioService.getSongsByFilters(struct.getCriteriosBusqueda());// SongSpecification.getSongsByFilters(struct.getListSearchCriteria());

        // Realizar la consulta con el repositorio utilizando Pageable y Specification
        Page<Servicio> servicios = serviceRepository.findAll(specification, pageable);

        // Convertir a DTO
        List<Servicio> servicesList = servicios.stream().collect(Collectors.toList());

        return ResponseEntity.ok(servicesList);
    }





}
