package com.insurances.repository;

import com.insurances.model.EstadoReclamo;
import com.insurances.model.Reclamo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ReclamoRepository extends JpaRepository<Reclamo, Long> {
    Page<Reclamo> findByPolizaUsuarioId(Long usuarioId, Pageable pageable);
    
    Page<Reclamo> findAll(Pageable pageable);

    // FIX: Método List<> para evitar count query innecesario en dashboard
    @Query("SELECT r FROM Reclamo r WHERE r.poliza.usuario.id = :usuarioId ORDER BY r.fechaCreacion DESC")
    List<Reclamo> findByPolizaUsuarioIdList(@Param("usuarioId") Long usuarioId);

    // Simplified filter methods - remove complex filtering to avoid errors
    Page<Reclamo> findByEstado(EstadoReclamo estado, Pageable pageable);
    Page<Reclamo> findByPolizaAseguradoraId(Long aseguradoraId, Pageable pageable);
    Page<Reclamo> findByPolizaUsuarioIdAndEstado(Long usuarioId, EstadoReclamo estado, Pageable pageable);
    Page<Reclamo> findByPolizaUsuarioIdAndPolizaAseguradoraId(Long usuarioId, Long aseguradoraId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(r.montoEstimado), 0) FROM Reclamo r")
    BigDecimal sumTotalAmount();

    @Query("SELECT r.estado, COUNT(r) FROM Reclamo r GROUP BY r.estado")
    List<Object[]> countByEstado();

    @Query("SELECT r.estado, COUNT(r) FROM Reclamo r WHERE r.poliza.usuario.id = :usuarioId GROUP BY r.estado")
    List<Object[]> countByEstadoAndUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COALESCE(SUM(r.montoEstimado), 0) FROM Reclamo r WHERE r.poliza.usuario.id = :usuarioId")
    BigDecimal sumTotalAmountByUsuarioId(@Param("usuarioId") Long usuarioId);
}
