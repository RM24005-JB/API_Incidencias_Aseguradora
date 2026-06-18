package com.insurances.dto;

import com.insurances.model.EstadoReclamo;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReclamoDTO {
    private Long id;
    @NotNull
    private Long polizaId;
    private String polizaNumero;
    private String aseguradoraNombre;
    private String tipoSeguro;
    @NotNull @PastOrPresent
    private LocalDateTime fechaSiniestro;
    @NotBlank @Size(max = 500)
    private String descripcion;
    @NotNull @Positive
    private BigDecimal montoEstimado;
    private EstadoReclamo estado;
    private String numeroReferenciaExterno;
    private LocalDateTime fechaCreacion;
    private String usuarioEmail;
}