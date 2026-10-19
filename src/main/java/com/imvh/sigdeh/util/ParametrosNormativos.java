package com.imvh.sigdeh.util;

// Constantes de la normativa, centralizadas en un solo lugar.
public final class ParametrosNormativos {

    // Residencia mínima para inscribirse, en meses (1 año). RF02.
    public static final int MESES_RESIDENCIA_INSCRIPCION = 12;

    // Antigüedad máxima plausible, en meses (100 años). Por encima se asume error de carga.
    public static final int MESES_RESIDENCIA_MAXIMA = 1200;

    // Residencia mínima para participar de un sorteo / listado, en años. RF08.
    public static final int ANIOS_RESIDENCIA_LISTADO = 5;

    // Meses sin actualizar a partir de los cuales una solicitud se señala como desactualizada. RF04.
    public static final int MESES_VIGENCIA_SOLICITUD = 12;

    // Tolerancia al verificar que los pesos de un esquema sumen 1,00.
    public static final double TOLERANCIA_SUMA_PESOS = 0.001;

    // Constructor privado: es una clase de constantes, no se instancia.
    private ParametrosNormativos() {
    }

}
