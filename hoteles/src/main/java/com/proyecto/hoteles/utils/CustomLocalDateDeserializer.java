package com.proyecto.hoteles.utils;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomLocalDateDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("d/M/yyyy H:m");
    private static final DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) 
            throws IOException, JsonProcessingException {
        String date = p.getText();
        try {
            return LocalDateTime.parse(date, formatter1);
        } catch (DateTimeParseException e) {
            return LocalDateTime.parse(date, formatter2);
        }
    }
}
