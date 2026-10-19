package com.imvh.sigdeh.dao.memoria;

import com.imvh.sigdeh.dao.GrupoFamiliarDAO;
import com.imvh.sigdeh.modelo.GrupoFamiliar;

import java.util.ArrayList;
import java.util.List;

// Implementación en memoria (lista) de GrupoFamiliarDAO. En el TP4 se reemplaza por una versión JDBC.
public class GrupoFamiliarDAOMemoria implements GrupoFamiliarDAO {

    private final List<GrupoFamiliar> grupos = new ArrayList<>();
    private int proximoId = 1;                       // simula el AUTO_INCREMENT de MySQL

    @Override
    public void guardar(GrupoFamiliar grupoFamiliar) {

        if (grupoFamiliar.getIdGrupoFamiliar() == 0) {
            grupoFamiliar.setIdGrupoFamiliar(proximoId++);
            grupos.add(grupoFamiliar);
        }
        // Si ya tenía id, es el mismo objeto en memoria: no hay nada más que hacer.

    }

}
