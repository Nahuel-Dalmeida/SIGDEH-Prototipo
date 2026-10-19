package com.imvh.sigdeh.priorizacion;

import com.imvh.sigdeh.modelo.Solicitud;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Conjunto versionado de criterios con sus pesos.
public class EsquemaPriorizacion {

    private final int version;
    private final LocalDate fechaVigencia;
    private final List<Criterio> criterios;

    public EsquemaPriorizacion(int version, LocalDate fechaVigencia) {

        this.version = version;
        this.fechaVigencia = fechaVigencia;
        this.criterios = new ArrayList<>();

    }

    public void agregarCriterio(Criterio criterio) {

        criterios.add(criterio);

    }

    // Suma ponderada de todos los criterios. Si los pesos suman 1,00, el resultado queda entre 0 y 100.
    public double calcularPuntaje(Solicitud solicitud) {

        double total = 0.0;
        for (Criterio criterio : criterios) {
            total += criterio.puntajePonderado(solicitud);
        }
        return total;

    }

    // Suma de los pesos de los criterios (debe dar 1,00).
    public double getSumaPesos() {

        double suma = 0.0;
        for (Criterio criterio : criterios) {
            suma += criterio.getPeso();
        }
        return suma;

    }

    public int getVersion() {

        return version;

    }

    public LocalDate getFechaVigencia() {

        return fechaVigencia;

    }

    public List<Criterio> getCriterios() {

        return Collections.unmodifiableList(criterios);

    }

}
