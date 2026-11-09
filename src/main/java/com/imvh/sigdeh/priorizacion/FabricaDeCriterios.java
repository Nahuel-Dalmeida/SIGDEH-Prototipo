package com.imvh.sigdeh.priorizacion;

import com.imvh.sigdeh.excepciones.DatosInvalidosException;

// Crea la estrategia que corresponde al código guardado en la base (columna criterios.estrategia).
public final class FabricaDeCriterios {

    // Arreglo con todas las estrategias que existen en el sistema. Es de tamaño fijo porque
    // solo cambia cuando se programa un criterio nuevo; agregarlo acá es el único paso extra.
    private static final CriterioPriorizacion[] DISPONIBLES = {
            new CriterioAntiguedadResidencia(),
            new CriterioIngresoPerCapita(),
            new CriterioVulnerabilidad()
    };

    private FabricaDeCriterios() {
    }

    // Búsqueda lineal en el arreglo por código.
    public static CriterioPriorizacion crear(String codigo) throws DatosInvalidosException {
        for (CriterioPriorizacion criterio : DISPONIBLES) {
            if (criterio.getCodigo().equalsIgnoreCase(codigo)) {
                return criterio;            // las estrategias no guardan estado: se pueden compartir
            }
        }
        throw new DatosInvalidosException("El criterio '" + codigo + "' de la base no existe en el sistema.");
    }
}
