package com.imvh.sigdeh.gestor;

import com.imvh.sigdeh.dao.GrupoFamiliarDAO;
import com.imvh.sigdeh.dao.SolicitudDAO;
import com.imvh.sigdeh.excepciones.DatosInvalidosException;
import com.imvh.sigdeh.excepciones.DniDuplicadoException;
import com.imvh.sigdeh.excepciones.EntidadNoEncontradaException;
import com.imvh.sigdeh.excepciones.ResidenciaInsuficienteException;
import com.imvh.sigdeh.excepciones.SigdehException;
import com.imvh.sigdeh.modelo.GrupoFamiliar;
import com.imvh.sigdeh.modelo.Integrante;
import com.imvh.sigdeh.modelo.Solicitud;
import com.imvh.sigdeh.util.ParametrosNormativos;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Reglas de negocio del registro de solicitudes: CU001, CU002 y CU003.
public class GestorDeSolicitudes {

    private final SolicitudDAO solicitudDAO;
    private final GrupoFamiliarDAO grupoFamiliarDAO;

    public GestorDeSolicitudes(SolicitudDAO solicitudDAO, GrupoFamiliarDAO grupoFamiliarDAO) {

        this.solicitudDAO = solicitudDAO;
        this.grupoFamiliarDAO = grupoFamiliarDAO;

    }

    // CU001 Registrar solicitud: valida todo y recién después guarda.
    public int registrarSolicitud(GrupoFamiliar grupo) throws SigdehException {

        validarDatos(grupo);
        validarResidenciaMinima(grupo.getFechaInicioResidencia());
        validarDnisNoDuplicados(grupo);

        grupoFamiliarDAO.guardar(grupo);
        Solicitud solicitud = new Solicitud(grupo);   // nace ACTIVA, con fecha de hoy
        solicitudDAO.guardar(solicitud);
        return solicitud.getNumero();

    }

    // CU002 Actualizar solicitud: modifica los datos variables del grupo y renueva la fecha de actualización.
    public void actualizarSolicitud(int numero, String nuevaSituacion, double nuevoIngreso)
            throws SigdehException {

        if (nuevaSituacion == null || nuevaSituacion.trim().isEmpty()) {
            throw new DatosInvalidosException("La situación habitacional no puede estar vacía.");
        }
        if (nuevoIngreso < 0) {
            throw new DatosInvalidosException("El ingreso neto no puede ser negativo.");
        }
        Solicitud solicitud = consultarEstado(numero);
        solicitud.getGrupoFamiliar().setSituacionHabitacional(nuevaSituacion.trim());
        solicitud.getGrupoFamiliar().setIngresoNeto(nuevoIngreso);
        solicitud.actualizar();                       // fecha de última actualización = hoy
        solicitudDAO.guardar(solicitud);

    }

    // CU003 Consultar estado de solicitud.
    public Solicitud consultarEstado(int numero) throws EntidadNoEncontradaException {

        Solicitud solicitud = solicitudDAO.buscarPorId(numero);
        if (solicitud == null) {
            throw new EntidadNoEncontradaException("una solicitud", numero);
        }
        return solicitud;

    }

    // Busca la solicitud en la que figura un DNI. Devuelve null si no figura en ninguna.
    public Solicitud buscarPorDni(String dni) {

        return solicitudDAO.buscarPorDni(dni);

    }

    // Devuelve todas las solicitudes registradas.
    public List<Solicitud> listarSolicitudes() {

        return solicitudDAO.buscarTodas();

    }

    // Indica si la solicitud superó el período de vigencia configurado (RF04).
    public boolean estaDesactualizada(Solicitud solicitud) {

        return solicitud.necesitaActualizacion(ParametrosNormativos.MESES_VIGENCIA_SOLICITUD);

    }

    // ------------------------------------------------------------------
    // Validaciones
    // ------------------------------------------------------------------

    // RF02: residencia mínima de un año, tomando la fecha de hoy como referencia.
    public void validarResidenciaMinima(LocalDate fechaInicioResidencia)
            throws DatosInvalidosException, ResidenciaInsuficienteException {

        validarResidenciaMinima(fechaInicioResidencia, LocalDate.now());

    }

    // Misma validación, con fecha de referencia explícita (permite probar los valores frontera).
    public void validarResidenciaMinima(LocalDate fechaInicioResidencia, LocalDate fechaReferencia)
            throws DatosInvalidosException, ResidenciaInsuficienteException {

        if (fechaInicioResidencia == null || fechaInicioResidencia.isAfter(fechaReferencia)) {
            throw new DatosInvalidosException("Fecha de inicio de residencia inválida.");   // CEI1 (m < 0)
        }
        long meses = ChronoUnit.MONTHS.between(fechaInicioResidencia, fechaReferencia);
        if (meses < ParametrosNormativos.MESES_RESIDENCIA_INSCRIPCION) {
            throw new ResidenciaInsuficienteException(meses);                               // CEV1 (0 a 11)
        }
        if (meses > ParametrosNormativos.MESES_RESIDENCIA_MAXIMA) {
            throw new DatosInvalidosException(
                    "Antigüedad de residencia fuera de rango, verifique la fecha ingresada."); // CEI2 (> 1200)
        }
        // 12 a 1200 meses: CEV2, la solicitud puede continuar

    }

    // RF03: ningún DNI puede repetirse dentro del grupo ni figurar en otra solicitud.
    private void validarDnisNoDuplicados(GrupoFamiliar grupo) throws DniDuplicadoException {

        Set<String> dnisDelGrupo = new HashSet<>();
        for (Integrante integrante : grupo.getIntegrantes()) {
            String dni = integrante.getDni();
            if (!dnisDelGrupo.add(dni)) {            // add() devuelve false si ya estaba
                throw new DniDuplicadoException(dni);
            }
            Solicitud existente = solicitudDAO.buscarPorDni(dni);
            if (existente != null) {
                throw new DniDuplicadoException(dni, existente.getNumero());
            }
        }

    }

    // Valida formato y rango de los datos del grupo familiar y de cada integrante.
    private void validarDatos(GrupoFamiliar grupo) throws DatosInvalidosException {

        if (grupo == null || grupo.cantidadIntegrantes() == 0) {
            throw new DatosInvalidosException("El grupo familiar debe tener al menos un integrante (el titular).");
        }
        if (grupo.getSituacionHabitacional() == null || grupo.getSituacionHabitacional().trim().isEmpty()) {
            throw new DatosInvalidosException("La situación habitacional no puede estar vacía.");
        }
        if (grupo.getIngresoNeto() < 0) {
            throw new DatosInvalidosException("El ingreso neto no puede ser negativo.");
        }
        for (Integrante integrante : grupo.getIntegrantes()) {
            if (integrante.getDni() == null || !integrante.getDni().matches("\\d{7,8}")) {
                throw new DatosInvalidosException("DNI inválido: debe tener 7 u 8 dígitos (" + integrante.getDni() + ").");
            }
            if (integrante.getApellidoNombre() == null || integrante.getApellidoNombre().trim().isEmpty()) {
                throw new DatosInvalidosException("El apellido y nombre del integrante DNI "
                        + integrante.getDni() + " no puede estar vacío.");
            }
            if (integrante.getFechaNacimiento() == null || integrante.getFechaNacimiento().isAfter(LocalDate.now())) {
                throw new DatosInvalidosException("Fecha de nacimiento inválida para el DNI " + integrante.getDni() + ".");
            }
        }

    }

}
