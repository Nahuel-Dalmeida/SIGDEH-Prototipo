package com.imvh.sigdeh.dao;

import com.imvh.sigdeh.priorizacion.EsquemaPriorizacion;

// Contrato de persistencia de esquemas de priorización.
public interface EsquemaPriorizacionDAO {

    void guardar(EsquemaPriorizacion esquema);

    // Devuelve el esquema vigente (el de fecha de vigencia más reciente que ya rige), o null si no hay.
    EsquemaPriorizacion buscarVigente();

}
