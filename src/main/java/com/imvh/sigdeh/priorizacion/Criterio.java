package com.imvh.sigdeh.priorizacion;

import com.imvh.sigdeh.modelo.Solicitud;

// Criterio de un esquema: nombre, peso y la estrategia que calcula el puntaje.
public class Criterio {

    private final String nombre;
    private final double peso;
    private final CriterioPriorizacion estrategia;

    public Criterio(String nombre, double peso, CriterioPriorizacion estrategia) {

        this.nombre = nombre;
        this.peso = peso;
        this.estrategia = estrategia;

    }

    // Puntaje parcial de la estrategia multiplicado por el peso del criterio.
    public double puntajePonderado(Solicitud solicitud) {

        return peso * estrategia.calcularPuntaje(solicitud);

    }

    public String getNombre() {

        return nombre;

    }

    public double getPeso() {

        return peso;

    }

}
