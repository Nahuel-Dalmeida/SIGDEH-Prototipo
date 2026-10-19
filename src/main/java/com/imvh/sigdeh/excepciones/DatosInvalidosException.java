package com.imvh.sigdeh.excepciones;

// Dato con formato o rango inválido (DNI, ingreso, fechas).
public class DatosInvalidosException extends SigdehException {

    private static final long serialVersionUID = 1L;

    public DatosInvalidosException(String mensaje) {

        super(mensaje);

    }

}
