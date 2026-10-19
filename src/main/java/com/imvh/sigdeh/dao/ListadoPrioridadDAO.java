package com.imvh.sigdeh.dao;

import com.imvh.sigdeh.modelo.ListadoPrioridad;

// Contrato de persistencia de listados de prioridad.
public interface ListadoPrioridadDAO {

    // Guarda el listado y le asigna su identificador.
    void guardar(ListadoPrioridad listado);

}
