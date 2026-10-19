package com.imvh.sigdeh.excepciones;

// Residencia menor al mínimo de un año (RF02).
public class ResidenciaInsuficienteException extends SigdehException {

    private static final long serialVersionUID = 1L;

    private final long mesesDeResidencia;

    public ResidenciaInsuficienteException(long mesesDeResidencia) {

        super("No se cumple el requisito de residencia mínima de 1 año.");
        this.mesesDeResidencia = mesesDeResidencia;

    }

    public long getMesesDeResidencia() {

        return mesesDeResidencia;

    }

}
