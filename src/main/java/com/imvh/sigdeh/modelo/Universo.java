package com.imvh.sigdeh.modelo;

// Segmento de la demanda dentro de una operatoria, con su cupo.
public class Universo {

    private int id;                                  // 0 = todavía no persistido
    private final String nombre;
    private final int cupo;
    private final boolean requiereDiscapacidad;

    public Universo(String nombre, int cupo, boolean requiereDiscapacidad) {

        this.nombre = nombre;
        this.cupo = cupo;
        this.requiereDiscapacidad = requiereDiscapacidad;

    }

    // Indica si la solicitud puede integrar este universo.
    public boolean admite(Solicitud solicitud) {

        if (requiereDiscapacidad) {
            return solicitud.getGrupoFamiliar().tieneDiscapacidad();
        }
        return true;

    }

    public int getId() {

        return id;

    }

    public void setId(int id) {

        this.id = id;

    }

    public String getNombre() {

        return nombre;

    }

    public int getCupo() {

        return cupo;

    }

    public boolean requiereDiscapacidad() {

        return requiereDiscapacidad;

    }

    @Override
    public String toString() {

        return nombre + " (cupo " + cupo + ")";

    }

}
