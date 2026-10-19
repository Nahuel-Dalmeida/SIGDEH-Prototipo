package com.imvh.sigdeh;

import com.imvh.sigdeh.dao.EsquemaPriorizacionDAO;
import com.imvh.sigdeh.dao.GrupoFamiliarDAO;
import com.imvh.sigdeh.dao.ListadoPrioridadDAO;
import com.imvh.sigdeh.dao.OperatoriaDAO;
import com.imvh.sigdeh.dao.SolicitudDAO;
import com.imvh.sigdeh.dao.memoria.EsquemaPriorizacionDAOMemoria;
import com.imvh.sigdeh.dao.memoria.GrupoFamiliarDAOMemoria;
import com.imvh.sigdeh.dao.memoria.ListadoPrioridadDAOMemoria;
import com.imvh.sigdeh.dao.memoria.OperatoriaDAOMemoria;
import com.imvh.sigdeh.dao.memoria.SolicitudDAOMemoria;
import com.imvh.sigdeh.datos.DatosDePrueba;
import com.imvh.sigdeh.gestor.GestorDePriorizacion;
import com.imvh.sigdeh.gestor.GestorDeSolicitudes;
import com.imvh.sigdeh.vista.Consola;
import com.imvh.sigdeh.vista.MenuPrincipal;

// Punto de entrada: arma DAO, gestores y vista, y lanza el menú.
public class Main {

    public static void main(String[] args) {

        // 1. Acceso a datos (hoy en memoria)
        SolicitudDAO solicitudDAO = new SolicitudDAOMemoria();
        GrupoFamiliarDAO grupoFamiliarDAO = new GrupoFamiliarDAOMemoria();
        OperatoriaDAO operatoriaDAO = new OperatoriaDAOMemoria();
        EsquemaPriorizacionDAO esquemaDAO = new EsquemaPriorizacionDAOMemoria();
        ListadoPrioridadDAO listadoDAO = new ListadoPrioridadDAOMemoria();

        // Carga datos de ejemplo para probar sin base de datos
        DatosDePrueba.cargar(solicitudDAO, grupoFamiliarDAO, operatoriaDAO, esquemaDAO);

        // 2. Lógica de negocio
        GestorDeSolicitudes gestorSolicitudes = new GestorDeSolicitudes(solicitudDAO, grupoFamiliarDAO);
        GestorDePriorizacion gestorPriorizacion =
                new GestorDePriorizacion(operatoriaDAO, solicitudDAO, esquemaDAO, listadoDAO);

        // 3. Presentación
        Consola consola = new Consola();
        try {
            new MenuPrincipal(gestorSolicitudes, gestorPriorizacion, consola).iniciar();
        } finally {
            consola.cerrar();                        // se cierra la entrada aunque ocurra un error inesperado
        }

    }

}
