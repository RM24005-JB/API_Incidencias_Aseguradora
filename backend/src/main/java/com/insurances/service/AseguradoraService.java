package com.insurances.service;

import com.insurances.dto.AseguradoraDTO;
import com.insurances.exception.ResourceNotFoundException;
import com.insurances.model.Aseguradora;
import com.insurances.repository.AseguradoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AseguradoraService {
    private final AseguradoraRepository aseguradoraRepository;

    @Cacheable(value = "aseguradoras")
    public List<AseguradoraDTO> listarTodas() {
        return aseguradoraRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @CacheEvict(value = "aseguradoras", allEntries = true)
    public AseguradoraDTO crear(AseguradoraDTO dto) {
        Aseguradora a = new Aseguradora();
        a.setNombre(dto.getNombre());
        a.setNit(dto.getNit());
        a.setContactoEmail(dto.getContactoEmail());
        a.setLogoUrl(dto.getLogoUrl());
        return toDTO(aseguradoraRepository.save(a));
    }

    @CacheEvict(value = "aseguradoras", allEntries = true)
    public AseguradoraDTO actualizar(Long id, AseguradoraDTO dto) {
        Aseguradora a = aseguradoraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aseguradora no encontrada"));
        a.setNombre(dto.getNombre());
        a.setNit(dto.getNit());
        a.setContactoEmail(dto.getContactoEmail());
        a.setLogoUrl(dto.getLogoUrl());
        return toDTO(aseguradoraRepository.save(a));
    }

    @CacheEvict(value = "aseguradoras", allEntries = true)
    public void eliminar(Long id) {
        aseguradoraRepository.deleteById(id);
    }

    private AseguradoraDTO toDTO(Aseguradora a) {
        AseguradoraDTO dto = new AseguradoraDTO();
        dto.setId(a.getId());
        dto.setNombre(a.getNombre());
        dto.setNit(a.getNit());
        dto.setContactoEmail(a.getContactoEmail());
        dto.setLogoUrl(a.getLogoUrl());
        return dto;
    }
}