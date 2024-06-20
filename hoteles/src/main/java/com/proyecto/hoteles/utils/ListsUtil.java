package com.proyecto.hoteles.utils;

import java.util.List;
import java.util.Set;

public class ListsUtil {

    public static <T> Set<T> interseccionSinListaVacia(Set<T> lista1, List<T> lista2) {
        if (lista1.isEmpty()) {
            lista1.addAll(lista2);
        } else {
            lista1.retainAll(lista2);
        }

        return lista1;
        
        
        /*return Stream.of(lista1, lista2)
                .filter(list -> !list.isEmpty())
                .reduce((list1, list2) -> {
                    list1.retainAll(list2);
                    return list1;
                })
                .orElseGet(ArrayList::new);*/
    }
}
