package com.imvh.sigdeh.dao.mysql;

import com.imvh.sigdeh.dao.GrupoFamiliarDAO;
import com.imvh.sigdeh.excepciones.PersistenciaException;
import com.imvh.sigdeh.modelo.GrupoFamiliar;
import com.imvh.sigdeh.modelo.Integrante;

import java.sql.ResultSet;
import java.sql.SQLException;

// Persistencia de grupos familiares y sus integrantes (tablas grupos_familiares e integrantes).
public class GrupoFamiliarDAOMySQL extends DAOBaseMySQL<GrupoFamiliar> implements GrupoFamiliarDAO {

    private static final String INSERTAR_GRUPO =
            "INSERT INTO grupos_familiares (situacion_habitacional, ingreso_neto, fecha_inicio_residencia) "
                    + "VALUES (?, ?, ?)";
    private static final String INSERTAR_INTEGRANTE =
            "INSERT INTO integrantes (grupo_id, dni, apellido_nombre, fecha_nacimiento, vinculo, discapacidad) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String ACTUALIZAR_GRUPO =
            "UPDATE grupos_familiares SET situacion_habitacional = ?, ingreso_neto = ? WHERE grupo_id = ?";
    private static final String INTEGRANTES_DEL_GRUPO =
            "SELECT dni, apellido_nombre, fecha_nacimiento, vinculo, discapacidad "
                    + "FROM integrantes WHERE grupo_id = ? ORDER BY integrante_id";

    // Guarda el grupo y todos sus integrantes en una sola transacción:
    // si falla un integrante (por ejemplo, un DNI repetido), no queda un grupo a medio cargar.
    @Override
    public void guardar(GrupoFamiliar grupo) throws PersistenciaException {
        enTransaccion("registrar el grupo familiar", () -> {
            int id = insertar(INSERTAR_GRUPO,
                    grupo.getSituacionHabitacional(), grupo.getIngresoNeto(), grupo.getFechaInicioResidencia());
            for (Integrante integrante : grupo.getIntegrantes()) {
                insertar(INSERTAR_INTEGRANTE, id, integrante.getDni(), integrante.getApellidoNombre(),
                        integrante.getFechaNacimiento(), integrante.getVinculo(), integrante.tieneDiscapacidad());
            }
            grupo.setIdGrupoFamiliar(id);            // recién se asigna cuando todo salió bien
            return id;
        });
    }

    // Actualiza los datos variables del grupo (CU002). Lo usa SolicitudDAOMySQL dentro de su transacción.
    void actualizarDatos(GrupoFamiliar grupo) throws PersistenciaException {
        actualizar(ACTUALIZAR_GRUPO, grupo.getSituacionHabitacional(), grupo.getIngresoNeto(),
                grupo.getIdGrupoFamiliar());
    }

    // Completa el grupo con sus integrantes (el primero cargado es el titular).
    void cargarIntegrantes(GrupoFamiliar grupo) throws PersistenciaException {
        for (Integrante integrante : consultar(INTEGRANTES_DEL_GRUPO, this::mapearIntegrante,
                grupo.getIdGrupoFamiliar())) {
            grupo.agregarIntegrante(integrante);
        }
    }

    // Arma el grupo a partir de las columnas de grupos_familiares (sin integrantes).
    @Override
    protected GrupoFamiliar mapear(ResultSet fila) throws SQLException {
        GrupoFamiliar grupo = new GrupoFamiliar(
                fila.getString("situacion_habitacional"),
                fila.getDouble("ingreso_neto"),
                leerFecha(fila, "fecha_inicio_residencia"));
        grupo.setIdGrupoFamiliar(fila.getInt("grupo_id"));
        return grupo;
    }

    private Integrante mapearIntegrante(ResultSet fila) throws SQLException {
        return new Integrante(
                fila.getString("dni"),
                fila.getString("apellido_nombre"),
                leerFecha(fila, "fecha_nacimiento"),
                fila.getString("vinculo"),
                fila.getBoolean("discapacidad"));
    }
}
