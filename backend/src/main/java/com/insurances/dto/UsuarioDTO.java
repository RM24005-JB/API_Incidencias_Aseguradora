package com.insurances.dto;

import com.insurances.model.Role;
import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String email;
    private String nombre;
    private String telefono;
    private Role role;
    private boolean enabled;
}