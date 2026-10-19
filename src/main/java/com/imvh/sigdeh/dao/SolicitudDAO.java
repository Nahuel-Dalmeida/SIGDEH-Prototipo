package com.imvh.sigdeh.dao;

import com.imvh.sigdeh.modelo.Solicitud;

import java.util.List;

// Contrato de persistencia de solicitudes.
public interface SolicitudDAO {

    // Guarda una solicitud nueva (le asigna número) o actualiza una existente.
    void guardar(Solicitud solicitud);

    // Devuelve la solicitud con ese número, o null si no existe.
    Solicitud buscarPorId(int numero);

    // Devuelve la solicitud en la que figura el DNI (como titular o integrante), o null si no figura.
    Solicitud buscarPorDni(String dni);

    // Devuelve todas las solicitudes registradas.
    List<Solicitud> buscarTodas();

}
