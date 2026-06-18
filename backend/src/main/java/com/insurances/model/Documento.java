package com.insurances.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class Documento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreOriginal;
    private String tipoContenido;
    private String rutaArchivo;

    // FIX: Renombrar campo con tilde para evitar problemas de charset en Linux/Docker
    @Column(name = "tamano")
    private Long tamano;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reclamo_id")
    private Reclamo reclamo;

    @CreatedDate
    private LocalDateTime fechaSubida;
}
