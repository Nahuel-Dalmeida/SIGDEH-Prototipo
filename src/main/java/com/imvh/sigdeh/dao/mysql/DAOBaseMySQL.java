package com.imvh.sigdeh.dao.mysql;

import com.imvh.sigdeh.excepciones.PersistenciaException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Base común de todos los DAO de MySQL (clase abstracta).
// Concentra lo repetitivo de JDBC: preparar la sentencia, cargar los parámetros,
// recorrer el ResultSet, cerrar recursos y traducir las SQLException.
// Cada DAO concreto solo escribe su SQL y cómo convertir una fila en un objeto (mapear).
public abstract class DAOBaseMySQL<T> {

    // Convierte una fila del ResultSet en un objeto del dominio.
    @FunctionalInterface
    protected interface MapeadorFila<R> {
        R mapear(ResultSet fila) throws SQLException;
    }

    // Trabajo que debe ejecutarse completo o no ejecutarse (transacción).
    @FunctionalInterface
    protected interface OperacionTransaccional<R> {
        R ejecutar() throws SQLException, PersistenciaException;
    }

    // Cada DAO concreto define cómo se arma su objeto a partir de una fila.
    protected abstract T mapear(ResultSet fila) throws SQLException;

    protected Connection conexion() throws PersistenciaException {
        return ConexionBD.getInstancia().getConexion();
    }

    // SELECT que devuelve una lista de objetos del tipo del DAO.
    protected List<T> consultar(String sql, Object... parametros) throws PersistenciaException {
        return consultar(sql, this::mapear, parametros);
    }

    // SELECT con un mapeador propio (para tablas hijas, por ejemplo los integrantes de un grupo).
    protected <R> List<R> consultar(String sql, MapeadorFila<R> mapeador, Object... parametros)
            throws PersistenciaException {
        List<R> resultado = new ArrayList<>();
        try (PreparedStatement sentencia = conexion().prepareStatement(sql)) {
            asignarParametros(sentencia, parametros);
            try (ResultSet filas = sentencia.executeQuery()) {
                while (filas.next()) {
                    resultado.add(mapeador.mapear(filas));
                }
            }
        } catch (SQLException e) {
            throw PersistenciaException.desde("consultar los datos", e);
        }
        return resultado;
    }

    // SELECT que espera una sola fila. Devuelve null si no hay resultados.
    protected T consultarUno(String sql, Object... parametros) throws PersistenciaException {
        List<T> resultado = consultar(sql, parametros);
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    // INSERT con clave AUTO_INCREMENT: devuelve el id generado por MySQL.
    protected int insertar(String sql, Object... parametros) throws PersistenciaException {
        try (PreparedStatement sentencia = conexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarParametros(sentencia, parametros);
            sentencia.executeUpdate();
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) {
                    return claves.getInt(1);
                }
                throw new PersistenciaException("MySQL no devolvió el identificador generado.");
            }
        } catch (SQLException e) {
            throw PersistenciaException.desde("guardar el registro", e);
        }
    }

    // UPDATE o DELETE: devuelve la cantidad de filas afectadas.
    protected int actualizar(String sql, Object... parametros) throws PersistenciaException {
        try (PreparedStatement sentencia = conexion().prepareStatement(sql)) {
            asignarParametros(sentencia, parametros);
            return sentencia.executeUpdate();
        } catch (SQLException e) {
            throw PersistenciaException.desde("actualizar el registro", e);
        }
    }

    // Ejecuta varias sentencias como una unidad: si una falla, se deshacen todas (rollback).
    protected <R> R enTransaccion(String accion, OperacionTransaccional<R> operacion)
            throws PersistenciaException {
        Connection conexion = conexion();
        try {
            conexion.setAutoCommit(false);
            R resultado = operacion.ejecutar();
            conexion.commit();
            return resultado;
        } catch (SQLException | PersistenciaException e) {
            deshacer(conexion);
            if (e instanceof PersistenciaException) {
                throw (PersistenciaException) e;
            }
            throw PersistenciaException.desde(accion, (SQLException) e);
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Aviso: no se pudo restablecer el modo autocommit.");
            }
        }
    }

    private void deshacer(Connection conexion) {
        try {
            conexion.rollback();
        } catch (SQLException e) {
            System.out.println("Aviso: no se pudo deshacer la transacción (" + e.getMessage() + ").");
        }
    }

    // Carga los parámetros (un arreglo de tamaño fijo) en los "?" de la sentencia, en orden.
    // Convierte los tipos de Java que JDBC no acepta directamente.
    private void asignarParametros(PreparedStatement sentencia, Object[] parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            Object valor = parametros[i];
            if (valor instanceof LocalDate) {
                valor = Date.valueOf((LocalDate) valor);
            } else if (valor instanceof LocalDateTime) {
                valor = Timestamp.valueOf((LocalDateTime) valor);
            } else if (valor instanceof Enum) {
                valor = ((Enum<?>) valor).name();
            }
            sentencia.setObject(i + 1, valor);            // los parámetros JDBC empiezan en 1
        }
    }

    // Lee una columna DATE como LocalDate (o null).
    protected static LocalDate leerFecha(ResultSet fila, String columna) throws SQLException {
        Date fecha = fila.getDate(columna);
        return fecha == null ? null : fecha.toLocalDate();
    }
}
