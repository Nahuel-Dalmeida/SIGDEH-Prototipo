package com.imvh.sigdeh.dao;

import com.imvh.sigdeh.excepciones.PersistenciaException;
import com.imvh.sigdeh.modelo.GrupoFamiliar;

// Contrato de persistencia de grupos familiares.
public interface GrupoFamiliarDAO {

    // Guarda el grupo familiar y le asigna su identificador.
    void guardar(GrupoFamiliar grupoFamiliar) throws PersistenciaException;

}
