package com.insurances.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AseguradoraDTO {
    private Long id;
    @NotBlank
    private String nombre;
    @NotBlank
    private String nit;
    @NotBlank
    @Email  // FIX: Validar formato de email
    private String contactoEmail;
    private String logoUrl;
}
