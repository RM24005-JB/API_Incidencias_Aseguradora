package com.insurances.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "polizas",
       indexes = {
           @Index(name = "idx_numero_poliza", columnList = "numeroPoliza"),
           @Index(name = "idx_aseguradora_id", columnList = "aseguradora_id")
       })
@Data
@EntityListeners(AuditingEntityListener.class)
public class Poliza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aseguradora_id")
    private Aseguradora aseguradora;

    private String numeroPoliza;
    private String tipo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String coberturas;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "poliza_cobertura",
        joinColumns = @JoinColumn(name = "poliza_id"),
        inverseJoinColumns = @JoinColumn(name = "cobertura_id")
    )
    private Set<Cobertura> coberturasAdicionales = new HashSet<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}