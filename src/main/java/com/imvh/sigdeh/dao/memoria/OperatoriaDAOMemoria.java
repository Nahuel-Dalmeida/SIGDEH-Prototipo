package com.imvh.sigdeh.dao.memoria;

import com.imvh.sigdeh.dao.OperatoriaDAO;
import com.imvh.sigdeh.modelo.Operatoria;
import com.imvh.sigdeh.modelo.Universo;

import java.util.ArrayList;
import java.util.List;

// Implementación en memoria (lista) de OperatoriaDAO. En el TP4 se reemplaza por una versión JDBC.
public class OperatoriaDAOMemoria implements OperatoriaDAO {

    private final List<Operatoria> operatorias = new ArrayList<>();
    private int proximoIdOperatoria = 1;
    private int proximoIdUniverso = 1;

    @Override
    public void guardar(Operatoria operatoria) {

        if (operatoria.getId() == 0) {
            operatoria.setId(proximoIdOperatoria++);
            operatorias.add(operatoria);
        }
        // Los universos se persisten junto con su operatoria
        for (Universo universo : operatoria.getUniversos()) {
            if (universo.getId() == 0) {
                universo.setId(proximoIdUniverso++);
            }
        }

    }

    // Búsqueda lineal por id.
    @Override
    public Operatoria buscarPorId(int id) {

        for (Operatoria operatoria : operatorias) {
            if (operatoria.getId() == id) {
                return operatoria;
            }
        }
        return null;

    }

    // Filtra las operatorias en estado ABIERTA.
    @Override
    public List<Operatoria> buscarAbiertas() {

        List<Operatoria> abiertas = new ArrayList<>();
        for (Operatoria operatoria : operatorias) {
            if (operatoria.estaAbierta()) {
                abiertas.add(operatoria);
            }
        }
        return abiertas;

    }

}
