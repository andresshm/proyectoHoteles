package com.proyecto.hoteles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.proyecto.hoteles.entidades.Habitacion;
import com.proyecto.hoteles.entidades.Hotel;
import com.proyecto.hoteles.entidades.Huesped;
import com.proyecto.hoteles.entidades.Servicio;
import com.proyecto.hoteles.exception.BussinesRuleException;
import com.proyecto.hoteles.repositorios.HostRepository;
import com.proyecto.hoteles.repositorios.HotelRepository;
import com.proyecto.hoteles.repositorios.RoomRepository;
import com.proyecto.hoteles.repositorios.ServiceRepository;
import com.proyecto.hoteles.servicios.ServicioHabitacion;
import com.proyecto.hoteles.servicios.ServicioHotel;
import com.proyecto.hoteles.servicios.ServicioHuesped;
import com.proyecto.hoteles.servicios.ServicioService;

@SpringBootTest
class HotelesApplicationTests {

/*HUESPED */
	@Autowired
	private ServicioHuesped servHost;
	@MockBean
	private HostRepository hostRepository;

/*SERVICIO */
	@Autowired
	private ServicioService servicioService;
	@MockBean
	private ServiceRepository serviceRepository;

/*HABITACION */
	@Autowired
	private ServicioHabitacion servicioHabitacion;
	@MockBean
	private RoomRepository roomRepository;

/*HOTEL */
	@Autowired
	private ServicioHotel servicioHotel;
	@MockBean
	private HotelRepository hotelRepository;

	/*GET ALL */
	@Test
	public void getAllTest(){
		/* SERVICIO */
		List<Servicio> entities = new ArrayList<>();
        entities.add(new Servicio("catering", "comida"));
        entities.add(new Servicio("piscina", "agua"));

        when(serviceRepository.findAll()).thenReturn(entities);
        // Llamada al método getAll()
        ResponseEntity<?> response = servicioService.getAll();
        // Verificación del resultado
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(entities, response.getBody());




		/* HUESPED */
		List<Huesped> entities2 = new ArrayList<>();
        entities2.add(new Huesped("Carlos", "sanchez", "123"));
        entities2.add(new Huesped("cristina", "perez", "432"));
		
        when(hostRepository.findAll()).thenReturn(entities2);
        // Llamada al método getAll()
        ResponseEntity<?> response2 = servHost.getAll();
        // Verificación del resultado
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(entities2, response2.getBody());




		/* HABITACION */
		List<Habitacion> entities3 = new ArrayList<>();
        entities3.add(new Habitacion("23", "simple", 20));
        entities3.add(new Habitacion("223", "doble", 20.45f));
		
        when(roomRepository.findAll()).thenReturn(entities3);
        // Llamada al método getAll()
        ResponseEntity<?> response3 = servicioHabitacion.getAll();
        // Verificación del resultado
        assertEquals(HttpStatus.OK, response3.getStatusCode());
        assertEquals(entities3, response3.getBody());




		/* HOTEL */
		List<Hotel> entities4 = new ArrayList<>();
        entities4.add(new Hotel("hyltor", "carril", "678678678", "hyltor@gmail.es", "hyltor.org"));
        entities4.add(new Hotel("la parra", "carril", "678212678", "laparra@gmail.es", "laparra.org"));
		
        when(hotelRepository.findAll()).thenReturn(entities4);
        // Llamada al método getAll()
        ResponseEntity<?> response4 = servicioHotel.getAll();
        // Verificación del resultado
        assertEquals(HttpStatus.OK, response4.getStatusCode());
        assertEquals(entities4, response4.getBody());
	}


/*GET BY ID */
	@Test
    void testGetById_ExistingId() throws BussinesRuleException {
/*SERVICIO */
        Long id = 1L;
        Servicio entity = new Servicio(id);
        when(serviceRepository.findById(id)).thenReturn(java.util.Optional.of(entity));
        // Llamada al método getById()
        ResponseEntity<?> response = servicioService.getById(id);
        // Verificación del resultado
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(entity, response.getBody());




/*HUESPED */

        Huesped entity2 = new Huesped(id);
        when(hostRepository.findById(id)).thenReturn(java.util.Optional.of(entity2));
        // Llamada al método getById()
        ResponseEntity<?> response2 = servHost.getById(id);
        // Verificación del resultado
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertNotNull(response2.getBody());
        assertEquals(entity2, response2.getBody());




/*HABITACION */

        Habitacion entity3 = new Habitacion(id);
        when(roomRepository.findById(id)).thenReturn(java.util.Optional.of(entity3));
        // Llamada al método getById()
        ResponseEntity<?> response3 = servicioHabitacion.getById(id);
        // Verificación del resultado
        assertEquals(HttpStatus.OK, response3.getStatusCode());
        assertNotNull(response3.getBody());
        assertEquals(entity3, response3.getBody());




/*HOTEL */

        Hotel entity4 = new Hotel(id);
        when(hotelRepository.findById(id)).thenReturn(java.util.Optional.of(entity4));
        // Llamada al método getById()
        ResponseEntity<?> response4 = servicioHotel.getById(id);
        // Verificación del resultado
        assertEquals(HttpStatus.OK, response4.getStatusCode());
        assertNotNull(response4.getBody());
        assertEquals(entity4, response4.getBody());
    }


/* GET BY ID - NOT FOUND*/
	@Test
    void testGetById_NonExistingId() throws BussinesRuleException {
        Long id = 2L;

       
/*SERVICIO */
	when(serviceRepository.findById(id)).thenReturn(java.util.Optional.empty());
	// Verificación del resultado: Devuelve una excepcion de not found
	assertThrows(BussinesRuleException.class, () -> servicioService.getById(id));




/*HUESPED */
	when(hostRepository.findById(id)).thenReturn(java.util.Optional.empty());
	// Verificación del resultado: Devuelve una excepcion de not found
	assertThrows(BussinesRuleException.class, () -> servHost.getById(id));




/*HABITACION */
	when(roomRepository.findById(id)).thenReturn(java.util.Optional.empty());
	// Verificación del resultado: Devuelve una excepcion de not found
	assertThrows(BussinesRuleException.class, () -> servicioHabitacion.getById(id));




/*HOTEL */
	when(hotelRepository.findById(id)).thenReturn(java.util.Optional.empty());
	// Verificación del resultado: Devuelve una excepcion de not found
	assertThrows(BussinesRuleException.class, () -> servicioHotel.getById(id));
    }




	/*@Test
	public void putTest() throws BussinesRuleException{
		Servicio obj = new Servicio(1L, "name1", "description1");
        when(serviceRepository.save(obj)).thenReturn(obj);
        // Llamar al método put del servicio
		ResponseEntity<?> response = servicioService.put(1L, obj);
        // Verificar que la respuesta sea OK (200)
 		assertEquals(HttpStatus.OK, response.getStatusCode());
	}*/


	@Test

    void deleteByIdTest() throws BussinesRuleException {
		Long id = 1L;


/*SERVICIO */
Servicio entity = new Servicio(id);
		when(serviceRepository.findById(id)).thenReturn(java.util.Optional.of(entity));
        // Llamar al método deleteById del servicio
        ResponseEntity<?> response = servicioService.deleteById(entity.getId());
        // Verificar que el método devuelva true
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Verificar que el repositorio haya sido llamado con el id correcto
        verify(serviceRepository).deleteById(1L);




/*HUESPED */
		Huesped entity2 = new Huesped(id);
		when(hostRepository.findById(id)).thenReturn(java.util.Optional.of(entity2));
        // Llamar al método deleteById del servicio
        ResponseEntity<?> response2 = servHost.deleteById(entity2.getId());
        // Verificar que el método devuelva true
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        // Verificar que el repositorio haya sido llamado con el id correcto
        verify(hostRepository).deleteById(1L);





/*HABITACION */
		Habitacion entity3 = new Habitacion(id);
		when(roomRepository.findById(id)).thenReturn(java.util.Optional.of(entity3));
        // Llamar al método deleteById del servicio
        ResponseEntity<?> response3 = servicioHabitacion.deleteById(entity3.getId());
        // Verificar que el método devuelva true
        assertEquals(HttpStatus.OK, response3.getStatusCode());
        // Verificar que el repositorio haya sido llamado con el id correcto
        verify(roomRepository).deleteById(1L);




/*HOTEL */
		Hotel entity4 = new Hotel(id);
		when(hotelRepository.findById(id)).thenReturn(java.util.Optional.of(entity4));
        // Llamar al método deleteById del servicio
        ResponseEntity<?> response4 = servicioHotel.deleteById(entity4.getId());
        // Verificar que el método devuelva true
        assertEquals(HttpStatus.OK, response4.getStatusCode());
        // Verificar que el repositorio haya sido llamado con el id correcto
        verify(hotelRepository).deleteById(1L);
		

    }



	/*@Test
	public void fechasTest(){
		Huesped huesped = new Huesped(LocalDateTime.of(2025, 7, 14, 15, 30), LocalDateTime.of(2025, 7, 28, 15, 30));
		when(servHost.post(huesped)).thenReturn(huesped);
	}*/

	/*@Test
	void contextLoads() {
	}*/

}
