package com.imvh.sigdeh.excepciones;

// Error al leer o escribir un archivo (configuración o exportación de listados).
public class ArchivoException extends SigdehException {

    private static final long serialVersionUID = 1L;

    public ArchivoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
