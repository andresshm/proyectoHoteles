package com.proyecto.hoteles.utils;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    private static final DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("d-M-yyyy");
    private static final DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) 
            throws IOException, JsonProcessingException {
        String date = p.getText();
        try {
            return LocalDate.parse(date, formatter1);
        } catch (DateTimeParseException e) {
            return LocalDate.parse(date, formatter2);
        }
    }
}
