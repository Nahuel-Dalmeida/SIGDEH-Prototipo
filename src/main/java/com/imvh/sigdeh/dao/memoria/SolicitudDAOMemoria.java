package com.imvh.sigdeh.dao.memoria;

import com.imvh.sigdeh.dao.SolicitudDAO;
import com.imvh.sigdeh.modelo.Integrante;
import com.imvh.sigdeh.modelo.Solicitud;

import java.util.ArrayList;
import java.util.List;

// Implementación en memoria (lista) de SolicitudDAO. En el TP4 se reemplaza por una versión JDBC.
public class SolicitudDAOMemoria implements SolicitudDAO {

    private final List<Solicitud> solicitudes = new ArrayList<>();
    private int proximoNumero = 1;                   // simula el AUTO_INCREMENT de MySQL

    @Override
    public void guardar(Solicitud solicitud) {

        if (solicitud.getNumero() == 0) {
            solicitud.setNumero(proximoNumero++);
            solicitudes.add(solicitud);
        }
        // Si ya tenía número, es el mismo objeto en memoria: ya está actualizada.

    }

    @Override
    public Solicitud buscarPorId(int numero) {

        // Búsqueda lineal: recorre la lista hasta encontrar el número
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getNumero() == numero) {
                return solicitud;
            }
        }
        return null;

    }

    @Override
    public Solicitud buscarPorDni(String dni) {

        // Búsqueda lineal anidada: solicitudes -> integrantes de su grupo familiar
        for (Solicitud solicitud : solicitudes) {
            for (Integrante integrante : solicitud.getGrupoFamiliar().getIntegrantes()) {
                if (integrante.getDni().equals(dni)) {
                    return solicitud;
                }
            }
        }
        return null;

    }

    @Override
    public List<Solicitud> buscarTodas() {

        // Copia defensiva: quien llama no puede modificar la lista interna
        return new ArrayList<>(solicitudes);

    }

}
