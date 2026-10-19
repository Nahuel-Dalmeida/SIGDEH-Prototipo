package com.imvh.sigdeh.priorizacion;

import com.imvh.sigdeh.modelo.GrupoFamiliar;
import com.imvh.sigdeh.modelo.Solicitud;

// Puntaje base según la situación habitacional, más un adicional por discapacidad.
public class CriterioVulnerabilidad extends CriterioBase {

    // Puntos extra si algún integrante tiene discapacidad.
    private static final double ADICIONAL_DISCAPACIDAD = 20.0;

    @Override
    protected double calcularPuntajeBruto(Solicitud solicitud) {

        GrupoFamiliar grupo = solicitud.getGrupoFamiliar();
        double puntaje = puntajeBasePorSituacion(grupo.getSituacionHabitacional());
        if (grupo.tieneDiscapacidad()) {
            puntaje += ADICIONAL_DISCAPACIDAD;
        }
        return puntaje;

    }

    // Puntaje base según la situación habitacional declarada.
    private double puntajeBasePorSituacion(String situacion) {

        String normalizada = situacion.trim().toLowerCase();
        switch (normalizada) {
            case "vivienda precaria":
                return 80.0;
            case "alquiler":
                return 60.0;
            case "cedida":
                return 50.0;
            default:
                return 30.0;            // cualquier otra situación
        }

    }

}
