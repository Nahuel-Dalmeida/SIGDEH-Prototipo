package com.imvh.sigdeh.dao;

import com.imvh.sigdeh.excepciones.PersistenciaException;
import com.imvh.sigdeh.modelo.ListadoPrioridad;

import java.util.List;

// Contrato de persistencia de listados de prioridad.
public interface ListadoPrioridadDAO {

    // Guarda el listado y le asigna su identificador.
    void guardar(ListadoPrioridad listado) throws PersistenciaException;

    // Historial de listados emitidos. Cada fila es un arreglo de columnas:
    // {id, fecha de emisión, operatoria, versión del esquema, cantidad de posiciones}.
    List<String[]> buscarHistorial() throws PersistenciaException;

}