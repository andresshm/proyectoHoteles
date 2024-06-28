package com.proyecto.hoteles.utils;

import java.util.List;
import java.util.Set;

public class ListsUtil {

    public static <T> Set<T> interseccionSinListaVacia(Set<T> lista1, List<T> lista2, List<Boolean> n) {
        boolean p = n.stream().anyMatch(b-> b && lista1.isEmpty()); //Si hay algun atributo anterior que no es nulo y ha dado una lista vacia porque no se han encontrado casos, entonces devolver lista vacia

        if(p)
            return lista1;

        if (lista1.isEmpty()) {//si la lista esta vacia porque el campo que da la lista1 es null:
            lista1.addAll(lista2);
        } else {//si el campo esta rellenado devuelve la interseccion
            lista1.retainAll(lista2);
        }
        //si el campo esta rellenado y da una lista vacia xq no hay ningun cliente asi: devuelve la lista2 *deberia devolver vacia

        return lista1;
        
    }
}
