package com.imvh.sigdeh.dao;

import com.imvh.sigdeh.excepciones.PersistenciaException;
import com.imvh.sigdeh.modelo.Operatoria;

import java.util.List;

// Contrato de persistencia de operatorias (con sus universos).
public interface OperatoriaDAO {

    // Guarda la operatoria y sus universos, asignándoles identificador.
    void guardar(Operatoria operatoria) throws PersistenciaException;

    // Devuelve la operatoria con ese id, o null si no existe.
    Operatoria buscarPorId(int id) throws PersistenciaException;

    // Devuelve las operatorias en estado ABIERTA.
    List<Operatoria> buscarAbiertas() throws PersistenciaException;

}
