package com.insurances.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class ReclamoEstadoHistorial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reclamo_id")
    private Reclamo reclamo;

    @Enumerated(EnumType.STRING)
    private EstadoReclamo estadoAnterior;

    @Enumerated(EnumType.STRING)
    private EstadoReclamo estadoNuevo;

    private String cambiadoPor;

    @CreatedDate
    private LocalDateTime fechaCambio;
}