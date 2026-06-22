package com.insurances.repository;

import com.insurances.model.Poliza;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PolizaRepository extends JpaRepository<Poliza, Long> {
    Page<Poliza> findAll(Pageable pageable);

    Page<Poliza> findByAseguradoraId(Long aseguradoraId, Pageable pageable);

    // Método para buscar póliza por número (retorna lista para evitar NonUniqueResultException)
    List<Poliza> findByNumeroPoliza(String numeroPoliza);
}
