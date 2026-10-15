package com.imvh.sigdeh.excepciones;

public class ResidenciaInsuficiente extends SigdehException {

    private final long mesesActuales;

    public ResidenciaInsuficiente(long mesesActuales) {
        super("No se cumple el requisito de residencia mínima de 1 año (meses: " + mesesActuales + ").");
        this.mesesActuales = mesesActuales;
    }

    public long getMesesActuales(){
        return mesesActuales;
    }
}
