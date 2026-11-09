package com.imvh.sigdeh.excepciones;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

// Error al conectarse o al operar con la base de datos MySQL.
// Envuelve la SQLException original (causa) y le da al usuario un mensaje entendible.
public class PersistenciaException extends SigdehException {

    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensaje) {
        super(mensaje);
    }

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    // Traduce una SQLException según su código SQLState (estándar de JDBC).
    public static PersistenciaException desde(String accion, SQLException e) {
        String estado = e.getSQLState() == null ? "" : e.getSQLState();
        String mensaje;
        if (e instanceof SQLIntegrityConstraintViolationException || estado.startsWith("23")) {
            mensaje = "No se pudo " + accion + ": el dato ya existe o hace referencia a un registro inexistente.";
        } else if (estado.startsWith("08")) {
            mensaje = "No se pudo " + accion + ": se perdió la conexión con la base de datos.";
        } else {
            mensaje = "No se pudo " + accion + " en la base de datos (" + e.getMessage() + ").";
        }
        return new PersistenciaException(mensaje, e);
    }
}
