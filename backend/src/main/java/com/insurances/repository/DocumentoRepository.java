package com.insurances.repository;

import com.insurances.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    List<Documento> findByReclamoId(Long reclamoId);
}