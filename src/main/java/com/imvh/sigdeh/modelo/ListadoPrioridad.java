package com.imvh.sigdeh.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Listado emitido para una operatoria, con la versión del esquema aplicada (RF10).
public class ListadoPrioridad {

    private int id;                                  // 0 = todavía no persistido
    private final Operatoria operatoria;
    private final LocalDateTime fechaEmision;
    private final int versionEsquema;
    private final List<Posicion> posiciones;

    public ListadoPrioridad(Operatoria operatoria, int versionEsquema, List<Posicion> posiciones) {

        this.operatoria = operatoria;
        this.versionEsquema = versionEsquema;
        this.fechaEmision = LocalDateTime.now();
        this.posiciones = new ArrayList<>(posiciones);

    }

    // Devuelve las posiciones de un universo, en el orden en que quedaron en el listado.
    public List<Posicion> getPosicionesDeUniverso(Universo universo) {

        List<Posicion> resultado = new ArrayList<>();
        for (Posicion posicion : posiciones) {
            if (posicion.getUniverso().getId() == universo.getId()) {
                resultado.add(posicion);
            }
        }
        return resultado;

    }

    public int getId() {

        return id;

    }

    public void setId(int id) {

        this.id = id;

    }

    public Operatoria getOperatoria() {

        return operatoria;

    }

    public LocalDateTime getFechaEmision() {

        return fechaEmision;

    }

    public int getVersionEsquema() {

        return versionEsquema;

    }

    public List<Posicion> getPosiciones() {

        return Collections.unmodifiableList(posiciones);

    }

}
