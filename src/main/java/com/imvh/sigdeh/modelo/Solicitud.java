package com.imvh.sigdeh.modelo;

import java.time.LocalDate;

// Solicitud de inscripción de un grupo familiar en el registro de demanda.
public class Solicitud {

    private int numero;                              // 0 = todavía no persistida
    private final LocalDate fechaAlta;
    private LocalDate fechaUltimaActualizacion;
    private EstadoSolicitud estado;
    private final GrupoFamiliar grupoFamiliar;
    private Double ultimoPuntaje;                    // null = todavía no se calculó (RF16)

    // Constructor para una solicitud nueva: nace ACTIVA, con fecha de hoy.
    public Solicitud(GrupoFamiliar grupoFamiliar) {

        this(grupoFamiliar, LocalDate.now(), LocalDate.now(), EstadoSolicitud.ACTIVA);

    }

    // Constructor completo: se usa para cargar solicitudes con fechas y estado ya conocidos.
    public Solicitud(GrupoFamiliar grupoFamiliar, LocalDate fechaAlta,
                     LocalDate fechaUltimaActualizacion, EstadoSolicitud estado) {

        this.grupoFamiliar = grupoFamiliar;
        this.fechaAlta = fechaAlta;
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
        this.estado = estado;

    }

    // Registra que la solicitud fue revisada hoy (RF04).
    public void actualizar() {

        this.fechaUltimaActualizacion = LocalDate.now();

    }

    // Indica si superó el período de vigencia sin actualizarse (RF04).
    public boolean necesitaActualizacion(int mesesVigencia) {

        LocalDate vencimiento = fechaUltimaActualizacion.plusMonths(mesesVigencia);
        return vencimiento.isBefore(LocalDate.now());

    }

    // Solo las solicitudes activas participan de los listados.
    public boolean estaActiva() {

        return estado == EstadoSolicitud.ACTIVA;

    }

    // ---------- Getters y setters ----------

    public int getNumero() {

        return numero;

    }

    public void setNumero(int numero) {

        this.numero = numero;

    }

    public LocalDate getFechaAlta() {

        return fechaAlta;

    }

    public LocalDate getFechaUltimaActualizacion() {

        return fechaUltimaActualizacion;

    }

    public EstadoSolicitud getEstado() {

        return estado;

    }

    public void setEstado(EstadoSolicitud estado) {

        this.estado = estado;

    }

    public GrupoFamiliar getGrupoFamiliar() {

        return grupoFamiliar;

    }

    public Double getUltimoPuntaje() {

        return ultimoPuntaje;

    }

    public void setUltimoPuntaje(Double ultimoPuntaje) {

        this.ultimoPuntaje = ultimoPuntaje;

    }

}
