package com.imvh.sigdeh.dao.mysql;

import com.imvh.sigdeh.dao.OperatoriaDAO;
import com.imvh.sigdeh.excepciones.PersistenciaException;
import com.imvh.sigdeh.modelo.EstadoOperatoria;
import com.imvh.sigdeh.modelo.Operatoria;
import com.imvh.sigdeh.modelo.Universo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

// Persistencia de operatorias y sus universos (tablas operatorias y universos).
public class OperatoriaDAOMySQL extends DAOBaseMySQL<Operatoria> implements OperatoriaDAO {

    private static final String SELECT_BASE =
            "SELECT operatoria_id, tipo_solucion, cantidad_disponible, fecha_convocatoria, estado FROM operatorias ";
    private static final String POR_ID = SELECT_BASE + "WHERE operatoria_id = ?";
    private static final String ABIERTAS = SELECT_BASE + "WHERE estado = 'ABIERTA' ORDER BY operatoria_id";
    private static final String UNIVERSOS =
            "SELECT universo_id, nombre, cupo, requiere_discapacidad FROM universos "
                    + "WHERE operatoria_id = ? ORDER BY universo_id";
    private static final String INSERTAR =
            "INSERT INTO operatorias (tipo_solucion, cantidad_disponible, fecha_convocatoria, estado) VALUES (?, ?, ?, ?)";
    private static final String INSERTAR_UNIVERSO =
            "INSERT INTO universos (operatoria_id, nombre, cupo, requiere_discapacidad) VALUES (?, ?, ?, ?)";

    // Guarda la operatoria con todos sus universos en una transacción.
    @Override
    public void guardar(Operatoria operatoria) throws PersistenciaException {
        enTransaccion("registrar la operatoria", () -> {
            int id = insertar(INSERTAR, operatoria.getTipoSolucion(), operatoria.getCantidadDisponible(),
                    operatoria.getFechaConvocatoria(), operatoria.getEstado());
            for (Universo universo : operatoria.getUniversos()) {
                universo.setId(insertar(INSERTAR_UNIVERSO, id, universo.getNombre(), universo.getCupo(),
                        universo.requiereDiscapacidad()));
            }
            operatoria.setId(id);
            return id;
        });
    }

    @Override
    public Operatoria buscarPorId(int id) throws PersistenciaException {
        Operatoria operatoria = consultarUno(POR_ID, id);
        if (operatoria != null) {
            cargarUniversos(operatoria);
        }
        return operatoria;
    }

    @Override
    public List<Operatoria> buscarAbiertas() throws PersistenciaException {
        List<Operatoria> abiertas = consultar(ABIERTAS);
        for (Operatoria operatoria : abiertas) {
            cargarUniversos(operatoria);
        }
        return abiertas;
    }

    private void cargarUniversos(Operatoria operatoria) throws PersistenciaException {
        for (Universo universo : consultar(UNIVERSOS, this::mapearUniverso, operatoria.getId())) {
            operatoria.agregarUniverso(universo);
        }
    }

    @Override
    protected Operatoria mapear(ResultSet fila) throws SQLException {
        Operatoria operatoria = new Operatoria(
                fila.getString("tipo_solucion"),
                fila.getInt("cantidad_disponible"),
                leerFecha(fila, "fecha_convocatoria"),
                EstadoOperatoria.valueOf(fila.getString("estado").toUpperCase()));
        operatoria.setId(fila.getInt("operatoria_id"));
        return operatoria;
    }

    private Universo mapearUniverso(ResultSet fila) throws SQLException {
        Universo universo = new Universo(
                fila.getString("nombre"),
                fila.getInt("cupo"),
                fila.getBoolean("requiere_discapacidad"));
        universo.setId(fila.getInt("universo_id"));
        return universo;
    }
}
