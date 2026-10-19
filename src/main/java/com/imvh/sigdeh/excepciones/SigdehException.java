package com.imvh.sigdeh.excepciones;

// Excepción base de SIGDEH: permite capturar cualquier error del sistema con un solo catch.
public class SigdehException extends Exception {

    private static final long serialVersionUID = 1L;

    public SigdehException(String mensaje) {

        super(mensaje);

    }

}
