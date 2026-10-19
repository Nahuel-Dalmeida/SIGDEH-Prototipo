package com.imvh.sigdeh.modelo;

import java.util.Comparator;

// Lugar de una solicitud dentro del listado: puntaje, orden y universo.
public class Posicion {

    // Orden del listado: mayor puntaje primero; si empatan, la solicitud más antigua.
    public static final Comparator<Posicion> POR_PRIORIDAD = (a, b) -> {
        int porPuntaje = Double.compare(b.puntaje, a.puntaje);
        if (porPuntaje != 0) {
            return porPuntaje;
        }
        int porFecha = a.solicitud.getFechaAlta().compareTo(b.solicitud.getFechaAlta());
        if (porFecha != 0) {
            return porFecha;
        }
        return Integer.compare(a.solicitud.getNumero(), b.solicitud.getNumero());
    };

    private final double puntaje;
    private int orden;                               // se asigna después de ordenar
    private final Solicitud solicitud;
    private final Universo universo;

    public Posicion(Solicitud solicitud, Universo universo, double puntaje) {

        this.solicitud = solicitud;
        this.universo = universo;
        this.puntaje = puntaje;

    }

    public double getPuntaje() {

        return puntaje;

    }

    public int getOrden() {

        return orden;

    }

    public void setOrden(int orden) {

        this.orden = orden;

    }

    public Solicitud getSolicitud() {

        return solicitud;

    }

    public Universo getUniverso() {

        return universo;

    }

}
