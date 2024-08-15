package com.proyecto.hoteles.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.hoteles.auth.requests.AuthResponse;
import com.proyecto.hoteles.auth.requests.LoginRequest;
import com.proyecto.hoteles.auth.requests.RegisterRequest;
import com.proyecto.hoteles.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"*"})
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));

    }
    
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));

    }

     
   
}
