package com.imvh.sigdeh.util;

import java.util.Comparator;
import java.util.List;

// Ordenación por inserción escrita a mano (la usa el listado de prioridad).
public final class Ordenador {

    private Ordenador() {
    }

    // Ordena la lista de menor a mayor según el comparador (es estable).
    public static <T> void ordenarPorInsercion(List<T> lista, Comparator<? super T> comparador) {

        for (int i = 1; i < lista.size(); i++) {
            T actual = lista.get(i);
            int j = i - 1;
            // Desplazamos hacia la derecha los elementos mayores que "actual"
            while (j >= 0 && comparador.compare(lista.get(j), actual) > 0) {
                lista.set(j + 1, lista.get(j));
                j--;
            }
            lista.set(j + 1, actual);
        }

    }

}
