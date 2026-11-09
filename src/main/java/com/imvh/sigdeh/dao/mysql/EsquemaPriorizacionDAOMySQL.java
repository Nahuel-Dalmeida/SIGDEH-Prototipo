package com.imvh.sigdeh.dao.mysql;

import com.imvh.sigdeh.dao.EsquemaPriorizacionDAO;
import com.imvh.sigdeh.excepciones.DatosInvalidosException;
import com.imvh.sigdeh.excepciones.PersistenciaException;
import com.imvh.sigdeh.priorizacion.Criterio;
import com.imvh.sigdeh.priorizacion.EsquemaPriorizacion;
import com.imvh.sigdeh.priorizacion.FabricaDeCriterios;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

// Persistencia de esquemas de priorización y sus criterios (tablas esquemas_priorizacion y criterios).
public class EsquemaPriorizacionDAOMySQL extends DAOBaseMySQL<EsquemaPriorizacion>
        implements EsquemaPriorizacionDAO {

    // Vigente = el de fecha de vigencia más reciente que ya empezó a regir.
    private static final String VIGENTE =
            "SELECT version, fecha_vigencia FROM esquemas_priorizacion "
                    + "WHERE fecha_vigencia <= CURDATE() ORDER BY fecha_vigencia DESC, version DESC LIMIT 1";
    private static final String CRITERIOS =
            "SELECT c.nombre, c.peso, c.estrategia FROM criterios c "
                    + "JOIN esquemas_priorizacion e ON e.esquema_id = c.esquema_id "
                    + "WHERE e.version = ? ORDER BY c.criterio_id";
    private static final String INSERTAR =
            "INSERT INTO esquemas_priorizacion (version, fecha_vigencia) VALUES (?, ?)";
    private static final String INSERTAR_CRITERIO =
            "INSERT INTO criterios (esquema_id, nombre, peso, estrategia) VALUES (?, ?, ?, ?)";

    // Índices del arreglo con que se lee cada fila de criterio.
    private static final int NOMBRE = 0;
    private static final int PESO = 1;
    private static final int ESTRATEGIA = 2;

    @Override
    public void guardar(EsquemaPriorizacion esquema) throws PersistenciaException {
        enTransaccion("registrar el esquema de priorización", () -> {
            int id = insertar(INSERTAR, esquema.getVersion(), esquema.getFechaVigencia());
            for (Criterio criterio : esquema.getCriterios()) {
                insertar(INSERTAR_CRITERIO, id, criterio.getNombre(), criterio.getPeso(),
                        criterio.getEstrategia().getCodigo());
            }
            return id;
        });
    }

    @Override
    public EsquemaPriorizacion buscarVigente() throws PersistenciaException {
        EsquemaPriorizacion esquema = consultarUno(VIGENTE);
        if (esquema == null) {
            return null;
        }
        // Cada criterio se lee como un arreglo de 3 textos y después se arma el objeto,
        // porque crear la estrategia puede fallar con una excepción que no es de SQL.
        List<String[]> filas = consultar(CRITERIOS,
                fila -> new String[]{fila.getString("nombre"), fila.getString("peso"), fila.getString("estrategia")},
                esquema.getVersion());
        try {
            for (String[] columnas : filas) {
                esquema.agregarCriterio(new Criterio(columnas[NOMBRE], Double.parseDouble(columnas[PESO]),
                        FabricaDeCriterios.crear(columnas[ESTRATEGIA])));
            }
        } catch (DatosInvalidosException e) {
            throw new PersistenciaException("El esquema vigente de la base es inconsistente: " + e.getMessage(), e);
        }
        return esquema;
    }

    @Override
    protected EsquemaPriorizacion mapear(ResultSet fila) throws SQLException {
        return new EsquemaPriorizacion(fila.getInt("version"), leerFecha(fila, "fecha_vigencia"));
    }
}
