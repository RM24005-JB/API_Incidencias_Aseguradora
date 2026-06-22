package com.insurances.config;

import com.insurances.model.*;
import com.insurances.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final UsuarioRepository usuarioRepository;
    private final AseguradoraRepository aseguradoraRepository;
    private final CoberturaRepository coberturaRepository;
    private final PolizaRepository polizaRepository;
    private final ReclamoRepository reclamoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (coberturaRepository.count() == 0) {
            Cobertura robo = new Cobertura(); robo.setNombre("Robo"); robo.setDescripcion("Cobertura contra robo total del vehículo"); coberturaRepository.save(robo);
            Cobertura granizo = new Cobertura(); granizo.setNombre("Granizo"); granizo.setDescripcion("Daños por granizo"); coberturaRepository.save(granizo);
            Cobertura rc = new Cobertura(); rc.setNombre("Responsabilidad Civil"); rc.setDescripcion("Cobertura por daños a terceros"); coberturaRepository.save(rc);
            Cobertura collision = new Cobertura(); collision.setNombre("Colisión"); collision.setDescripcion("Daños por colisión con otro vehículo"); coberturaRepository.save(collision);
            Cobertura medical = new Cobertura(); medical.setNombre("Gastos Médicos"); medical.setDescripcion("Cobertura de gastos médicos por accidente"); coberturaRepository.save(medical);
            log.info("Coberturas de ejemplo creadas.");
        }
        if (!usuarioRepository.existsByEmail("admin@insurances.com")) {
            Usuario admin = Usuario.builder().email("admin@insurances.com").password(passwordEncoder.encode("admin123")).nombre("Administrador").telefono("2222-0000").role(Role.ADMIN).enabled(true).build();
            usuarioRepository.save(admin);
            log.info("Usuario administrador creado: admin@insurances.com");
        }
        if (aseguradoraRepository.count() == 0) {
            Aseguradora suiza = new Aseguradora(); suiza.setNombre("Aseguradora Suiza"); suiza.setNit("0614-010866-101-5"); suiza.setContactoEmail("servicio.cliente@aseguradorasuiza.com.sv"); suiza.setLogoUrl("https://www.aseguradorasuiza.com.sv/wp-content/uploads/2020/01/logo-aseguradora-suiza.png"); aseguradoraRepository.save(suiza);
            Aseguradora roble = new Aseguradora(); roble.setNombre("Seguros El Roble"); roble.setNit("0614-020577-101-4"); roble.setContactoEmail("info@seguroselroble.com.sv"); roble.setLogoUrl("https://www.seguroselroble.com.sv/wp-content/uploads/2019/06/logo-seguros-el-roble.png"); aseguradoraRepository.save(roble);
            Aseguradora atlantida = new Aseguradora(); atlantida.setNombre("Seguros Atlántida"); atlantida.setNit("0614-030466-101-3"); atlantida.setContactoEmail("atencion@segurosatlantida.com.sv"); atlantida.setLogoUrl("https://www.segurosatlantida.com.sv/wp-content/uploads/2020/02/logo-seguros-atlantida.png"); aseguradoraRepository.save(atlantida);
            Aseguradora centro = new Aseguradora(); centro.setNombre("La Centroamericana"); centro.setNit("0614-040355-101-2"); centro.setContactoEmail("servicio@lacentroamericana.com.sv"); centro.setLogoUrl("https://www.lacentroamericana.com.sv/wp-content/uploads/2019/08/logo-la-centroamericana.png"); aseguradoraRepository.save(centro);
            Aseguradora sisa = new Aseguradora(); sisa.setNombre("Seguros SISA"); sisa.setNit("0614-050244-101-1"); sisa.setContactoEmail("contacto@segurossisa.com.sv"); sisa.setLogoUrl("https://www.segurossisa.com.sv/wp-content/uploads/2020/03/logo-seguros-sisa.png"); aseguradoraRepository.save(sisa);
            log.info("Se crearon 5 aseguradoras de El Salvador.");
        }
        if (!usuarioRepository.existsByEmail("cliente@example.com")) {
            Usuario cliente = Usuario.builder().email("cliente@example.com").password(passwordEncoder.encode("cliente123")).nombre("Cliente Ejemplo").telefono("2234-5678").role(Role.USER).enabled(true).build();
            usuarioRepository.save(cliente);
            log.info("Usuario cliente de ejemplo creado: cliente@example.com");
        }
        if (!usuarioRepository.existsByEmail("clienteA@example.com")) {
            Usuario clienteA = Usuario.builder().email("clienteA@example.com").password(passwordEncoder.encode("clienteA123")).nombre("Cliente A").telefono("2234-1111").role(Role.USER).enabled(true).build();
            usuarioRepository.save(clienteA);
            log.info("Usuario cliente A creado: clienteA@example.com");
        }
        if (!usuarioRepository.existsByEmail("clienteB@example.com")) {
            Usuario clienteB = Usuario.builder().email("clienteB@example.com").password(passwordEncoder.encode("clienteB123")).nombre("Cliente B").telefono("2234-2222").role(Role.USER).enabled(true).build();
            usuarioRepository.save(clienteB);
            log.info("Usuario cliente B creado: clienteB@example.com");
        }
        if (!usuarioRepository.existsByEmail("clienteC@example.com")) {
            Usuario clienteC = Usuario.builder().email("clienteC@example.com").password(passwordEncoder.encode("clienteC123")).nombre("Cliente C").telefono("2234-3333").role(Role.USER).enabled(true).build();
            usuarioRepository.save(clienteC);
            log.info("Usuario cliente C creado: clienteC@example.com");
        }
        
        // Crear pólizas de ejemplo con contexto de El Salvador
        Usuario cliente = usuarioRepository.findByEmail("cliente@example.com").orElse(null);
        if (cliente != null && polizaRepository.count() < 10) {
            List<Aseguradora> suizaList = aseguradoraRepository.findByNombre("Aseguradora Suiza");
            List<Aseguradora> robleList = aseguradoraRepository.findByNombre("Seguros El Roble");
            List<Aseguradora> atlantidaList = aseguradoraRepository.findByNombre("Seguros Atlántida");
            List<Aseguradora> centroList = aseguradoraRepository.findByNombre("La Centroamericana");
            List<Aseguradora> sisaList = aseguradoraRepository.findByNombre("Seguros SISA");
            
            Aseguradora suiza = suizaList.isEmpty() ? null : suizaList.get(0);
            Aseguradora roble = robleList.isEmpty() ? null : robleList.get(0);
            Aseguradora atlantida = atlantidaList.isEmpty() ? null : atlantidaList.get(0);
            Aseguradora centro = centroList.isEmpty() ? null : centroList.get(0);
            Aseguradora sisa = sisaList.isEmpty() ? null : sisaList.get(0);
            
            log.info("Aseguradoras encontradas - Suiza: {}, Roble: {}, Atlántida: {}, Centro: {}, SISA: {}", 
                suiza != null, roble != null, atlantida != null, centro != null, sisa != null);
            
            // Listar todas las aseguradoras para depuración
            aseguradoraRepository.findAll().forEach(a -> log.info("Aseguradora en DB: {}", a.getNombre()));
            
            if (suiza != null) {
                Poliza poliza1 = new Poliza();
                poliza1.setNumeroPoliza("AS-2024-001");
                poliza1.setTipo("AUTO");
                poliza1.setFechaInicio(LocalDate.of(2024, 1, 1));
                poliza1.setFechaFin(LocalDate.of(2024, 12, 31));
                poliza1.setCoberturas("Robo, Responsabilidad Civil, Gastos Médicos");
                poliza1.setAseguradora(suiza);
                polizaRepository.save(poliza1);
                
                Poliza poliza2 = new Poliza();
                poliza2.setNumeroPoliza("AS-2024-002");
                poliza2.setTipo("SALUD");
                poliza2.setFechaInicio(LocalDate.of(2024, 3, 1));
                poliza2.setFechaFin(LocalDate.of(2025, 2, 28));
                poliza2.setCoberturas("Gastos Médicos, Hospitalización");
                poliza2.setAseguradora(suiza);
                polizaRepository.save(poliza2);
                
                Poliza poliza3 = new Poliza();
                poliza3.setNumeroPoliza("AS-2024-003");
                poliza3.setTipo("HOGAR");
                poliza3.setFechaInicio(LocalDate.of(2024, 5, 1));
                poliza3.setFechaFin(LocalDate.of(2025, 4, 30));
                poliza3.setCoberturas("Incendio, Robo, Daños por agua");
                poliza3.setAseguradora(suiza);
                polizaRepository.save(poliza3);
            }
            
            if (roble != null) {
                Poliza poliza4 = new Poliza();
                poliza4.setNumeroPoliza("ER-2024-001");
                poliza4.setTipo("AUTO");
                poliza4.setFechaInicio(LocalDate.of(2024, 6, 1));
                poliza4.setFechaFin(LocalDate.of(2025, 5, 31));
                poliza4.setCoberturas("Colisión, Responsabilidad Civil");
                poliza4.setAseguradora(roble);
                polizaRepository.save(poliza4);
                
                Poliza poliza5 = new Poliza();
                poliza5.setNumeroPoliza("ER-2024-002");
                poliza5.setTipo("VIDA");
                poliza5.setFechaInicio(LocalDate.of(2024, 2, 1));
                poliza5.setFechaFin(LocalDate.of(2034, 1, 31));
                poliza5.setCoberturas("Muerte accidental, Invalidez");
                poliza5.setAseguradora(roble);
                polizaRepository.save(poliza5);
            }
            
            if (atlantida != null) {
                Poliza poliza6 = new Poliza();
                poliza6.setNumeroPoliza("AT-2024-001");
                poliza6.setTipo("AUTO");
                poliza6.setFechaInicio(LocalDate.of(2024, 7, 1));
                poliza6.setFechaFin(LocalDate.of(2025, 6, 30));
                poliza6.setCoberturas("Todo riesgo, Asistencia en carretera");
                poliza6.setAseguradora(atlantida);
                polizaRepository.save(poliza6);
            }
            
            if (centro != null) {
                Poliza poliza7 = new Poliza();
                poliza7.setNumeroPoliza("LC-2024-001");
                poliza7.setTipo("SALUD");
                poliza7.setFechaInicio(LocalDate.of(2024, 4, 1));
                poliza7.setFechaFin(LocalDate.of(2025, 3, 31));
                poliza7.setCoberturas("Gastos Médicos, Medicamentos, Consultas");
                poliza7.setAseguradora(centro);
                polizaRepository.save(poliza7);
            }
            
            if (sisa != null) {
                Poliza poliza8 = new Poliza();
                poliza8.setNumeroPoliza("SISA-2024-001");
                poliza8.setTipo("AUTO");
                poliza8.setFechaInicio(LocalDate.of(2024, 8, 1));
                poliza8.setFechaFin(LocalDate.of(2025, 7, 31));
                poliza8.setCoberturas("Responsabilidad Civil, Daños a terceros");
                poliza8.setAseguradora(sisa);
                polizaRepository.save(poliza8);
            }
            
            log.info("Se crearon pólizas de ejemplo.");
        }
        
        // Crear reclamos de ejemplo con contexto de El Salvador
        if (cliente != null && reclamoRepository.count() < 15) {
            // Verificar si ya existen reclamos para este cliente antes de crearlos
            List<Reclamo> existingClaimsForCliente = reclamoRepository.findByPolizaUsuarioIdList(cliente.getId());
            if (existingClaimsForCliente.isEmpty()) {
            List<Poliza> poliza1List = polizaRepository.findByNumeroPoliza("AS-2024-001");
            List<Poliza> poliza2List = polizaRepository.findByNumeroPoliza("ER-2024-001");
            List<Poliza> poliza3List = polizaRepository.findByNumeroPoliza("AS-2024-002");
            List<Poliza> poliza4List = polizaRepository.findByNumeroPoliza("AT-2024-001");
            List<Poliza> poliza5List = polizaRepository.findByNumeroPoliza("LC-2024-001");
            List<Poliza> poliza6List = polizaRepository.findByNumeroPoliza("SISA-2024-001");
            List<Poliza> poliza7List = polizaRepository.findByNumeroPoliza("AS-2024-003");
            List<Poliza> poliza8List = polizaRepository.findByNumeroPoliza("ER-2024-002");
            
            Poliza poliza1 = poliza1List.isEmpty() ? null : poliza1List.get(0);
            Poliza poliza2 = poliza2List.isEmpty() ? null : poliza2List.get(0);
            Poliza poliza3 = poliza3List.isEmpty() ? null : poliza3List.get(0);
            Poliza poliza4 = poliza4List.isEmpty() ? null : poliza4List.get(0);
            Poliza poliza5 = poliza5List.isEmpty() ? null : poliza5List.get(0);
            Poliza poliza6 = poliza6List.isEmpty() ? null : poliza6List.get(0);
            Poliza poliza7 = poliza7List.isEmpty() ? null : poliza7List.get(0);
            Poliza poliza8 = poliza8List.isEmpty() ? null : poliza8List.get(0);
            
            if (poliza1 != null) {
                Reclamo reclamo1 = new Reclamo();
                reclamo1.setPoliza(poliza1);
                reclamo1.setUsuario(cliente);
                reclamo1.setFechaCreacion(LocalDateTime.of(2024, 6, 15, 10, 30));
                reclamo1.setFechaSiniestro(LocalDateTime.of(2024, 6, 14, 15, 45));
                reclamo1.setDescripcion("Colisión en boulevard de los Héroes, San Salvador. Daño en paragolpes delantero.");
                reclamo1.setMontoEstimado(new BigDecimal("2500.00"));
                reclamo1.setEstado(EstadoReclamo.APROBADO);
                reclamoRepository.save(reclamo1);
                
                Reclamo reclamo2 = new Reclamo();
                reclamo2.setPoliza(poliza1);
                reclamo2.setUsuario(cliente);
                reclamo2.setFechaCreacion(LocalDateTime.of(2024, 8, 20, 14, 15));
                reclamo2.setFechaSiniestro(LocalDateTime.of(2024, 8, 19, 8, 30));
                reclamo2.setDescripcion("Robo de espejos laterales en estacionamiento de Metrocentro, Santa Tecla.");
                reclamo2.setMontoEstimado(new BigDecimal("350.00"));
                reclamo2.setEstado(EstadoReclamo.PAGADO);
                reclamoRepository.save(reclamo2);
            }
            
            if (poliza2 != null) {
                Reclamo reclamo3 = new Reclamo();
                reclamo3.setPoliza(poliza2);
                reclamo3.setUsuario(cliente);
                reclamo3.setFechaCreacion(LocalDateTime.of(2024, 9, 5, 9, 15));
                reclamo3.setFechaSiniestro(LocalDateTime.of(2024, 9, 4, 16, 20));
                reclamo3.setDescripcion("Intento de robo en estacionamiento de Multiplaza, San Salvador. Daño en cerradura.");
                reclamo3.setMontoEstimado(new BigDecimal("1800.00"));
                reclamo3.setEstado(EstadoReclamo.EN_VALIDACION);
                reclamoRepository.save(reclamo3);
                
                Reclamo reclamo4 = new Reclamo();
                reclamo4.setPoliza(poliza2);
                reclamo4.setUsuario(cliente);
                reclamo4.setFechaCreacion(LocalDateTime.of(2024, 10, 12, 16, 30));
                reclamo4.setFechaSiniestro(LocalDateTime.of(2024, 10, 11, 9, 45));
                reclamo4.setDescripcion("Accidente en carretera a La Libertad. Choque trasero en semáforo.");
                reclamo4.setMontoEstimado(new BigDecimal("3200.00"));
                reclamo4.setEstado(EstadoReclamo.REGISTRADO);
                reclamoRepository.save(reclamo4);
            }
            
            if (poliza3 != null) {
                Reclamo reclamo5 = new Reclamo();
                reclamo5.setPoliza(poliza3);
                reclamo5.setUsuario(cliente);
                reclamo5.setFechaCreacion(LocalDateTime.of(2024, 7, 8, 11, 20));
                reclamo5.setFechaSiniestro(LocalDateTime.of(2024, 7, 7, 8, 15));
                reclamo5.setDescripcion("Consulta de emergencia en Hospital de la Mujer, San Salvador. Apendicitis.");
                reclamo5.setMontoEstimado(new BigDecimal("4500.00"));
                reclamo5.setEstado(EstadoReclamo.APROBADO);
                reclamoRepository.save(reclamo5);
                
                Reclamo reclamo6 = new Reclamo();
                reclamo6.setPoliza(poliza3);
                reclamo6.setUsuario(cliente);
                reclamo6.setFechaCreacion(LocalDateTime.of(2024, 11, 3, 8, 45));
                reclamo6.setFechaSiniestro(LocalDateTime.of(2024, 11, 2, 14, 30));
                reclamo6.setDescripcion("Procedimiento quirúrgico en Hospital Diagnóstico, San Salvador. Cirugía ambulatoria.");
                reclamo6.setMontoEstimado(new BigDecimal("2800.00"));
                reclamo6.setEstado(EstadoReclamo.PAGADO);
                reclamoRepository.save(reclamo6);
            }
            
            if (poliza4 != null) {
                Reclamo reclamo7 = new Reclamo();
                reclamo7.setPoliza(poliza4);
                reclamo7.setUsuario(cliente);
                reclamo7.setFechaCreacion(LocalDateTime.of(2024, 8, 25, 13, 10));
                reclamo7.setFechaSiniestro(LocalDateTime.of(2024, 8, 24, 17, 45));
                reclamo7.setDescripcion("Volcadura en carretera Panamericana, cerca de San Miguel. Daños severos.");
                reclamo7.setMontoEstimado(new BigDecimal("8500.00"));
                reclamo7.setEstado(EstadoReclamo.EN_VALIDACION);
                reclamoRepository.save(reclamo7);
            }
            
            if (poliza5 != null) {
                Reclamo reclamo8 = new Reclamo();
                reclamo8.setPoliza(poliza5);
                reclamo8.setUsuario(cliente);
                reclamo8.setFechaCreacion(LocalDateTime.of(2024, 9, 18, 10, 0));
                reclamo8.setFechaSiniestro(LocalDateTime.of(2024, 9, 17, 11, 30));
                reclamo8.setDescripcion("Hospitalización por dengue en Hospital Nacional Rosales, San Salvador.");
                reclamo8.setMontoEstimado(new BigDecimal("3200.00"));
                reclamo8.setEstado(EstadoReclamo.APROBADO);
                reclamoRepository.save(reclamo8);
            }
            
            if (poliza6 != null) {
                Reclamo reclamo9 = new Reclamo();
                reclamo9.setPoliza(poliza6);
                reclamo9.setUsuario(cliente);
                reclamo9.setFechaCreacion(LocalDateTime.of(2024, 10, 5, 15, 30));
                reclamo9.setFechaSiniestro(LocalDateTime.of(2024, 10, 4, 18, 15));
                reclamo9.setDescripcion("Choque en intersección de Alameda Roosevelt y 25 Avenida Norte, San Salvador.");
                reclamo9.setMontoEstimado(new BigDecimal("1500.00"));
                reclamo9.setEstado(EstadoReclamo.REGISTRADO);
                reclamoRepository.save(reclamo9);
                
                Reclamo reclamo10 = new Reclamo();
                reclamo10.setPoliza(poliza6);
                reclamo10.setUsuario(cliente);
                reclamo10.setFechaCreacion(LocalDateTime.of(2024, 12, 1, 9, 45));
                reclamo10.setFechaSiniestro(LocalDateTime.of(2024, 11, 30, 16, 0));
                reclamo10.setDescripcion("Daño por granizo en zona de Santa Ana. Parabrisas y techo afectados.");
                reclamo10.setMontoEstimado(new BigDecimal("1200.00"));
                reclamo10.setEstado(EstadoReclamo.EN_VALIDACION);
                reclamoRepository.save(reclamo10);
            }
            
            if (poliza7 != null) {
                Reclamo reclamo11 = new Reclamo();
                reclamo11.setPoliza(poliza7);
                reclamo11.setUsuario(cliente);
                reclamo11.setFechaCreacion(LocalDateTime.of(2024, 11, 15, 14, 20));
                reclamo11.setFechaSiniestro(LocalDateTime.of(2024, 11, 14, 7, 30));
                reclamo11.setDescripcion("Inundación en residencia en Colonia Escalón, San Salvador. Daños en sala.");
                reclamo11.setMontoEstimado(new BigDecimal("5500.00"));
                reclamo11.setEstado(EstadoReclamo.APROBADO);
                reclamoRepository.save(reclamo11);
                
                Reclamo reclamo12 = new Reclamo();
                reclamo12.setPoliza(poliza7);
                reclamo12.setUsuario(cliente);
                reclamo12.setFechaCreacion(LocalDateTime.of(2024, 12, 10, 11, 0));
                reclamo12.setFechaSiniestro(LocalDateTime.of(2024, 12, 9, 22, 15));
                reclamo12.setDescripcion("Intento de robo en vivienda en Antiguo Cuscatlán. Puerta forzada.");
                reclamo12.setMontoEstimado(new BigDecimal("800.00"));
                reclamo12.setEstado(EstadoReclamo.RECHAZADO);
                reclamoRepository.save(reclamo12);
            }
            
            if (poliza8 != null) {
                Reclamo reclamo13 = new Reclamo();
                reclamo13.setPoliza(poliza8);
                reclamo13.setUsuario(cliente);
                reclamo13.setFechaCreacion(LocalDateTime.of(2024, 12, 5, 16, 45));
                reclamo13.setFechaSiniestro(LocalDateTime.of(2024, 12, 4, 10, 30));
                reclamo13.setDescripcion("Accidente de tránsito en boulevard Constitución, San Salvador. Lesiones leves.");
                reclamo13.setMontoEstimado(new BigDecimal("4200.00"));
                reclamo13.setEstado(EstadoReclamo.EN_VALIDACION);
                reclamoRepository.save(reclamo13);
                
                Reclamo reclamo14 = new Reclamo();
                reclamo14.setPoliza(poliza8);
                reclamo14.setUsuario(cliente);
                reclamo14.setFechaCreacion(LocalDateTime.of(2024, 12, 20, 10, 15));
                reclamo14.setFechaSiniestro(LocalDateTime.of(2024, 12, 19, 15, 0));
                reclamo14.setDescripcion("Daño por caída de árbol en zona de San Salvador. Techo del vehículo afectado.");
                reclamo14.setMontoEstimado(new BigDecimal("2800.00"));
                reclamo14.setEstado(EstadoReclamo.REGISTRADO);
                reclamoRepository.save(reclamo14);
            }
            
            log.info("Se crearon reclamos de ejemplo.");
            }
        }
        
        // Crear pólizas y reclamos para Cliente A (enero, febrero, marzo)
        Usuario clienteA = usuarioRepository.findByEmail("clienteA@example.com").orElse(null);
        if (clienteA != null && polizaRepository.count() < 15) {
            // Verificar si ya existe la póliza AS-2024-A01
            boolean polizaAExists = polizaRepository.findByNumeroPoliza("AS-2024-A01").stream()
                .anyMatch(p -> p.getNumeroPoliza().equals("AS-2024-A01"));
            
            if (!polizaAExists) {
            List<Aseguradora> suizaList = aseguradoraRepository.findByNombre("Aseguradora Suiza");
            Aseguradora suiza = suizaList.isEmpty() ? null : suizaList.get(0);
            
            if (suiza != null) {
                Poliza polizaA1 = new Poliza();
                polizaA1.setNumeroPoliza("AS-2024-A01");
                polizaA1.setTipo("AUTO");
                polizaA1.setFechaInicio(LocalDate.of(2024, 1, 1));
                polizaA1.setFechaFin(LocalDate.of(2024, 12, 31));
                polizaA1.setCoberturas("Robo, Responsabilidad Civil, Gastos Médicos");
                polizaA1.setAseguradora(suiza);
                polizaRepository.save(polizaA1);
                
                // Reclamos de Cliente A en enero, febrero, marzo
                Reclamo reclamoA1 = new Reclamo();
                reclamoA1.setPoliza(polizaA1);
                reclamoA1.setUsuario(clienteA);
                reclamoA1.setFechaCreacion(LocalDateTime.of(2024, 1, 15, 10, 30));
                reclamoA1.setFechaSiniestro(LocalDateTime.of(2024, 1, 14, 15, 45));
                reclamoA1.setDescripcion("Colisión en zona norte, San Salvador. Daño en paragolpes.");
                reclamoA1.setMontoEstimado(new BigDecimal("1800.00"));
                reclamoA1.setEstado(EstadoReclamo.APROBADO);
                reclamoRepository.save(reclamoA1);
                
                Reclamo reclamoA2 = new Reclamo();
                reclamoA2.setPoliza(polizaA1);
                reclamoA2.setUsuario(clienteA);
                reclamoA2.setFechaCreacion(LocalDateTime.of(2024, 2, 20, 14, 15));
                reclamoA2.setFechaSiniestro(LocalDateTime.of(2024, 2, 19, 8, 30));
                reclamoA2.setDescripcion("Robo de espejos laterales en Santa Tecla.");
                reclamoA2.setMontoEstimado(new BigDecimal("350.00"));
                reclamoA2.setEstado(EstadoReclamo.PAGADO);
                reclamoRepository.save(reclamoA2);
                
                Reclamo reclamoA3 = new Reclamo();
                reclamoA3.setPoliza(polizaA1);
                reclamoA3.setUsuario(clienteA);
                reclamoA3.setFechaCreacion(LocalDateTime.of(2024, 3, 10, 9, 15));
                reclamoA3.setFechaSiniestro(LocalDateTime.of(2024, 3, 9, 16, 20));
                reclamoA3.setDescripcion("Choque en semáforo de San Salvador.");
                reclamoA3.setMontoEstimado(new BigDecimal("2200.00"));
                reclamoA3.setEstado(EstadoReclamo.EN_VALIDACION);
                reclamoRepository.save(reclamoA3);

                // Additional claims for Cliente A in October and November
                Reclamo reclamoA4 = new Reclamo();
                reclamoA4.setPoliza(polizaA1);
                reclamoA4.setUsuario(clienteA);
                reclamoA4.setFechaCreacion(LocalDateTime.of(2024, 10, 5, 16, 45));
                reclamoA4.setFechaSiniestro(LocalDateTime.of(2024, 10, 4, 10, 30));
                reclamoA4.setDescripcion("Daño en parabrisas por piedra en carretera a Santa Ana.");
                reclamoA4.setMontoEstimado(new BigDecimal("450.00"));
                reclamoA4.setEstado(EstadoReclamo.APROBADO);
                reclamoRepository.save(reclamoA4);

                Reclamo reclamoA5 = new Reclamo();
                reclamoA5.setPoliza(polizaA1);
                reclamoA5.setUsuario(clienteA);
                reclamoA5.setFechaCreacion(LocalDateTime.of(2024, 11, 18, 11, 20));
                reclamoA5.setFechaSiniestro(LocalDateTime.of(2024, 11, 17, 14, 15));
                reclamoA5.setDescripcion("Colisión en estacionamiento de centro comercial.");
                reclamoA5.setMontoEstimado(new BigDecimal("950.00"));
                reclamoA5.setEstado(EstadoReclamo.REGISTRADO);
                reclamoRepository.save(reclamoA5);
            }
            log.info("Se crearon pólizas y reclamos para Cliente A.");
            }
        }
        
        // Crear pólizas y reclamos para Cliente B (abril, mayo, junio)
        Usuario clienteB = usuarioRepository.findByEmail("clienteB@example.com").orElse(null);
        if (clienteB != null && polizaRepository.count() < 20) {
            // Verificar si ya existe la póliza ER-2024-B01
            boolean polizaBExists = polizaRepository.findByNumeroPoliza("ER-2024-B01").stream()
                .anyMatch(p -> p.getNumeroPoliza().equals("ER-2024-B01"));
            
            if (!polizaBExists) {
            List<Aseguradora> robleList = aseguradoraRepository.findByNombre("Seguros El Roble");
            Aseguradora roble = robleList.isEmpty() ? null : robleList.get(0);
            
            if (roble != null) {
                Poliza polizaB1 = new Poliza();
                polizaB1.setNumeroPoliza("ER-2024-B01");
                polizaB1.setTipo("AUTO");
                polizaB1.setFechaInicio(LocalDate.of(2024, 1, 1));
                polizaB1.setFechaFin(LocalDate.of(2024, 12, 31));
                polizaB1.setCoberturas("Colisión, Responsabilidad Civil");
                polizaB1.setAseguradora(roble);
                polizaRepository.save(polizaB1);
                
                // Reclamos de Cliente B en abril, mayo, junio
                Reclamo reclamoB1 = new Reclamo();
                reclamoB1.setPoliza(polizaB1);
                reclamoB1.setUsuario(clienteB);
                reclamoB1.setFechaCreacion(LocalDateTime.of(2024, 4, 12, 11, 20));
                reclamoB1.setFechaSiniestro(LocalDateTime.of(2024, 4, 11, 8, 15));
                reclamoB1.setDescripcion("Accidente en carretera a La Libertad.");
                reclamoB1.setMontoEstimado(new BigDecimal("3500.00"));
                reclamoB1.setEstado(EstadoReclamo.APROBADO);
                reclamoRepository.save(reclamoB1);
                
                Reclamo reclamoB2 = new Reclamo();
                reclamoB2.setPoliza(polizaB1);
                reclamoB2.setUsuario(clienteB);
                reclamoB2.setFechaCreacion(LocalDateTime.of(2024, 5, 18, 8, 45));
                reclamoB2.setFechaSiniestro(LocalDateTime.of(2024, 5, 17, 14, 30));
                reclamoB2.setDescripcion("Daño por granizo en zona de Santa Ana.");
                reclamoB2.setMontoEstimado(new BigDecimal("1200.00"));
                reclamoB2.setEstado(EstadoReclamo.PAGADO);
                reclamoRepository.save(reclamoB2);
                
                Reclamo reclamoB3 = new Reclamo();
                reclamoB3.setPoliza(polizaB1);
                reclamoB3.setUsuario(clienteB);
                reclamoB3.setFechaCreacion(LocalDateTime.of(2024, 6, 22, 15, 30));
                reclamoB3.setFechaSiniestro(LocalDateTime.of(2024, 6, 21, 9, 45));
                reclamoB3.setDescripcion("Choque en intersección de San Salvador.");
                reclamoB3.setMontoEstimado(new BigDecimal("2800.00"));
                reclamoB3.setEstado(EstadoReclamo.REGISTRADO);
                reclamoRepository.save(reclamoB3);

                // Additional claims for Cliente B in July and December
                Reclamo reclamoB4 = new Reclamo();
                reclamoB4.setPoliza(polizaB1);
                reclamoB4.setUsuario(clienteB);
                reclamoB4.setFechaCreacion(LocalDateTime.of(2024, 7, 15, 9, 30));
                reclamoB4.setFechaSiniestro(LocalDateTime.of(2024, 7, 14, 16, 20));
                reclamoB4.setDescripcion("Atropello en zona de Santa Tecla.");
                reclamoB4.setMontoEstimado(new BigDecimal("1500.00"));
                reclamoB4.setEstado(EstadoReclamo.APROBADO);
                reclamoRepository.save(reclamoB4);

                Reclamo reclamoB5 = new Reclamo();
                reclamoB5.setPoliza(polizaB1);
                reclamoB5.setUsuario(clienteB);
                reclamoB5.setFechaCreacion(LocalDateTime.of(2024, 12, 20, 14, 0));
                reclamoB5.setFechaSiniestro(LocalDateTime.of(2024, 12, 19, 11, 45));
                reclamoB5.setDescripcion("Daño por lluvia torrencial en zona de San Salvador.");
                reclamoB5.setMontoEstimado(new BigDecimal("800.00"));
                reclamoB5.setEstado(EstadoReclamo.EN_VALIDACION);
                reclamoRepository.save(reclamoB5);
            }
            log.info("Se crearon pólizas y reclamos para Cliente B.");
            }
        }
        
        // Crear pólizas y reclamos para Cliente C (julio, agosto, septiembre)
        Usuario clienteC = usuarioRepository.findByEmail("clienteC@example.com").orElse(null);
        if (clienteC != null && polizaRepository.count() < 25) {
            // Verificar si ya existe la póliza AT-2024-C01
            boolean polizaCExists = polizaRepository.findByNumeroPoliza("AT-2024-C01").stream()
                .anyMatch(p -> p.getNumeroPoliza().equals("AT-2024-C01"));
            
            if (!polizaCExists) {
            List<Aseguradora> atlantidaList = aseguradoraRepository.findByNombre("Seguros Atlántida");
            Aseguradora atlantida = atlantidaList.isEmpty() ? null : atlantidaList.get(0);
            
            if (atlantida != null) {
                Poliza polizaC1 = new Poliza();
                polizaC1.setNumeroPoliza("AT-2024-C01");
                polizaC1.setTipo("AUTO");
                polizaC1.setFechaInicio(LocalDate.of(2024, 1, 1));
                polizaC1.setFechaFin(LocalDate.of(2024, 12, 31));
                polizaC1.setCoberturas("Todo riesgo, Asistencia en carretera");
                polizaC1.setAseguradora(atlantida);
                polizaRepository.save(polizaC1);
                
                // Reclamos de Cliente C en julio, agosto, septiembre
                Reclamo reclamoC1 = new Reclamo();
                reclamoC1.setPoliza(polizaC1);
                reclamoC1.setUsuario(clienteC);
                reclamoC1.setFechaCreacion(LocalDateTime.of(2024, 7, 8, 13, 10));
                reclamoC1.setFechaSiniestro(LocalDateTime.of(2024, 7, 7, 17, 45));
                reclamoC1.setDescripcion("Volcadura en carretera Panamericana.");
                reclamoC1.setMontoEstimado(new BigDecimal("6500.00"));
                reclamoC1.setEstado(EstadoReclamo.APROBADO);
                reclamoRepository.save(reclamoC1);
                
                Reclamo reclamoC2 = new Reclamo();
                reclamoC2.setPoliza(polizaC1);
                reclamoC2.setUsuario(clienteC);
                reclamoC2.setFechaCreacion(LocalDateTime.of(2024, 8, 14, 10, 0));
                reclamoC2.setFechaSiniestro(LocalDateTime.of(2024, 8, 13, 11, 30));
                reclamoC2.setDescripcion("Hospitalización por accidente en San Salvador.");
                reclamoC2.setMontoEstimado(new BigDecimal("4200.00"));
                reclamoC2.setEstado(EstadoReclamo.EN_VALIDACION);
                reclamoRepository.save(reclamoC2);
                
                Reclamo reclamoC3 = new Reclamo();
                reclamoC3.setPoliza(polizaC1);
                reclamoC3.setUsuario(clienteC);
                reclamoC3.setFechaCreacion(LocalDateTime.of(2024, 9, 25, 15, 30));
                reclamoC3.setFechaSiniestro(LocalDateTime.of(2024, 9, 24, 18, 15));
                reclamoC3.setDescripcion("Daño por granizo en zona de San Salvador.");
                reclamoC3.setMontoEstimado(new BigDecimal("1800.00"));
                reclamoC3.setEstado(EstadoReclamo.PAGADO);
                reclamoRepository.save(reclamoC3);

                // Additional claims for Cliente C in October and December
                Reclamo reclamoC4 = new Reclamo();
                reclamoC4.setPoliza(polizaC1);
                reclamoC4.setUsuario(clienteC);
                reclamoC4.setFechaCreacion(LocalDateTime.of(2024, 10, 12, 10, 15));
                reclamoC4.setFechaSiniestro(LocalDateTime.of(2024, 10, 11, 14, 30));
                reclamoC4.setDescripcion("Choque en carretera a La Unión.");
                reclamoC4.setMontoEstimado(new BigDecimal("3200.00"));
                reclamoC4.setEstado(EstadoReclamo.APROBADO);
                reclamoRepository.save(reclamoC4);

                Reclamo reclamoC5 = new Reclamo();
                reclamoC5.setPoliza(polizaC1);
                reclamoC5.setUsuario(clienteC);
                reclamoC5.setFechaCreacion(LocalDateTime.of(2024, 12, 8, 16, 45));
                reclamoC5.setFechaSiniestro(LocalDateTime.of(2024, 12, 7, 9, 20));
                reclamoC5.setDescripcion("Incendio parcial del vehículo en zona de San Salvador.");
                reclamoC5.setMontoEstimado(new BigDecimal("5500.00"));
                reclamoC5.setEstado(EstadoReclamo.EN_VALIDACION);
                reclamoRepository.save(reclamoC5);
            }
            log.info("Se crearon pólizas y reclamos para Cliente C.");
            }
        }
    }
}