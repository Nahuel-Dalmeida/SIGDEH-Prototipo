package com.imvh.sigdeh.dao.mysql;

import com.imvh.sigdeh.dao.SolicitudDAO;
import com.imvh.sigdeh.excepciones.PersistenciaException;
import com.imvh.sigdeh.modelo.EstadoSolicitud;
import com.imvh.sigdeh.modelo.GrupoFamiliar;
import com.imvh.sigdeh.modelo.Solicitud;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

// Persistencia de solicitudes (tabla solicitudes, unida con grupos_familiares).
public class SolicitudDAOMySQL extends DAOBaseMySQL<Solicitud> implements SolicitudDAO {

    // Consulta base: solicitud + grupo familiar + último puntaje.
    // El último puntaje no es una columna: se toma de la posición del listado más reciente.
    private static final String SELECT_BASE =
            "SELECT s.solicitud_id, s.fecha_alta, s.fecha_ultima_actualizacion, s.estado, "
                    + "g.grupo_id, g.situacion_habitacional, g.ingreso_neto, g.fecha_inicio_residencia, "
                    + "(SELECT p.puntaje FROM posiciones p "
                    + "   JOIN listados_prioridad l ON l.listado_id = p.listado_id "
                    + "  WHERE p.solicitud_id = s.solicitud_id "
                    + "  ORDER BY l.fecha_emision DESC, l.listado_id DESC LIMIT 1) AS ultimo_puntaje "
                    + "FROM solicitudes s JOIN grupos_familiares g ON g.grupo_id = s.grupo_id ";

    private static final String POR_NUMERO = SELECT_BASE + "WHERE s.solicitud_id = ?";
    private static final String POR_DNI = SELECT_BASE
            + "WHERE s.grupo_id = (SELECT i.grupo_id FROM integrantes i WHERE i.dni = ?)";
    private static final String TODAS = SELECT_BASE + "ORDER BY s.solicitud_id";

    private static final String INSERTAR =
            "INSERT INTO solicitudes (grupo_id, fecha_alta, fecha_ultima_actualizacion, estado) "
                    + "VALUES (?, ?, ?, ?)";
    private static final String ACTUALIZAR =
            "UPDATE solicitudes SET fecha_ultima_actualizacion = ?, estado = ? WHERE solicitud_id = ?";

    // Borrado en el orden que exigen las claves foráneas: primero las tablas dependientes (TP2).
    // Es un arreglo porque los pasos son fijos y se ejecutan siempre en este orden.
    private static final String[] BORRADO_EN_ORDEN = {
            "DELETE FROM posiciones WHERE solicitud_id = ?",
            "DELETE FROM solicitudes WHERE solicitud_id = ?",
            "DELETE FROM integrantes WHERE grupo_id = ?",
            "DELETE FROM grupos_familiares WHERE grupo_id = ?"
    };

    private final GrupoFamiliarDAOMySQL grupoDAO = new GrupoFamiliarDAOMySQL();

    // Número 0 = solicitud nueva (INSERT); si ya tiene número, se actualiza junto con su grupo (UPDATE).
    @Override
    public void guardar(Solicitud solicitud) throws PersistenciaException {
        if (solicitud.getNumero() == 0) {
            int numero = insertar(INSERTAR, solicitud.getGrupoFamiliar().getIdGrupoFamiliar(),
                    solicitud.getFechaAlta(), solicitud.getFechaUltimaActualizacion(), solicitud.getEstado());
            solicitud.setNumero(numero);
            return;
        }
        enTransaccion("actualizar la solicitud", () -> {
            grupoDAO.actualizarDatos(solicitud.getGrupoFamiliar());
            int filas = actualizar(ACTUALIZAR, solicitud.getFechaUltimaActualizacion(),
                    solicitud.getEstado(), solicitud.getNumero());
            if (filas == 0) {
                throw new PersistenciaException("La solicitud N° " + solicitud.getNumero() + " ya no existe.");
            }
            return filas;
        });
    }

    @Override
    public Solicitud buscarPorId(int numero) throws PersistenciaException {
        return completar(consultarUno(POR_NUMERO, numero));
    }

    @Override
    public Solicitud buscarPorDni(String dni) throws PersistenciaException {
        return completar(consultarUno(POR_DNI, dni));
    }

    @Override
    public List<Solicitud> buscarTodas() throws PersistenciaException {
        List<Solicitud> solicitudes = consultar(TODAS);
        for (Solicitud solicitud : solicitudes) {
            completar(solicitud);
        }
        return solicitudes;
    }

    // Las dos primeras sentencias usan el número de solicitud y las dos últimas el id del grupo.
    // Todo dentro de una transacción: o se borra la solicitud completa o no se borra nada.
    @Override
    public void eliminar(Solicitud solicitud) throws PersistenciaException {
        int[] claves = {solicitud.getNumero(), solicitud.getNumero(),
                solicitud.getGrupoFamiliar().getIdGrupoFamiliar(), solicitud.getGrupoFamiliar().getIdGrupoFamiliar()};
        enTransaccion("eliminar la solicitud", () -> {
            int filas = 0;
            for (int i = 0; i < BORRADO_EN_ORDEN.length; i++) {
                filas += actualizar(BORRADO_EN_ORDEN[i], claves[i]);
            }
            return filas;
        });
    }

    // Agrega los integrantes al grupo de la solicitud (se leen de otra tabla).
    private Solicitud completar(Solicitud solicitud) throws PersistenciaException {
        if (solicitud != null) {
            grupoDAO.cargarIntegrantes(solicitud.getGrupoFamiliar());
        }
        return solicitud;
    }

    @Override
    protected Solicitud mapear(ResultSet fila) throws SQLException {
        GrupoFamiliar grupo = grupoDAO.mapear(fila);     // mismas columnas de grupos_familiares
        Solicitud solicitud = new Solicitud(grupo,
                leerFecha(fila, "fecha_alta"),
                leerFecha(fila, "fecha_ultima_actualizacion"),
                EstadoSolicitud.valueOf(fila.getString("estado").toUpperCase()));
        solicitud.setNumero(fila.getInt("solicitud_id"));
        double puntaje = fila.getDouble("ultimo_puntaje");
        if (!fila.wasNull()) {
            solicitud.setUltimoPuntaje(puntaje);
        }
        return solicitud;
    }
}
