package com.proyecto.hoteles.entidades;

import lombok.Data;

@Data
public class HuespedesPorHotel {

    private String hotelName;
    private int num;


    public HuespedesPorHotel(String hotelName, int num) {
        this.hotelName = hotelName;
        this.num = num;
    }




}
