package com.imvh.sigdeh.vista;

import com.imvh.sigdeh.excepciones.SigdehException;
import com.imvh.sigdeh.gestor.GestorDePriorizacion;
import com.imvh.sigdeh.gestor.GestorDeSolicitudes;
import com.imvh.sigdeh.modelo.GrupoFamiliar;
import com.imvh.sigdeh.modelo.Integrante;
import com.imvh.sigdeh.modelo.ListadoPrioridad;
import com.imvh.sigdeh.modelo.Operatoria;
import com.imvh.sigdeh.modelo.Posicion;
import com.imvh.sigdeh.modelo.Solicitud;
import com.imvh.sigdeh.modelo.Universo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Menú de consola: muestra información y pide datos; las reglas viven en los gestores.
public class MenuPrincipal {

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final GestorDeSolicitudes gestorSolicitudes;
    private final GestorDePriorizacion gestorPriorizacion;
    private final Consola consola;

    public MenuPrincipal(GestorDeSolicitudes gestorSolicitudes, GestorDePriorizacion gestorPriorizacion,
                         Consola consola) {

        this.gestorSolicitudes = gestorSolicitudes;
        this.gestorPriorizacion = gestorPriorizacion;
        this.consola = consola;

    }

    // Bucle principal: muestra el menú hasta que el usuario elige salir.
    public void iniciar() {

        int opcion;
        do {
            mostrarMenu();
            opcion = consola.leerEntero("Seleccione una opción: ");
            System.out.println();
            try {
                switch (opcion) {
                    case 1:
                        registrarSolicitud();
                        break;
                    case 2:
                        actualizarSolicitud();
                        break;
                    case 3:
                        consultarEstado();
                        break;
                    case 4:
                        buscarPorDni();
                        break;
                    case 5:
                        listarSolicitudes();
                        break;
                    case 6:
                        emitirListado();
                        break;
                    case 0:
                        System.out.println("Cerrando SIGDEH. ¡Hasta luego!");
                        break;
                    default:
                        System.out.println("Opción inválida. Elija un número entre 0 y 6.");
                }
            } catch (SigdehException e) {
                // Cualquier error de negocio (residencia, DNI duplicado, no encontrado...) llega hasta acá
                System.out.println("ERROR: " + e.getMessage());
            }
        } while (opcion != 0);

    }

    // Imprime las opciones del menú.
    private void mostrarMenu() {

        System.out.println();
        System.out.println("=================================================");
        System.out.println("  SIGDEH - Gestión y Priorización de la Demanda");
        System.out.println("            Habitacional (IMVH)");
        System.out.println("=================================================");
        System.out.println(" 1. Registrar solicitud");
        System.out.println(" 2. Actualizar solicitud");
        System.out.println(" 3. Consultar estado de una solicitud");
        System.out.println(" 4. Buscar solicitud por DNI");
        System.out.println(" 5. Listar solicitudes registradas");
        System.out.println(" 6. Emitir listado de prioridad");
        System.out.println(" 0. Salir");
        System.out.println("-------------------------------------------------");

    }

    // CU001 Registrar solicitud
    private void registrarSolicitud() throws SigdehException {

        System.out.println("--- Nueva solicitud ---");
        System.out.println("Datos del titular:");
        LocalDate fechaResidencia = consola.leerFecha("Fecha de inicio de residencia en la localidad");
        String situacion = consola.leerTexto("Situación habitacional actual (ej: Alquiler, Vivienda precaria, Cedida): ");
        double ingreso = consola.leerDecimal("Ingreso neto mensual del grupo familiar: ");

        GrupoFamiliar grupo = new GrupoFamiliar(situacion, ingreso, fechaResidencia);
        grupo.agregarIntegrante(leerIntegrante("Titular"));

        while (consola.leerSiNo("¿Desea agregar otro integrante?")) {
            System.out.println("Datos del integrante:");
            String vinculo = consola.leerTexto("Vínculo con el titular (ej: Hijo/a, Conyuge): ");
            grupo.agregarIntegrante(leerIntegrante(vinculo));
        }

        // Las validaciones (residencia, DNI duplicado, formato) las hace el gestor
        int numero = gestorSolicitudes.registrarSolicitud(grupo);
        System.out.println("Solicitud registrada con estado ACTIVA. Número de solicitud asignado: " + numero);

    }

    // Pide los datos de un integrante y lo crea.
    private Integrante leerIntegrante(String vinculo) {

        String dni = consola.leerTexto("  DNI (sin puntos): ");
        String apellidoNombre = consola.leerTexto("  Apellido y nombre: ");
        LocalDate nacimiento = consola.leerFecha("  Fecha de nacimiento");
        boolean discapacidad = consola.leerSiNo("  ¿Tiene discapacidad?");
        return new Integrante(dni, apellidoNombre, nacimiento, vinculo, discapacidad);

    }

    // CU002 Actualizar solicitud
    private void actualizarSolicitud() throws SigdehException {

        int numero = consola.leerEntero("Número de solicitud a actualizar: ");
        Solicitud solicitud = gestorSolicitudes.consultarEstado(numero);     // falla si no existe
        GrupoFamiliar grupo = solicitud.getGrupoFamiliar();
        System.out.println("Situación habitacional actual: " + grupo.getSituacionHabitacional());
        System.out.printf("Ingreso neto actual: %.2f%n", grupo.getIngresoNeto());

        String nuevaSituacion = consola.leerTexto("Nueva situación habitacional: ");
        double nuevoIngreso = consola.leerDecimal("Nuevo ingreso neto mensual: ");
        gestorSolicitudes.actualizarSolicitud(numero, nuevaSituacion, nuevoIngreso);
        System.out.println("Solicitud actualizada. Fecha de última actualización: "
                + LocalDate.now().format(Consola.FORMATO_FECHA));

    }

    // CU003 Consultar estado (RF16)
    private void consultarEstado() throws SigdehException {

        int numero = consola.leerEntero("Número de solicitud: ");
        mostrarDetalle(gestorSolicitudes.consultarEstado(numero));

    }

    // Busca una solicitud por DNI y muestra su detalle.
    private void buscarPorDni() {

        String dni = consola.leerTexto("DNI a buscar: ");
        Solicitud solicitud = gestorSolicitudes.buscarPorDni(dni);
        if (solicitud == null) {
            System.out.println("El DNI " + dni + " no figura en ninguna solicitud.");
        } else {
            mostrarDetalle(solicitud);
        }

    }

    // Imprime los datos de una solicitud (RF16).
    private void mostrarDetalle(Solicitud solicitud) {

        GrupoFamiliar grupo = solicitud.getGrupoFamiliar();
        System.out.println("Solicitud N° " + solicitud.getNumero());
        System.out.println("  Titular:                  " + grupo.getTitular().getApellidoNombre());
        System.out.println("  Estado:                   " + solicitud.getEstado());
        System.out.println("  Fecha de alta:            " + solicitud.getFechaAlta().format(Consola.FORMATO_FECHA));
        System.out.println("  Última actualización:     "
                + solicitud.getFechaUltimaActualizacion().format(Consola.FORMATO_FECHA));
        if (gestorSolicitudes.estaDesactualizada(solicitud)) {
            System.out.println("  *** ATENCIÓN: solicitud DESACTUALIZADA, supera el período de vigencia ***");
        }
        System.out.println("  Situación habitacional:   " + grupo.getSituacionHabitacional());
        System.out.printf("  Ingreso neto:             %.2f%n", grupo.getIngresoNeto());
        System.out.println("  Años de residencia:       " + grupo.aniosDeResidencia());
        System.out.println("  Integrantes (" + grupo.cantidadIntegrantes() + "):");
        for (Integrante integrante : grupo.getIntegrantes()) {
            System.out.println("    - " + integrante);
        }
        if (solicitud.getUltimoPuntaje() == null) {
            System.out.println("  Último puntaje calculado: sin calcular");
        } else {
            System.out.printf("  Último puntaje calculado: %.2f%n", solicitud.getUltimoPuntaje());
        }

    }

    // Imprime una tabla con todas las solicitudes y marca las desactualizadas.
    private void listarSolicitudes() {

        List<Solicitud> solicitudes = gestorSolicitudes.listarSolicitudes();
        if (solicitudes.isEmpty()) {
            System.out.println("No hay solicitudes registradas.");
            return;
        }
        System.out.printf("%-5s %-25s %-10s %-20s %-12s%n", "N°", "Titular", "Estado", "Situación", "Últ. actual.");
        for (Solicitud s : solicitudes) {
            String marca = gestorSolicitudes.estaDesactualizada(s) ? "  (desactualizada)" : "";
            System.out.printf("%-5d %-25s %-10s %-20s %-12s%s%n",
                    s.getNumero(),
                    s.getGrupoFamiliar().getTitular().getApellidoNombre(),
                    s.getEstado(),
                    s.getGrupoFamiliar().getSituacionHabitacional(),
                    s.getFechaUltimaActualizacion().format(Consola.FORMATO_FECHA),
                    marca);
        }

    }

    // CU006 Emitir listado de prioridad
    private void emitirListado() throws SigdehException {

        List<Operatoria> abiertas = gestorPriorizacion.listarOperatoriasAbiertas();
        if (abiertas.isEmpty()) {
            System.out.println("No hay operatorias abiertas.");
            return;
        }
        System.out.println("Operatorias abiertas:");
        for (Operatoria operatoria : abiertas) {
            System.out.println("  " + operatoria);
        }
        int id = consola.leerEntero("Seleccione el número de operatoria: ");

        ListadoPrioridad listado = gestorPriorizacion.emitirListado(id);
        mostrarListado(listado);

    }

    // Imprime el listado de prioridad agrupado por universo.
    private void mostrarListado(ListadoPrioridad listado) {

        System.out.println();
        System.out.println("LISTADO DE PRIORIDAD N° " + listado.getId());
        System.out.println(listado.getOperatoria());
        System.out.println("Emitido: " + listado.getFechaEmision().format(FORMATO_FECHA_HORA)
                + " | Versión del esquema aplicado: " + listado.getVersionEsquema());

        for (Universo universo : listado.getOperatoria().getUniversos()) {
            System.out.println();
            System.out.println("== " + universo + " ==");
            List<Posicion> posiciones = listado.getPosicionesDeUniverso(universo);
            if (posiciones.isEmpty()) {
                System.out.println("  Ninguna solicitud cumple los requisitos: cupo vacante.");
                continue;
            }
            System.out.printf("  %-6s %-10s %-25s %-10s %-8s%n", "Orden", "Solicitud", "Titular", "Puntaje", "Cupo");
            for (Posicion p : posiciones) {
                String dentroDeCupo = p.getOrden() <= universo.getCupo() ? "SI" : "NO";
                System.out.printf("  %-6d %-10d %-25s %-10.2f %-8s%n",
                        p.getOrden(),
                        p.getSolicitud().getNumero(),
                        p.getSolicitud().getGrupoFamiliar().getTitular().getApellidoNombre(),
                        p.getPuntaje(),
                        dentroDeCupo);
            }
        }

    }

}
