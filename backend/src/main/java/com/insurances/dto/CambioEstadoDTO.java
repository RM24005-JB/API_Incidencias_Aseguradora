package com.insurances.dto;

import com.insurances.model.EstadoReclamo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CambioEstadoDTO {
    @NotNull
    private EstadoReclamo nuevoEstado;
}