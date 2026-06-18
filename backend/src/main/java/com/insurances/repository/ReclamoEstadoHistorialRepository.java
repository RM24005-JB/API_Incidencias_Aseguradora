package com.insurances.repository;

import com.insurances.model.ReclamoEstadoHistorial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReclamoEstadoHistorialRepository extends JpaRepository<ReclamoEstadoHistorial, Long> {
    List<ReclamoEstadoHistorial> findByReclamoIdOrderByFechaCambioAsc(Long reclamoId);
}