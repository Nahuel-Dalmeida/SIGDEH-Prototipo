package com.imvh.sigdeh.priorizacion;

import com.imvh.sigdeh.modelo.Solicitud;

// Base común de los criterios: asegura que el puntaje parcial quede entre 0 y 100.
public abstract class CriterioBase implements CriterioPriorizacion {

    private static final double PUNTAJE_MINIMO = 0.0;
    private static final double PUNTAJE_MAXIMO = 100.0;

    // Regla propia de cada criterio. Puede devolver valores fuera de rango; se corrigen abajo.
    protected abstract double calcularPuntajeBruto(Solicitud solicitud);

    @Override
    public final double calcularPuntaje(Solicitud solicitud) {

        double bruto = calcularPuntajeBruto(solicitud);
        return Math.max(PUNTAJE_MINIMO, Math.min(PUNTAJE_MAXIMO, bruto));

    }

}
