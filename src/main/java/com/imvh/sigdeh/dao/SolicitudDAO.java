package com.imvh.sigdeh.dao;

import com.imvh.sigdeh.excepciones.PersistenciaException;
import com.imvh.sigdeh.modelo.Solicitud;

import java.util.List;

// Contrato de persistencia de solicitudes.
public interface SolicitudDAO {

    // Guarda una solicitud nueva (le asigna número) o actualiza una existente.
    void guardar(Solicitud solicitud) throws PersistenciaException;

    // Devuelve la solicitud con ese número, o null si no existe.
    Solicitud buscarPorId(int numero) throws PersistenciaException;

    // Devuelve la solicitud en la que figura el DNI (como titular o integrante), o null si no figura.
    Solicitud buscarPorDni(String dni) throws PersistenciaException;

    // Devuelve todas las solicitudes registradas.
    List<Solicitud> buscarTodas() throws PersistenciaException;

    // Borra la solicitud junto con su grupo familiar, sus integrantes y sus posiciones en listados.
    void eliminar(Solicitud solicitud) throws PersistenciaException;

}
