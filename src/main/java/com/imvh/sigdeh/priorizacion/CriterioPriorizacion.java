package com.imvh.sigdeh.priorizacion;

import com.imvh.sigdeh.modelo.Solicitud;

// Estrategia de priorización (patrón Strategy): cada criterio implementa su propia regla.
public interface CriterioPriorizacion {

    // Devuelve el puntaje parcial (0 a 100) que obtiene la solicitud según este criterio.
    double calcularPuntaje(Solicitud solicitud);

    // Código con el que el criterio se guarda en la columna criterios.estrategia de la base.
    String getCodigo();

}
