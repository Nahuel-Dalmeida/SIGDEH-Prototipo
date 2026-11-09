package com.imvh.sigdeh.util;

import com.imvh.sigdeh.excepciones.ArchivoException;
import com.imvh.sigdeh.modelo.ListadoPrioridad;
import com.imvh.sigdeh.modelo.Posicion;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// Exporta un listado de prioridad a un archivo CSV que se puede abrir con Excel
// (por ejemplo, para publicarlo o adjuntarlo al acta de la operatoria).
public final class ExportadorCSV {

    private static final String CARPETA = "exportaciones";
    private static final String SEPARADOR = ";";        // Excel en español usa ; como separador
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Encabezado fijo del archivo: un arreglo, porque las columnas no cambian.
    private static final String[] ENCABEZADO = {"Universo", "Orden", "Solicitud", "Titular", "Puntaje", "Dentro del cupo"};

    private ExportadorCSV() {
    }

    // Escribe el archivo y devuelve su ruta.
    public static Path exportar(ListadoPrioridad listado) throws ArchivoException {
        Path archivo = Paths.get(CARPETA, "listado_" + listado.getId() + ".csv");
        try {
            Files.createDirectories(archivo.getParent());
            try (BufferedWriter salida = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
                salida.write('﻿');                  // marca BOM: Excel reconoce los acentos
                salida.write("Listado de prioridad N° " + listado.getId() + SEPARADOR + listado.getOperatoria());
                salida.newLine();
                salida.write("Emitido" + SEPARADOR + listado.getFechaEmision().format(FORMATO)
                        + SEPARADOR + "Versión del esquema" + SEPARADOR + listado.getVersionEsquema());
                salida.newLine();
                salida.write(String.join(SEPARADOR, ENCABEZADO));
                salida.newLine();
                for (Posicion posicion : listado.getPosiciones()) {
                    salida.write(String.join(SEPARADOR, aFila(posicion)));
                    salida.newLine();
                }
            }
            return archivo.toAbsolutePath();
        } catch (IOException e) {
            throw new ArchivoException("No se pudo escribir el archivo " + archivo + ": " + e.getMessage(), e);
        }
    }

    // Convierte una posición en un arreglo con una celda por columna del encabezado.
    private static String[] aFila(Posicion posicion) {
        String[] campos = new String[ENCABEZADO.length];
        campos[0] = posicion.getUniverso().getNombre();
        campos[1] = String.valueOf(posicion.getOrden());
        campos[2] = String.valueOf(posicion.getSolicitud().getNumero());
        campos[3] = posicion.getSolicitud().getGrupoFamiliar().getTitular().getApellidoNombre();
        campos[4] = String.format(Locale.forLanguageTag("es-AR"), "%.2f", posicion.getPuntaje());
        campos[5] = posicion.getOrden() <= posicion.getUniverso().getCupo() ? "SI" : "NO";
        return campos;
    }
}
