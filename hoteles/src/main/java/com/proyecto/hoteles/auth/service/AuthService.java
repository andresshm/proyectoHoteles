package com.proyecto.hoteles.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.proyecto.hoteles.auth.jwt.JwtService;
import com.proyecto.hoteles.auth.requests.AuthResponse;
import com.proyecto.hoteles.auth.requests.LoginRequest;
import com.proyecto.hoteles.auth.requests.RegisterRequest;
import com.proyecto.hoteles.auth.user.Role;
import com.proyecto.hoteles.auth.user.User;
import com.proyecto.hoteles.auth.user.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request){
       authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails user=userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token=jwtService.getToken(user);
        return AuthResponse.builder()
            .token(token)
            .build();
    }
   
   
    public AuthResponse register(RegisterRequest request){
        User user = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode( request.getPassword()))
            .fistname(request.getFistname())
            .lastname(request.getLastname())
            .location(request.getLocation())
            .role(Role.USER)
            .build();

        userRepository.save(user);

        return AuthResponse.builder()
                .token(jwtService.getToken(user))
                .build();
    }

}
