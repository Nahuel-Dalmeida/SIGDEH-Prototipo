package com.imvh.sigdeh.dao.memoria;

import com.imvh.sigdeh.dao.ListadoPrioridadDAO;
import com.imvh.sigdeh.modelo.ListadoPrioridad;

import java.util.ArrayList;
import java.util.List;

// Implementación en memoria (lista) de ListadoPrioridadDAO. En el TP4 se reemplaza por una versión JDBC.
public class ListadoPrioridadDAOMemoria implements ListadoPrioridadDAO {

    private final List<ListadoPrioridad> listados = new ArrayList<>();
    private int proximoId = 1;                       // simula el AUTO_INCREMENT de MySQL

    @Override
    public void guardar(ListadoPrioridad listado) {

        if (listado.getId() == 0) {
            listado.setId(proximoId++);
            listados.add(listado);
        }

    }

}
