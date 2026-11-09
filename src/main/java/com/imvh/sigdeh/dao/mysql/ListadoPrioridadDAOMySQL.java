package com.imvh.sigdeh.dao.mysql;

import com.imvh.sigdeh.dao.ListadoPrioridadDAO;
import com.imvh.sigdeh.excepciones.PersistenciaException;
import com.imvh.sigdeh.modelo.ListadoPrioridad;
import com.imvh.sigdeh.modelo.Posicion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

// Persistencia de listados de prioridad y sus posiciones (tablas listados_prioridad y posiciones).
// El tipo del DAO es String[] porque lo único que se lee de vuelta es el historial,
// donde cada fila es un arreglo de columnas para mostrar.
public class ListadoPrioridadDAOMySQL extends DAOBaseMySQL<String[]> implements ListadoPrioridadDAO {

    // La tabla guarda la clave del esquema (esquema_id); el modelo conoce su versión,
    // así que la clave se busca con una subconsulta por versión.
    private static final String INSERTAR =
            "INSERT INTO listados_prioridad (operatoria_id, esquema_id, fecha_emision) "
                    + "VALUES (?, (SELECT esquema_id FROM esquemas_priorizacion WHERE version = ?), ?)";
    private static final String INSERTAR_POSICION =
            "INSERT INTO posiciones (listado_id, solicitud_id, universo_id, puntaje, orden) VALUES (?, ?, ?, ?, ?)";
    private static final String HISTORIAL =
            "SELECT l.listado_id, DATE_FORMAT(l.fecha_emision, '%d/%m/%Y %H:%i') AS emitido, "
                    + "o.tipo_solucion, e.version, COUNT(p.posicion_id) AS cantidad "
                    + "FROM listados_prioridad l "
                    + "JOIN operatorias o ON o.operatoria_id = l.operatoria_id "
                    + "JOIN esquemas_priorizacion e ON e.esquema_id = l.esquema_id "
                    + "LEFT JOIN posiciones p ON p.listado_id = l.listado_id "
                    + "GROUP BY l.listado_id, l.fecha_emision, o.tipo_solucion, e.version "
                    + "ORDER BY l.listado_id DESC";

    // Guarda el listado y todas sus posiciones en una transacción.
    // Las posiciones se envían juntas en un lote (batch) en lugar de una sentencia por vez.
    @Override
    public void guardar(ListadoPrioridad listado) throws PersistenciaException {
        enTransaccion("guardar el listado de prioridad", () -> {
            int id = insertar(INSERTAR, listado.getOperatoria().getId(), listado.getVersionEsquema(),
                    listado.getFechaEmision());
            try (PreparedStatement sentencia = conexion().prepareStatement(INSERTAR_POSICION)) {
                for (Posicion posicion : listado.getPosiciones()) {
                    sentencia.setInt(1, id);
                    sentencia.setInt(2, posicion.getSolicitud().getNumero());
                    sentencia.setInt(3, posicion.getUniverso().getId());
                    sentencia.setDouble(4, posicion.getPuntaje());
                    sentencia.setInt(5, posicion.getOrden());
                    sentencia.addBatch();
                }
                sentencia.executeBatch();
            }
            listado.setId(id);
            return id;
        });
    }

    @Override
    public List<String[]> buscarHistorial() throws PersistenciaException {
        return consultar(HISTORIAL);
    }

    @Override
    protected String[] mapear(ResultSet fila) throws SQLException {
        return new String[]{
                fila.getString("listado_id"),
                fila.getString("emitido"),
                fila.getString("tipo_solucion"),
                fila.getString("version"),
                fila.getString("cantidad")
        };
    }
}
