package com.imvh.sigdeh.modelo;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Unidad de información del dominio: un titular con sus integrantes.
public class GrupoFamiliar {

    private int idGrupoFamiliar;                    // 0 = todavía no persistido
    private String situacionHabitacional;
    private double ingresoNeto;
    private final LocalDate fechaInicioResidencia;
    private final List<Integrante> integrantes;

    public GrupoFamiliar(String situacionHabitacional, double ingresoNeto, LocalDate fechaInicioResidencia) {

        this.situacionHabitacional = situacionHabitacional;
        this.ingresoNeto = ingresoNeto;
        this.fechaInicioResidencia = fechaInicioResidencia;
        this.integrantes = new ArrayList<>();

    }

    // Comportamiento del dominio

    // Agrega un integrante al grupo (el primero cargado es el titular).
    public void agregarIntegrante(Integrante integrante) {

        integrantes.add(integrante);

    }

    // Años completos de residencia en la localidad (anosDeResidencia() del diagrama de diseño).
    public int aniosDeResidencia() {

        return Period.between(fechaInicioResidencia, LocalDate.now()).getYears();

    }

    // Meses completos de residencia en la localidad.
    public long mesesDeResidencia() {

        return ChronoUnit.MONTHS.between(fechaInicioResidencia, LocalDate.now());

    }

    public int cantidadIntegrantes() {

        return integrantes.size();

    }

    // Ingreso neto familiar dividido la cantidad de integrantes.
    public double ingresoPerCapita() {

        if (integrantes.isEmpty()) {
            return ingresoNeto;
        }
        return ingresoNeto / integrantes.size();

    }

    // Devuelve true si al menos un integrante tiene discapacidad.
    public boolean tieneDiscapacidad() {

        for (Integrante integrante : integrantes) {
            if (integrante.tieneDiscapacidad()) {
                return true;
            }
        }
        return false;

    }

    // Devuelve el titular (primer integrante cargado) o null si todavía no hay integrantes.
    public Integrante getTitular() {

        if (integrantes.isEmpty()) {
            return null;
        }
        return integrantes.get(0);

    }

    //Getters y setters

    public int getIdGrupoFamiliar() {

        return idGrupoFamiliar;

    }

    public void setIdGrupoFamiliar(int idGrupoFamiliar) {

        this.idGrupoFamiliar = idGrupoFamiliar;

    }

    public String getSituacionHabitacional() {

        return situacionHabitacional;

    }

    public void setSituacionHabitacional(String situacionHabitacional) {

        this.situacionHabitacional = situacionHabitacional;

    }

    public double getIngresoNeto() {

        return ingresoNeto;

    }

    public void setIngresoNeto(double ingresoNeto) {

        this.ingresoNeto = ingresoNeto;

    }

    public LocalDate getFechaInicioResidencia() {

        return fechaInicioResidencia;

    }

    // Devuelve una vista de solo lectura: la lista interna solo se modifica con agregarIntegrante().
    public List<Integrante> getIntegrantes() {

        return Collections.unmodifiableList(integrantes);

    }

}
