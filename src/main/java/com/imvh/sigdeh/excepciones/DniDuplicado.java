package com.imvh.sigdeh.excepciones;

public class DniDuplicado extends RuntimeException {
    public DniDuplicado(String dni) {
        super("DNI " + dni + " ya registrado en otra solicitud.");
    }
}
