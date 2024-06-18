package com.proyecto.hoteles.controlador;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.proyecto.hoteles.entidades.Habitacion;
import com.proyecto.hoteles.entidades.Hotel;
import com.proyecto.hoteles.entidades.Huesped;
import com.proyecto.hoteles.repositorios.HotelRepository;

import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@RestController
@RequestMapping("/hotel")
public class HotelRestController {
  @Autowired
    HotelRepository hotelRepository;

    private final WebClient.Builder webClientBuilder; //Utilizamos este cliente porque es reactivo y no bloquea la comunicacion, pero hay mas opciones

    public HotelRestController(WebClient.Builder webClientBuilder){
        this.webClientBuilder = webClientBuilder; 
    }

    HttpClient client = HttpClient.create()
    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
    .option(ChannelOption.SO_KEEPALIVE, true)
    .option(EpollChannelOption.TCP_KEEPIDLE, 300)
    .option(EpollChannelOption.TCP_KEEPINTVL, 60)
    .responseTimeout(Duration.ofSeconds(1))
    .doOnConnected(connection -> {
        connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
        connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
    });

    @GetMapping()
    public List<Hotel> findAll() {
        return hotelRepository.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Optional<Hotel> hotel = hotelRepository.findById(id);
        if(hotel.isPresent()){
            return new ResponseEntity<>(hotel.get(), HttpStatus.OK); 
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable Long id, @RequestBody Hotel input) {
        Optional<Hotel> optionalhotel = hotelRepository.findById(id);
        if(optionalhotel.isPresent()){            
            Hotel newhotel = optionalhotel.get();
            newhotel.setNombre(input.getNombre());
            newhotel.setDireccion(input.getDireccion());
            newhotel.setEmail(input.getEmail());
            newhotel.setSitioWeb(input.getSitioWeb());
            newhotel.setTelefono(input.getTelefono());
            Hotel save = hotelRepository.save(newhotel);
            return new ResponseEntity<>(save, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping()
    public ResponseEntity<?> post(@RequestBody Hotel input) {
        input.getHabitaciones().forEach(x -> x.setHotel(input));
        Hotel save = hotelRepository.save(input);
        return ResponseEntity.ok(save);
    }
    

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        hotelRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/full")
    public ResponseEntity<?> deleteAll(){
        hotelRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }
    /*@GetMapping("/full")
    public Hotel getByName(@RequestPart String name) {
            Hotel hotel = hotelRepository.getByName(name);
            List<Habitacion> habitaciones = hotel.getHabitaciones();
        habitaciones.forEach(x -> {
            String roomNumber = getRoomNumber(x.getId());
            x.setNumero(roomNumber);
        });
        return hotel;
    }




    private String getRoomNumber(long id){
        WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
        .baseUrl("http://localhost:8080/habitacion")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8081/hotel"))
        .build();

        JsonNode block = build.method(HttpMethod.GET).uri("/" + id)
        .retrieve().bodyToMono(JsonNode.class).block();

        String number = block.get("numero").asText();
        return number;

    }*/
}
