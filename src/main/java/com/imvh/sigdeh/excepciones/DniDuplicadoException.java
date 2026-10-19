package com.imvh.sigdeh.excepciones;

// DNI repetido en el grupo o ya registrado en otra solicitud (RF03).
public class DniDuplicadoException extends SigdehException {

    private static final long serialVersionUID = 1L;

    private final String dni;

    // El DNI ya figura en una solicitud registrada.
    public DniDuplicadoException(String dni, int numeroSolicitud) {

        super("El DNI " + dni + " ya figura en la solicitud N° " + numeroSolicitud + ".");
        this.dni = dni;

    }

    // El DNI está repetido dentro de los datos que se están cargando.
    public DniDuplicadoException(String dni) {

        super("El DNI " + dni + " está repetido dentro del mismo grupo familiar.");
        this.dni = dni;

    }

    public String getDni() {

        return dni;

    }

}
