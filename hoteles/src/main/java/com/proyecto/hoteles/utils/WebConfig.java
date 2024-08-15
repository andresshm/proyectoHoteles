package com.proyecto.hoteles.utils;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToLocalDateConverter());
    }

    // @Override
    // public void addCorsMappings(CorsRegistry registry) {
    //     registry.addMapping("/**")
    //             .allowedOrigins("http://localhost:4200") // Cambia esto a la URL de tu frontend
    //             .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    //             .allowedHeaders("Content-Type", "Authorization")
    //             .allowCredentials(true);
    // }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("http://localhost:4200").allowedMethods("GET", "POST", "OPTIONS", "PUT")
                .allowedHeaders("Content-Type", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method",
                        "Access-Control-Request-Headers")
                .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
                .allowCredentials(false).maxAge(3600);
    }

}
