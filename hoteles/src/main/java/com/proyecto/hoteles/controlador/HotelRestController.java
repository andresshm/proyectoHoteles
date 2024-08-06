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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.hoteles.entidades.Filtro;
import com.proyecto.hoteles.entidades.Hotel;
import com.proyecto.hoteles.entidades.HuespedesPorHotel;
import com.proyecto.hoteles.exception.BussinesRuleException;
import com.proyecto.hoteles.repositorios.HotelRepository;
import com.proyecto.hoteles.servicios.ServicioHotel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Hotel API", description = "Esta API sirve para gestionar los hoteles")
@RestController
@CrossOrigin(origins = "*"/*"http://localhost:4200" */)
@RequestMapping("/hotel")
public class HotelRestController {
    @Autowired
    HotelRepository hotelRepository;

    @Autowired
    ServicioHotel servicio;

    /**
     * GET-ALL
     * 
     * @return ResponseEntity<?>
     * @throws BussinesRuleException
     */
    @Operation(summary = "Devuelve una lista con todos los hoteles")
    @GetMapping()
    public ResponseEntity<?> findAll() throws BussinesRuleException {
        return servicio.getAll();
    }

    /**
     * GET-BY-ID
     * 
     * @param id
     * @return ResponseEntity<?>
     * @throws BussinesRuleException
     */
    @Operation(summary = "Devuelve el hotel con el id seleccionado")
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) throws BussinesRuleException {
        return servicio.getById(id);
    }

    /**
     * PUT
     * 
     * @param id
     * @param input
     * @return ResponseEntity<?>
     * @throws BussinesRuleException
     */
    @Operation(summary = "Permite actualizar un elemento completo")
    @PutMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, check phone is numeric")
    })
    public ResponseEntity<?> put(@PathVariable Long id, @RequestBody Hotel input) throws BussinesRuleException {
        return servicio.put(id, input);
    }

    /**
     * PATCH
     * 
     * @param id
     * @param fields
     * @return Hotel/null
     */
    @Operation(summary = "Permite actualizar un campo concreto")
    @PatchMapping("/{id}")
    public Hotel patch(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
        return servicio.updateHotelByFields(id, fields);
    }

    /**
     * POST
     * 
     * @param input
     * @return ResponseEntity<?>
     * @throws BussinesRuleException
     */
    @Operation(summary = "Registra un hotel en la base de datos")
    @PostMapping()
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Hotel added successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, check phone is numeric")
    })
    public ResponseEntity<?> post(@RequestBody Hotel input) throws BussinesRuleException {
        return servicio.post(input);
    }

    /**
     * DELETE-BY-ID
     * 
     * @param id
     * @return ResponseEntity<?>
     * @throws BussinesRuleException
     */
    @Operation(summary = "Elimina el hotel con el id seleccionado")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws BussinesRuleException {
        return servicio.deleteById(id);
    }

    /**
     * DELETE-ALL
     * 
     * @return ResponseEntity<?>
     */
    @Operation(summary = "Elimina todos los hoteles de la base de datos")
    @DeleteMapping("/full")
    public ResponseEntity<?> deleteAll() {
        return servicio.deleteAll();
    }


    @GetMapping("/total")
    public List<HuespedesPorHotel> getNumHosts() throws BussinesRuleException {
        return servicio.getNumHosts();
    }
    

    /**
     * FILTER
     * 
     * @param nombre
     * @param direccion
     * @param telefono
     * @param email
     * @param web
     * @return Lista de hoteles que cumplen las condiciones
     */
    @Operation(summary = "Permite buscar un hotel filtrando por sus campos")
    @GetMapping("/filter")
    public List<Hotel> getByParams(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String web) {
        return servicio.filter(nombre, direccion, telefono, email, web);
    }



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
        Specification<Hotel> specification = ServicioHotel.getSongsByFilters(struct.getCriteriosBusqueda());// SongSpecification.getSongsByFilters(struct.getListSearchCriteria());

        // Realizar la consulta con el repositorio utilizando Pageable y Specification
        Page<Hotel> hoteles = hotelRepository.findAll(specification, pageable);

        // Convertir a DTO
        List<Hotel> hotelsList = hoteles.stream().collect(Collectors.toList());

        return ResponseEntity.ok(hotelsList);
    }

}
