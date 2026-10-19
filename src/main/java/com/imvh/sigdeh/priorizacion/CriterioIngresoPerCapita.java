package com.imvh.sigdeh.priorizacion;

import com.imvh.sigdeh.modelo.Solicitud;

// A menor ingreso per cápita, más puntaje.
public class CriterioIngresoPerCapita extends CriterioBase {

    // Ingreso mensual per cápita (en pesos) a partir del cual el criterio no suma puntos.
    private static final double TOPE_INGRESO_PER_CAPITA = 500000.0;

    @Override
    protected double calcularPuntajeBruto(Solicitud solicitud) {

        double perCapita = solicitud.getGrupoFamiliar().ingresoPerCapita();
        return (1.0 - perCapita / TOPE_INGRESO_PER_CAPITA) * 100.0;

    }

}
