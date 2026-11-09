package com.imvh.sigdeh.dao.mysql;

import com.imvh.sigdeh.excepciones.PersistenciaException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// Conexión única con MySQL (patrón Singleton).
// Los datos de acceso se leen del archivo config/db.properties.ejemplo, así no quedan escritos en el código.
public final class ConexionBD {

    private static final String ARCHIVO_CONFIGURACION = "config/db.properties.ejemplo";
    private static final int SEGUNDOS_VALIDACION = 2;

    private static ConexionBD instancia;

    private Connection conexion;
    private String url;
    private String usuario;
    private String clave;

    // Constructor privado: nadie fuera de la clase puede crear otra instancia.
    private ConexionBD() {
    }

    public static synchronized ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    // Devuelve la conexión abierta; si no existe o se cayó, la (re)abre.
    public synchronized Connection getConexion() throws PersistenciaException {
        try {
            if (conexion == null || !conexion.isValid(SEGUNDOS_VALIDACION)) {
                if (url == null) {
                    leerConfiguracion();
                }
                conexion = DriverManager.getConnection(url, usuario, clave);
            }
            return conexion;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo conectar a la base de datos (" + url + "). "
                    + "Verifique que MySQL esté iniciado y que el usuario y la clave de "
                    + ARCHIVO_CONFIGURACION + " sean correctos.", e);
        }
    }

    // Lee url, usuario y clave del archivo de configuración.
    private void leerConfiguracion() throws PersistenciaException {
        Properties propiedades = new Properties();
        try (InputStream entrada = new FileInputStream(ARCHIVO_CONFIGURACION)) {
            propiedades.load(entrada);
        } catch (IOException e) {
            throw new PersistenciaException("No se encontró el archivo " + ARCHIVO_CONFIGURACION
                    + ". Copie config/db.properties.ejemplo con ese nombre y complete la clave.", e);
        }
        url = propiedades.getProperty("url", "jdbc:mysql://localhost:3306/sigdeh?useSSL=false&allowPublicKeyRetrieval=true");
        usuario = propiedades.getProperty("usuario", "root");
        clave = propiedades.getProperty("clave", "");
    }

    // Cierra la conexión al salir del sistema. Un error al cerrar no impide terminar.
    public synchronized void cerrar() {
        if (conexion == null) {
            return;
        }
        try {
            conexion.close();
        } catch (SQLException e) {
            System.out.println("Aviso: no se pudo cerrar la conexión (" + e.getMessage() + ").");
        } finally {
            conexion = null;
        }
    }
}
