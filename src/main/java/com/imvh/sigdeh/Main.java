package com.imvh.sigdeh;

import com.imvh.sigdeh.dao.EsquemaPriorizacionDAO;
import com.imvh.sigdeh.dao.GrupoFamiliarDAO;
import com.imvh.sigdeh.dao.ListadoPrioridadDAO;
import com.imvh.sigdeh.dao.OperatoriaDAO;
import com.imvh.sigdeh.dao.SolicitudDAO;
import com.imvh.sigdeh.dao.mysql.ConexionBD;
import com.imvh.sigdeh.dao.mysql.EsquemaPriorizacionDAOMySQL;
import com.imvh.sigdeh.dao.mysql.GrupoFamiliarDAOMySQL;
import com.imvh.sigdeh.dao.mysql.ListadoPrioridadDAOMySQL;
import com.imvh.sigdeh.dao.mysql.OperatoriaDAOMySQL;
import com.imvh.sigdeh.dao.mysql.SolicitudDAOMySQL;
import com.imvh.sigdeh.excepciones.PersistenciaException;
import com.imvh.sigdeh.gestor.GestorDePriorizacion;
import com.imvh.sigdeh.gestor.GestorDeSolicitudes;
import com.imvh.sigdeh.vista.Consola;
import com.imvh.sigdeh.vista.MenuPrincipal;

// Punto de entrada: verifica la conexión, arma DAO, gestores y vista, y lanza el menú.
public class Main {

    public static void main(String[] args) {

        Consola consola = new Consola();
        try {
            // 0. Se prueba la conexión antes de mostrar el menú: sin base de datos no se puede trabajar
            ConexionBD.getInstancia().getConexion();
            System.out.println("Conexión con la base de datos establecida.");

            // 1. Acceso a datos: implementaciones MySQL detrás de las mismas interfaces del TP3
            SolicitudDAO solicitudDAO = new SolicitudDAOMySQL();
            GrupoFamiliarDAO grupoFamiliarDAO = new GrupoFamiliarDAOMySQL();
            OperatoriaDAO operatoriaDAO = new OperatoriaDAOMySQL();
            EsquemaPriorizacionDAO esquemaDAO = new EsquemaPriorizacionDAOMySQL();
            ListadoPrioridadDAO listadoDAO = new ListadoPrioridadDAOMySQL();

            // 2. Lógica de negocio
            GestorDeSolicitudes gestorSolicitudes = new GestorDeSolicitudes(solicitudDAO, grupoFamiliarDAO);
            GestorDePriorizacion gestorPriorizacion =
                    new GestorDePriorizacion(operatoriaDAO, solicitudDAO, esquemaDAO, listadoDAO);

            // 3. Presentación
            new MenuPrincipal(gestorSolicitudes, gestorPriorizacion, consola).iniciar();

        } catch (PersistenciaException e) {
            System.out.println("No se pudo iniciar SIGDEH. " + e.getMessage());
        } finally {
            ConexionBD.getInstancia().cerrar();   // se cierra la conexión aunque ocurra un error
            consola.cerrar();
        }

    }
}
