package com.imvh.sigdeh.gestor;

import com.imvh.sigdeh.dao.EsquemaPriorizacionDAO;
import com.imvh.sigdeh.dao.ListadoPrioridadDAO;
import com.imvh.sigdeh.dao.OperatoriaDAO;
import com.imvh.sigdeh.dao.SolicitudDAO;
import com.imvh.sigdeh.excepciones.DatosInvalidosException;
import com.imvh.sigdeh.excepciones.EntidadNoEncontradaException;
import com.imvh.sigdeh.excepciones.SigdehException;
import com.imvh.sigdeh.modelo.ListadoPrioridad;
import com.imvh.sigdeh.modelo.Operatoria;
import com.imvh.sigdeh.modelo.Posicion;
import com.imvh.sigdeh.modelo.Solicitud;
import com.imvh.sigdeh.modelo.Universo;
import com.imvh.sigdeh.priorizacion.EsquemaPriorizacion;
import com.imvh.sigdeh.util.Ordenador;
import com.imvh.sigdeh.util.ParametrosNormativos;

import java.util.ArrayList;
import java.util.List;

// Reglas de negocio del listado de prioridad: CU006 y CU007.
public class GestorDePriorizacion {

    private final OperatoriaDAO operatoriaDAO;
    private final SolicitudDAO solicitudDAO;
    private final EsquemaPriorizacionDAO esquemaDAO;
    private final ListadoPrioridadDAO listadoDAO;

    public GestorDePriorizacion(OperatoriaDAO operatoriaDAO, SolicitudDAO solicitudDAO,
                                EsquemaPriorizacionDAO esquemaDAO, ListadoPrioridadDAO listadoDAO) {

        this.operatoriaDAO = operatoriaDAO;
        this.solicitudDAO = solicitudDAO;
        this.esquemaDAO = esquemaDAO;
        this.listadoDAO = listadoDAO;

    }

    // Paso 2 del CU006: operatorias entre las que puede elegir el director.
    public List<Operatoria> listarOperatoriasAbiertas() {

        return operatoriaDAO.buscarAbiertas();

    }

    // CU006 Emitir listado de prioridad: sigue los pasos de la ficha y devuelve el listado ordenado.
    public ListadoPrioridad emitirListado(int idOperatoria) throws SigdehException {

        // Pasos 2-3: operatoria elegida, esquema vigente y verificación de precondiciones
        Operatoria operatoria = operatoriaDAO.buscarPorId(idOperatoria);
        if (operatoria == null) {
            throw new EntidadNoEncontradaException("una operatoria", idOperatoria);
        }
        if (!operatoria.estaAbierta()) {
            throw new DatosInvalidosException("La operatoria no está abierta.");
        }
        if (operatoria.getUniversos().isEmpty()) {
            throw new DatosInvalidosException("La operatoria no tiene universos definidos.");
        }
        EsquemaPriorizacion esquema = esquemaDAO.buscarVigente();
        if (esquema == null) {
            throw new DatosInvalidosException("No hay un esquema de priorización vigente.");
        }
        double desvio = Math.abs(esquema.getSumaPesos() - 1.0);
        if (desvio > ParametrosNormativos.TOLERANCIA_SUMA_PESOS) {
            throw new DatosInvalidosException("Los pesos del esquema vigente deben sumar 1,00.");
        }

        // Pasos 4-5: solicitudes elegibles y cálculo del puntaje de cada una (CU007)
        List<Posicion> posiciones = new ArrayList<>();
        for (Solicitud solicitud : solicitudesElegibles()) {
            Universo universo = asignarUniverso(operatoria, solicitud);
            if (universo == null) {
                continue;                             // ningún universo la admite: queda fuera del listado
            }
            double puntaje = esquema.calcularPuntaje(solicitud);
            solicitud.setUltimoPuntaje(puntaje);      // para poder consultarlo después (RF16)
            posiciones.add(new Posicion(solicitud, universo, puntaje));
        }

        // Paso 6: ordenar cada universo por puntaje descendente y numerar el orden.
        // Las posiciones ya ordenadas se acumulan en "posicionesOrdenadas", que es la lista que se guarda.
        List<Posicion> posicionesOrdenadas = new ArrayList<>();
        for (Universo universo : operatoria.getUniversos()) {
            List<Posicion> delUniverso = new ArrayList<>();
            for (Posicion posicion : posiciones) {
                if (posicion.getUniverso().getId() == universo.getId()) {
                    delUniverso.add(posicion);
                }
            }
            Ordenador.ordenarPorInsercion(delUniverso, Posicion.POR_PRIORIDAD);
            for (int i = 0; i < delUniverso.size(); i++) {
                delUniverso.get(i).setOrden(i + 1);
            }
            posicionesOrdenadas.addAll(delUniverso);
        }

        // Paso 7: registrar el listado con la versión del esquema aplicada
        ListadoPrioridad listado = new ListadoPrioridad(operatoria, esquema.getVersion(), posicionesOrdenadas);
        listadoDAO.guardar(listado);
        return listado;

    }

    // Paso 4: solicitudes activas con la residencia mínima exigida para participar (RF08).
    private List<Solicitud> solicitudesElegibles() {

        List<Solicitud> elegibles = new ArrayList<>();
        for (Solicitud solicitud : solicitudDAO.buscarTodas()) {
            boolean activa = solicitud.estaActiva();
            boolean residenciaSuficiente = solicitud.getGrupoFamiliar().aniosDeResidencia()
                    >= ParametrosNormativos.ANIOS_RESIDENCIA_LISTADO;
            if (activa && residenciaSuficiente) {
                elegibles.add(solicitud);
            }
        }
        return elegibles;

    }

    // Paso 6: prefiere el universo específico (discapacidad) y, si no corresponde, el general.
    private Universo asignarUniverso(Operatoria operatoria, Solicitud solicitud) {

        Universo elegido = null;
        for (Universo universo : operatoria.getUniversos()) {
            if (universo.admite(solicitud)) {
                if (elegido == null || universo.requiereDiscapacidad()) {
                    elegido = universo;
                }
            }
        }
        return elegido;

    }

}
