package com.imvh.sigdeh.dao;

import com.imvh.sigdeh.modelo.Operatoria;

import java.util.List;

// Contrato de persistencia de operatorias (con sus universos).
public interface OperatoriaDAO {

    // Guarda la operatoria y sus universos, asignándoles identificador.
    void guardar(Operatoria operatoria);

    // Devuelve la operatoria con ese id, o null si no existe.
    Operatoria buscarPorId(int id);

    // Devuelve las operatorias en estado ABIERTA.
    List<Operatoria> buscarAbiertas();

}
