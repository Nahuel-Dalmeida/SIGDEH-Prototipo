package com.imvh.sigdeh.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Convocatoria con una cantidad de soluciones, segmentada en universos.
public class Operatoria {

    private int id;                                  // 0 = todavía no persistida
    private final String tipoSolucion;
    private final int cantidadDisponible;
    private final LocalDate fechaConvocatoria;
    private EstadoOperatoria estado;
    private final List<Universo> universos;

    public Operatoria(String tipoSolucion, int cantidadDisponible,
                      LocalDate fechaConvocatoria, EstadoOperatoria estado) {

        this.tipoSolucion = tipoSolucion;
        this.cantidadDisponible = cantidadDisponible;
        this.fechaConvocatoria = fechaConvocatoria;
        this.estado = estado;
        this.universos = new ArrayList<>();

    }

    public void agregarUniverso(Universo universo) {

        universos.add(universo);

    }

    // Indica si la operatoria está abierta para emitir listados.
    public boolean estaAbierta() {

        return estado == EstadoOperatoria.ABIERTA;

    }

    public int getId() {

        return id;

    }

    public void setId(int id) {

        this.id = id;

    }

    public String getTipoSolucion() {

        return tipoSolucion;

    }

    public int getCantidadDisponible() {

        return cantidadDisponible;

    }

    public LocalDate getFechaConvocatoria() {

        return fechaConvocatoria;

    }

    public EstadoOperatoria getEstado() {

        return estado;

    }

    public void setEstado(EstadoOperatoria estado) {

        this.estado = estado;

    }

    public List<Universo> getUniversos() {

        return Collections.unmodifiableList(universos);

    }

    @Override
    public String toString() {

        return "Operatoria " + id + " - " + tipoSolucion + " (" + cantidadDisponible + " soluciones)";

    }

}
