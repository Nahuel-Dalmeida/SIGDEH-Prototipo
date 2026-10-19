package com.imvh.sigdeh.modelo;

import java.time.LocalDate;

// Persona que integra un grupo familiar (el titular también es integrante).
public class Integrante {

    private final String dni;
    private final String apellidoNombre;
    private final LocalDate fechaNacimiento;
    private final String vinculo;
    private final boolean discapacidad;

    public Integrante(String dni, String apellidoNombre, LocalDate fechaNacimiento,
                      String vinculo, boolean discapacidad) {

        this.dni = dni;
        this.apellidoNombre = apellidoNombre;
        this.fechaNacimiento = fechaNacimiento;
        this.vinculo = vinculo;
        this.discapacidad = discapacidad;

    }

    public String getDni() {

        return dni;

    }

    public String getApellidoNombre() {

        return apellidoNombre;

    }

    public LocalDate getFechaNacimiento() {

        return fechaNacimiento;

    }

    public String getVinculo() {

        return vinculo;

    }

    public boolean tieneDiscapacidad() {

        return discapacidad;

    }

    @Override
    public String toString() {

        return apellidoNombre + " (DNI " + dni + ", " + vinculo + ")";

    }

}
