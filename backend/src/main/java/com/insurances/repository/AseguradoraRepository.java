package com.insurances.repository;

import com.insurances.model.Aseguradora;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AseguradoraRepository extends JpaRepository<Aseguradora, Long> {
    List<Aseguradora> findByNombre(String nombre);
}