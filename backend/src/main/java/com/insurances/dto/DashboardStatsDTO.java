package com.insurances.dto;

import com.insurances.model.EstadoReclamo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class DashboardStatsDTO {
    private Long totalPolicies;
    private Long openClaims;
    private Long approvedClaims;
    private BigDecimal totalAmount;
    private List<MonthlyClaim> monthlyClaims;
    private Map<EstadoReclamo, Long> reclamosPorEstado;
    private List<RecentClaim> recentClaims;

    @Data
    public static class MonthlyClaim {
        private String month;
        private Long count;
        public MonthlyClaim(String month, Long count) { this.month = month; this.count = count; }
    }

    @Data
    public static class RecentClaim {
        private Long id;
        private String policy;
        private String description;
        private String status;
        private BigDecimal amount;
        public RecentClaim(Long id, String policy, String description, String status, BigDecimal amount) {
            this.id = id; this.policy = policy; this.description = description; this.status = status; this.amount = amount;
        }
    }
}