package com.insurances.service;

import com.insurances.dto.DashboardStatsDTO;
import com.insurances.model.EstadoReclamo;
import com.insurances.model.Reclamo;
import com.insurances.repository.PolizaRepository;
import com.insurances.repository.ReclamoRepository;
import com.insurances.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {
    private final ReclamoRepository reclamoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PolizaRepository polizaRepository;

    public DashboardStatsDTO getStats(String userEmail, boolean isAdmin) {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        if (isAdmin) {
            // --- ADMIN: Estadísticas globales ---
            long totalPolizas = polizaRepository.count();
            stats.setTotalPolicies(totalPolizas);

            // FIX: Usar query de agregación en una sola consulta (rendimiento)
            Map<EstadoReclamo, Long> porEstado = new HashMap<>();
            for (Object[] result : reclamoRepository.countByEstado()) {
                EstadoReclamo estado = (EstadoReclamo) result[0];
                Long count = (Long) result[1];
                porEstado.put(estado, count);
            }
            for (EstadoReclamo estado : EstadoReclamo.values()) {
                porEstado.putIfAbsent(estado, 0L);
            }
            stats.setReclamosPorEstado(porEstado);

            long openClaims = porEstado.getOrDefault(EstadoReclamo.REGISTRADO, 0L)
                    + porEstado.getOrDefault(EstadoReclamo.EN_VALIDACION, 0L);
            stats.setOpenClaims(openClaims);
            stats.setApprovedClaims(porEstado.getOrDefault(EstadoReclamo.APROBADO, 0L));

            BigDecimal totalAmount = reclamoRepository.sumTotalAmount();
            stats.setTotalAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO);

            stats.setMonthlyClaims(getMonthlyClaims());
            stats.setRecentClaims(getRecentClaims());

        } else {
            // --- USUARIO NORMAL: Estadísticas personales ---
            Long usuarioId = usuarioRepository.findByEmail(userEmail)
                    .map(u -> u.getId())
                    .orElse(null);

            if (usuarioId != null) {
                long userPolicies = polizaRepository.countByUsuarioId(usuarioId);
                stats.setTotalPolicies(userPolicies);

                Map<EstadoReclamo, Long> userPorEstado = new HashMap<>();
                for (Object[] result : reclamoRepository.countByEstadoAndUsuarioId(usuarioId)) {
                    EstadoReclamo estado = (EstadoReclamo) result[0];
                    Long count = (Long) result[1];
                    userPorEstado.put(estado, count);
                }
                for (EstadoReclamo estado : EstadoReclamo.values()) {
                    userPorEstado.putIfAbsent(estado, 0L);
                }
                stats.setReclamosPorEstado(userPorEstado);

                long userOpen = userPorEstado.getOrDefault(EstadoReclamo.REGISTRADO, 0L)
                        + userPorEstado.getOrDefault(EstadoReclamo.EN_VALIDACION, 0L);
                stats.setOpenClaims(userOpen);
                long userApproved = userPorEstado.getOrDefault(EstadoReclamo.APROBADO, 0L);
                stats.setApprovedClaims(userApproved);

                BigDecimal userAmount = reclamoRepository.sumTotalAmountByUsuarioId(usuarioId);
                stats.setTotalAmount(userAmount != null ? userAmount : BigDecimal.ZERO);

                // FIX: Usar List<> en lugar de Page<> para evitar count query innecesario
                List<Reclamo> userReclamos = reclamoRepository.findByPolizaUsuarioIdList(usuarioId);

                List<DashboardStatsDTO.RecentClaim> recentUser = userReclamos.stream()
                        .sorted((a, b) -> b.getFechaCreacion().compareTo(a.getFechaCreacion()))
                        .limit(5)
                        .map(r -> new DashboardStatsDTO.RecentClaim(
                            r.getId(),
                            r.getPoliza().getAseguradora().getNombre() + " - " + r.getPoliza().getTipo(),
                            r.getDescripcion(),
                            r.getEstado().name(),
                            r.getMontoEstimado()
                        ))
                        .collect(Collectors.toList());
                stats.setRecentClaims(recentUser);

                stats.setMonthlyClaims(getMonthlyClaimsForUser(userReclamos));
            } else {
                stats.setTotalPolicies(0L);
                stats.setOpenClaims(0L);
                stats.setApprovedClaims(0L);
                stats.setTotalAmount(BigDecimal.ZERO);
                stats.setReclamosPorEstado(new HashMap<>());
                stats.setMonthlyClaims(new ArrayList<>());
                stats.setRecentClaims(new ArrayList<>());
            }
        }
        return stats;
    }

    private List<DashboardStatsDTO.MonthlyClaim> getMonthlyClaims() {
        List<Reclamo> allReclamos = reclamoRepository.findAll();
        return getMonthlyClaimsForUser(allReclamos);
    }

    private List<DashboardStatsDTO.MonthlyClaim> getMonthlyClaimsForUser(List<Reclamo> reclamos) {
        // Agrupar por número de mes (1-12) para evitar problemas de localización
        Map<Integer, Long> byMonthNumber = new HashMap<>();
        
        for (Reclamo reclamo : reclamos) {
            if (reclamo.getFechaCreacion() != null) {
                int month = reclamo.getFechaCreacion().getMonthValue();
                byMonthNumber.put(month, byMonthNumber.getOrDefault(month, 0L) + 1);
            }
        }

        List<DashboardStatsDTO.MonthlyClaim> result = new ArrayList<>();
        String[] monthsDisplay = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};

        for (int i = 1; i <= 12; i++) {
            long count = byMonthNumber.getOrDefault(i, 0L);
            result.add(new DashboardStatsDTO.MonthlyClaim(monthsDisplay[i - 1], count));
        }
        return result;
    }

    // Comentado temporalmente hasta que se resuelva el problema de parámetros NULL en PostgreSQL
    // private List<DashboardStatsDTO.RecentClaim> getRecentClaims() {
    //     List<Reclamo> recent = reclamoRepository.findAllWithFilters(
    //         null, null, null, null,
    //         PageRequest.of(0, 5, Sort.by("fechaCreacion").descending())
    //     ).getContent();
    // 
    //     return recent.stream().map(r -> new DashboardStatsDTO.RecentClaim(
    //         r.getId(),
    //         r.getPoliza().getAseguradora().getNombre() + " - " + r.getPoliza().getTipo(),
    //         r.getDescripcion(),
    //         r.getEstado().name(),
    //         r.getMontoEstimado()
    //     )).collect(Collectors.toList());
    // }

    private List<DashboardStatsDTO.RecentClaim> getRecentClaims() {
        // Implementación temporal sin filtros
        List<Reclamo> recent = reclamoRepository.findAll(
            PageRequest.of(0, 5, Sort.by("fechaCreacion").descending())
        ).getContent();

        return recent.stream().map(r -> new DashboardStatsDTO.RecentClaim(
            r.getId(),
            r.getPoliza().getAseguradora().getNombre() + " - " + r.getPoliza().getTipo(),
            r.getDescripcion(),
            r.getEstado().name(),
            r.getMontoEstimado()
        )).collect(Collectors.toList());
    }
}
