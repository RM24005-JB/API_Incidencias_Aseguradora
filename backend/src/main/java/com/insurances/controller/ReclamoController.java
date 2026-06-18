package com.insurances.controller;

import com.insurances.dto.ReclamoDTO;
import com.insurances.model.EstadoReclamo;
import com.insurances.service.ReclamoService;
import com.insurances.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reclamos")
@RequiredArgsConstructor
@Tag(name = "Reclamos", description = "Gestión de reclamos del usuario autenticado")
public class ReclamoController {
    private final ReclamoService reclamoService;
    private final UsuarioService usuarioService;

    private Long getUsuarioId(UserDetails user) {
        return usuarioService.getIdByEmail(user.getUsername());
    }

    @GetMapping
    @Operation(summary = "Listar reclamos del usuario (con paginación y filtros opcionales)")
    public ResponseEntity<Page<ReclamoDTO>> listarMisReclamos(@AuthenticationPrincipal UserDetails user,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size,
                                                              @RequestParam(required = false) EstadoReclamo estado,
                                                              @RequestParam(required = false) Long aseguradoraId,
                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
                                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta) {
        Long usuarioId = getUsuarioId(user);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());

        Page<ReclamoDTO> reclamos = reclamoService.listarPorUsuarioConFiltros(usuarioId, estado, aseguradoraId, fechaDesde, fechaHasta, pageable);

        return ResponseEntity.ok(reclamos);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo reclamo")
    public ResponseEntity<ReclamoDTO> crear(@Valid @RequestBody ReclamoDTO dto, 
                                           @AuthenticationPrincipal UserDetails user) {
        Long usuarioId = getUsuarioId(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reclamoService.crear(dto, usuarioId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de un reclamo")
    public ResponseEntity<ReclamoDTO> obtener(@PathVariable Long id, 
                                             @AuthenticationPrincipal UserDetails user) {
        Long usuarioId = getUsuarioId(user);
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(reclamoService.obtenerPorId(id, usuarioId, isAdmin));
    }
}
