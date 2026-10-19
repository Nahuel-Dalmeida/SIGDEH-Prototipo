package com.imvh.sigdeh.excepciones;

// No existe la entidad buscada por su identificador.
public class EntidadNoEncontradaException extends SigdehException {

    private static final long serialVersionUID = 1L;

    public EntidadNoEncontradaException(String entidad, int id) {

        super("No existe " + entidad + " con el número " + id + ".");

    }

}
