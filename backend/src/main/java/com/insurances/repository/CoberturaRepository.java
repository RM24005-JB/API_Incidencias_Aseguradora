package com.insurances.repository;

import com.insurances.model.Cobertura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoberturaRepository extends JpaRepository<Cobertura, Long> {
}