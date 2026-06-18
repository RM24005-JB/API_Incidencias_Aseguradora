package com.insurances.controller;

import com.insurances.exception.BusinessException;
import com.insurances.exception.ForbiddenException;
import com.insurances.exception.ResourceNotFoundException;
import com.insurances.model.Documento;
import com.insurances.model.Reclamo;
import com.insurances.model.Usuario;
import com.insurances.repository.DocumentoRepository;
import com.insurances.repository.ReclamoRepository;
import com.insurances.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
public class UploadController {
    private final DocumentoRepository documentoRepository;
    private final ReclamoRepository reclamoRepository;
    private final UsuarioRepository usuarioRepository;

    @Value("${file.upload-dir:/app/uploads}")
    private String uploadDir;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
        "application/pdf", "image/jpeg", "image/png", "image/jpg"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @PostMapping("/reclamo/{reclamoId}")
    @Transactional
    @Operation(summary = "Subir un documento asociado a un reclamo")
    public ResponseEntity<Map<String, String>> uploadDocument(
            @PathVariable Long reclamoId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails user) {

        Long usuarioAutenticadoId = getUsuarioIdFromEmail(user.getUsername());
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("Archivo demasiado grande. Maximo 10MB.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BusinessException("Tipo de archivo no permitido. Solo PDF, JPG o PNG.");
        }

        Reclamo reclamo = reclamoRepository.findById(reclamoId)
                .orElseThrow(() -> new ResourceNotFoundException("Reclamo no encontrado"));

        Long propietarioId = reclamo.getPoliza().getUsuario().getId();
        if (!isAdmin && !propietarioId.equals(usuarioAutenticadoId)) {
            throw new ForbiddenException("No tiene permisos para subir documentos a este reclamo");
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = (originalFilename != null && originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
            String storedFilename = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(storedFilename);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Documento documento = new Documento();
            documento.setNombreOriginal(originalFilename);
            documento.setTipoContenido(contentType);
            documento.setRutaArchivo(filePath.toString());
            // FIX: Usar tamano en lugar de tamaño
            documento.setTamano(file.getSize());
            documento.setReclamo(reclamo);
            documentoRepository.save(documento);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Archivo subido correctamente");
            response.put("fileName", originalFilename);
            response.put("storedName", storedFilename);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IOException e) {
            log.error("Error al guardar el archivo", e);
            throw new BusinessException("Error al guardar el archivo: " + e.getMessage());
        }
    }

    @GetMapping("/download/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "Descargar un documento por su ID")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {

        Long usuarioAutenticadoId = getUsuarioIdFromEmail(user.getUsername());
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Documento doc = documentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado"));

        Reclamo reclamo = doc.getReclamo();
        Long propietarioId = reclamo.getPoliza().getUsuario().getId();

        if (!isAdmin && !propietarioId.equals(usuarioAutenticadoId)) {
            throw new ForbiddenException("No tiene permisos para descargar este documento");
        }

        try {
            Path path = Paths.get(doc.getRutaArchivo());
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new BusinessException("No se puede leer el archivo");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(doc.getTipoContenido()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + doc.getNombreOriginal() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            throw new BusinessException("Error al acceder al archivo: " + e.getMessage());
        }
    }

    private Long getUsuarioIdFromEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .map(Usuario::getId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}