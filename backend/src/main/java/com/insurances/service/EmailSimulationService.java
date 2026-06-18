package com.insurances.service;

import com.insurances.model.Reclamo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailSimulationService {
    public void enviarNotificacion(Reclamo reclamo) {
        log.info("=========================================");
        log.info("SIMULACIÓN DE NOTIFICACIÓN A ASEGURADORA");
        log.info("Reclamo ID: {}", reclamo.getId());
        log.info("Aseguradora: {}", reclamo.getPoliza().getAseguradora().getNombre());
        log.info("Póliza: {}", reclamo.getPoliza().getNumeroPoliza());
        log.info("Descripción: {}", reclamo.getDescripcion());
        log.info("Estado inicial: {}", reclamo.getEstado());
        log.info("=========================================");
    }
}