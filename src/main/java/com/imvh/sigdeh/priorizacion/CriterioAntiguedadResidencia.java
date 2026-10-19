package com.imvh.sigdeh.priorizacion;

import com.imvh.sigdeh.modelo.Solicitud;

// A más años de residencia, más puntaje (máximo con 20 años).
public class CriterioAntiguedadResidencia extends CriterioBase {

    private static final double ANIOS_PARA_MAXIMO = 20.0;

    @Override
    protected double calcularPuntajeBruto(Solicitud solicitud) {

        int anios = solicitud.getGrupoFamiliar().aniosDeResidencia();
        return anios / ANIOS_PARA_MAXIMO * 100.0;

    }

}
