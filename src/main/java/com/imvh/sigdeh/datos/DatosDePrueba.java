package com.imvh.sigdeh.datos;

import com.imvh.sigdeh.dao.EsquemaPriorizacionDAO;
import com.imvh.sigdeh.dao.GrupoFamiliarDAO;
import com.imvh.sigdeh.dao.OperatoriaDAO;
import com.imvh.sigdeh.dao.SolicitudDAO;
import com.imvh.sigdeh.modelo.EstadoOperatoria;
import com.imvh.sigdeh.modelo.EstadoSolicitud;
import com.imvh.sigdeh.modelo.GrupoFamiliar;
import com.imvh.sigdeh.modelo.Integrante;
import com.imvh.sigdeh.modelo.Operatoria;
import com.imvh.sigdeh.modelo.Solicitud;
import com.imvh.sigdeh.modelo.Universo;
import com.imvh.sigdeh.priorizacion.Criterio;
import com.imvh.sigdeh.priorizacion.CriterioAntiguedadResidencia;
import com.imvh.sigdeh.priorizacion.CriterioIngresoPerCapita;
import com.imvh.sigdeh.priorizacion.CriterioVulnerabilidad;
import com.imvh.sigdeh.priorizacion.EsquemaPriorizacion;

import java.time.LocalDate;

// Datos de ejemplo para probar el sistema sin base de datos (se elimina en el TP4).
public final class DatosDePrueba {

    private DatosDePrueba() {
    }

    public static void cargar(SolicitudDAO solicitudDAO, GrupoFamiliarDAO grupoDAO,
                              OperatoriaDAO operatoriaDAO, EsquemaPriorizacionDAO esquemaDAO) {

        cargarSolicitudes(solicitudDAO, grupoDAO);
        cargarOperatoria(operatoriaDAO);
        cargarEsquema(esquemaDAO);

    }

    private static void cargarSolicitudes(SolicitudDAO solicitudDAO, GrupoFamiliarDAO grupoDAO) {

        // 1. Familia Gómez (TP2): alquiler, residencia desde 2019
        GrupoFamiliar gomez = new GrupoFamiliar("Alquiler", 850000.00, LocalDate.of(2019, 3, 15));
        gomez.agregarIntegrante(new Integrante("30123456", "Gomez, Marta", LocalDate.of(1985, 4, 12), "Titular", false));
        gomez.agregarIntegrante(new Integrante("45123456", "Gomez, Lucas", LocalDate.of(2010, 9, 1), "Hijo/a", false));
        guardar(solicitudDAO, grupoDAO, gomez, LocalDate.of(2024, 5, 10), LocalDate.of(2026, 8, 20));

        // 2. Familia Pérez (TP2): vivienda precaria, titular con discapacidad
        GrupoFamiliar perez = new GrupoFamiliar("Vivienda precaria", 620000.00, LocalDate.of(2015, 7, 1));
        perez.agregarIntegrante(new Integrante("28456789", "Perez, Raul", LocalDate.of(1978, 11, 23), "Titular", true));
        guardar(solicitudDAO, grupoDAO, perez, LocalDate.of(2020, 2, 18), LocalDate.of(2026, 6, 5));

        // 3. Familia Ramírez: residencia de solo ~3 años (queda fuera de los listados) y solicitud desactualizada
        GrupoFamiliar ramirez = new GrupoFamiliar("Alquiler", 400000.00, LocalDate.of(2023, 1, 10));
        ramirez.agregarIntegrante(new Integrante("33222111", "Ramirez, Julia", LocalDate.of(1990, 2, 2), "Titular", false));
        guardar(solicitudDAO, grupoDAO, ramirez, LocalDate.of(2024, 3, 1), LocalDate.of(2025, 3, 1));

        // 4. Familia Sosa: familia numerosa con bajo ingreso per cápita
        GrupoFamiliar sosa = new GrupoFamiliar("Vivienda precaria", 300000.00, LocalDate.of(2017, 9, 20));
        sosa.agregarIntegrante(new Integrante("27111222", "Sosa, Carlos", LocalDate.of(1983, 6, 30), "Titular", false));
        sosa.agregarIntegrante(new Integrante("50111222", "Sosa, Ana", LocalDate.of(2015, 1, 10), "Hijo/a", false));
        sosa.agregarIntegrante(new Integrante("51111222", "Sosa, Pedro", LocalDate.of(2018, 3, 5), "Hijo/a", false));
        guardar(solicitudDAO, grupoDAO, sosa, LocalDate.of(2023, 8, 14), LocalDate.of(2026, 9, 1));

    }

    // Guarda el grupo y su solicitud con fechas ya conocidas.
    private static void guardar(SolicitudDAO solicitudDAO, GrupoFamiliarDAO grupoDAO, GrupoFamiliar grupo,
                                LocalDate fechaAlta, LocalDate fechaUltimaActualizacion) {

        grupoDAO.guardar(grupo);
        solicitudDAO.guardar(new Solicitud(grupo, fechaAlta, fechaUltimaActualizacion, EstadoSolicitud.ACTIVA));

    }

    // Operatoria abierta con un universo general y otro para discapacidad.
    private static void cargarOperatoria(OperatoriaDAO operatoriaDAO) {

        Operatoria operatoria = new Operatoria("Lote con servicios", 40, LocalDate.of(2026, 10, 1),
                EstadoOperatoria.ABIERTA);
        operatoria.agregarUniverso(new Universo("Universo General", 30, false));
        operatoria.agregarUniverso(new Universo("Universo Discapacidad", 10, true));
        operatoriaDAO.guardar(operatoria);

    }

    // Esquema versión 3 con pesos 0,30 / 0,40 / 0,30.
    private static void cargarEsquema(EsquemaPriorizacionDAO esquemaDAO) {

        EsquemaPriorizacion esquema = new EsquemaPriorizacion(3, LocalDate.of(2026, 1, 1));
        esquema.agregarCriterio(new Criterio("Antiguedad de residencia", 0.30, new CriterioAntiguedadResidencia()));
        esquema.agregarCriterio(new Criterio("Ingreso per capita", 0.40, new CriterioIngresoPerCapita()));
        esquema.agregarCriterio(new Criterio("Vulnerabilidad del grupo familiar", 0.30, new CriterioVulnerabilidad()));
        esquemaDAO.guardar(esquema);

    }

}
