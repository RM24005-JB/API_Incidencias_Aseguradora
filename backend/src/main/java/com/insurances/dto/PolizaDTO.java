package com.insurances.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PolizaDTO {
    private Long id;
    private Long usuarioId;
    @NotNull
    private Long aseguradoraId;
    private String nombreAseguradora;
    @NotBlank
    private String numeroPoliza;
    @NotBlank
    private String tipo;
    @NotNull @PastOrPresent
    private LocalDate fechaInicio;
    @NotNull @Future
    private LocalDate fechaFin;
    private String coberturas;
}