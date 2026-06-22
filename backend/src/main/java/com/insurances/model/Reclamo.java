package com.insurances.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reclamos",
       indexes = {
           @Index(name = "idx_reclamo_poliza", columnList = "poliza_id"),
           @Index(name = "idx_reclamo_usuario", columnList = "usuario_id"),
           @Index(name = "idx_reclamo_estado", columnList = "estado"),
           @Index(name = "idx_reclamo_fecha", columnList = "fechaCreacion")
       })
@Data
@EntityListeners(AuditingEntityListener.class)
public class Reclamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poliza_id")
    private Poliza poliza;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private LocalDateTime fechaSiniestro;
    private String descripcion;
    private BigDecimal montoEstimado;

    @Enumerated(EnumType.STRING)
    private EstadoReclamo estado = EstadoReclamo.REGISTRADO;

    private String numeroReferenciaExterno;

    // FIX: Remove @CreatedDate to allow manual date setting in DataInitializer
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "reclamo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Documento> documentos = new ArrayList<>();

    @OneToMany(mappedBy = "reclamo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReclamoEstadoHistorial> historialEstados = new ArrayList<>();
}
