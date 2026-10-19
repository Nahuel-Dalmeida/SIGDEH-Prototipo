package com.imvh.sigdeh.dao.memoria;

import com.imvh.sigdeh.dao.EsquemaPriorizacionDAO;
import com.imvh.sigdeh.priorizacion.EsquemaPriorizacion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Implementación en memoria (lista) de EsquemaPriorizacionDAO. En el TP4 se reemplaza por una versión JDBC.
public class EsquemaPriorizacionDAOMemoria implements EsquemaPriorizacionDAO {

    private final List<EsquemaPriorizacion> esquemas = new ArrayList<>();

    @Override
    public void guardar(EsquemaPriorizacion esquema) {

        esquemas.add(esquema);

    }

    // Elige el esquema más reciente que ya está en vigencia.
    @Override
    public EsquemaPriorizacion buscarVigente() {

        LocalDate hoy = LocalDate.now();
        EsquemaPriorizacion vigente = null;
        for (EsquemaPriorizacion esquema : esquemas) {
            boolean yaRige = !esquema.getFechaVigencia().isAfter(hoy);
            boolean esMasReciente = vigente == null
                    || esquema.getFechaVigencia().isAfter(vigente.getFechaVigencia());
            if (yaRige && esMasReciente) {
                vigente = esquema;
            }
        }
        return vigente;

    }

}
