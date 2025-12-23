package com.example.SGE.Controller;

import com.example.SGE.Entity.*;
import com.example.SGE.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Controller para Relatórios
 * 
 * Endpoints:
 * - GET /api/reports/summary - Resumo geral
 * - GET /api/reports/inspections - Relatório de inspeções
 * - GET /api/reports/maintenance - Relatório de manutenção
 * - GET /api/reports/alerts - Relatório de alertas
 * - GET /api/reports/equipment - Relatório de equipamentos
 * - POST /api/reports/export - Exportar e enviar para webhook n8n
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {
    
    @Autowired
    private InspectionRepository inspectionRepository;
    
    @Autowired
    private MaintenanceRepository maintenanceRepository;
    
    @Autowired
    private AlertRepository alertRepository;
    
    @Autowired
    private ExtinguisherRepository extinguisherRepository;
    
    @Autowired
    private UnitRepository unitRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${webhook.n8n.url:}")
    private String n8nWebhookUrl;
    
    // ============================================================================
    // RELATÓRIOS - Reports
    // ============================================================================
    
    /**
     * Resumo geral do sistema
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getSummary() {
        try {
            long totalExtinguishers = extinguisherRepository.count();
            long totalUnits = unitRepository.count();
            long totalInspections = inspectionRepository.count();
            long totalMaintenances = maintenanceRepository.count();
            
            Map<String, Object> summary = Map.ofEntries(
                Map.entry("timestamp", LocalDateTime.now()),
                Map.entry("totalExtinguishers", totalExtinguishers),
                Map.entry("totalUnits", totalUnits),
                Map.entry("totalInspections", totalInspections),
                Map.entry("totalMaintenances", totalMaintenances),
                Map.entry("healthStatus", "SAUDÁVEL")
            );
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erro ao gerar resumo"));
        }
    }
    
    /**
     * Relatório de inspeções
     */
    @GetMapping("/inspections")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getInspectionsReport(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size,
        @RequestParam(required = false) String status
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<?> inspections = inspectionRepository.findAll(pageable);
            
            Map<String, Object> report = Map.ofEntries(
                Map.entry("title", "Relatório de Inspeções"),
                Map.entry("generatedAt", LocalDateTime.now()),
                Map.entry("totalRecords", inspections.getTotalElements()),
                Map.entry("data", inspections.getContent())
            );
            
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erro ao gerar relatório de inspeções"));
        }
    }
    
    /**
     * Relatório de manutenção
     */
    @GetMapping("/maintenance")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getMaintenanceReport(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<?> maintenance = maintenanceRepository.findAll(pageable);
            
            Map<String, Object> report = Map.ofEntries(
                Map.entry("title", "Relatório de Manutenção"),
                Map.entry("generatedAt", LocalDateTime.now()),
                Map.entry("totalRecords", maintenance.getTotalElements()),
                Map.entry("data", maintenance.getContent())
            );
            
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erro ao gerar relatório de manutenção"));
        }
    }
    
    /**
     * Relatório de alertas
     */
    @GetMapping("/alerts")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getAlertsReport(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<?> alerts = alertRepository.findAll(pageable);
            
            Map<String, Object> report = Map.ofEntries(
                Map.entry("title", "Relatório de Alertas"),
                Map.entry("generatedAt", LocalDateTime.now()),
                Map.entry("totalRecords", alerts.getTotalElements()),
                Map.entry("data", alerts.getContent())
            );
            
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erro ao gerar relatório de alertas"));
        }
    }
    
    /**
     * Relatório de equipamentos
     */
    @GetMapping("/equipment")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getEquipmentReport(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<?> equipment = extinguisherRepository.findAll(pageable);
            
            Map<String, Object> report = Map.ofEntries(
                Map.entry("title", "Relatório de Equipamentos"),
                Map.entry("generatedAt", LocalDateTime.now()),
                Map.entry("totalRecords", equipment.getTotalElements()),
                Map.entry("data", equipment.getContent())
            );
            
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erro ao gerar relatório de equipamentos"));
        }
    }
    
    // ============================================================================
    // EXPORTAR RELATÓRIO - Export Report para webhook n8n
    // ============================================================================
    
    /**
     * Exportar relatório e enviar para webhook n8n
     * POST /api/reports/export
     * 
     * Body:
     * {
     *   "reportType": "summary|inspections|maintenance|alerts|equipment",
     *   "webhookUrl": "https://seu-n8n.com/webhook/seu-endpoint" (opcional - usa variável se não fornecida),
     *   "format": "json|pdf|csv",
     *   "filters": {
     *     "dateStart": "2025-01-01",
     *     "dateEnd": "2025-12-31",
     *     "unitId": 1
     *   }
     * }
     */
    @PostMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> exportReport(
        @RequestBody Map<String, Object> exportRequest,
        Authentication auth
    ) {
        try {
            String reportType = (String) exportRequest.get("reportType");
            String webhookUrl = (String) exportRequest.getOrDefault("webhookUrl", n8nWebhookUrl);
            String format = (String) exportRequest.getOrDefault("format", "json");
            Map<String, Object> filters = (Map<String, Object>) exportRequest.get("filters");
            
            // Validar entrada
            if (reportType == null || reportType.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("error", "reportType é obrigatório"));
            }
            
            if (!isValidReportType(reportType)) {
                return ResponseEntity.status(400).body(Map.of("error", "Tipo de relatório inválido: " + reportType));
            }
            
            if (webhookUrl == null || webhookUrl.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("error", "Webhook URL não configurada"));
            }
            
            // Gerar conteúdo do relatório
            Map<String, Object> reportData = generateReportData(reportType, filters);
            
            // Preparar payload para n8n
            Map<String, Object> payload = Map.ofEntries(
                Map.entry("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)),
                Map.entry("reportType", reportType),
                Map.entry("format", format),
                Map.entry("user", auth.getName()),
                Map.entry("filters", filters != null ? filters : Map.of()),
                Map.entry("data", reportData)
            );
            
            // Enviar para webhook n8n de forma assíncrona
            sendToWebhookAsync(webhookUrl, payload);
            
            return ResponseEntity.ok(Map.of(
                "message", "Relatório enviado com sucesso",
                "reportType", reportType,
                "timestamp", LocalDateTime.now(),
                "webhookUrl", webhookUrl
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "Erro ao exportar relatório",
                "details", e.getMessage()
            ));
        }
    }
    
    /**
     * Testar conexão com webhook n8n
     * GET /api/reports/webhook/test
     */
    @GetMapping("/webhook/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> testWebhook(@RequestParam(required = false) String webhookUrl) {
        try {
            String url = webhookUrl != null ? webhookUrl : n8nWebhookUrl;
            
            if (url == null || url.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("error", "Webhook URL não configurada"));
            }
            
            Map<String, Object> testPayload = Map.of(
                "test", true,
                "timestamp", LocalDateTime.now(),
                "message", "Teste de conexão com webhook n8n"
            );
            
            sendToWebhookAsync(url, testPayload);
            
            return ResponseEntity.ok(Map.of(
                "message", "Teste de webhook enviado",
                "url", url,
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "Erro ao testar webhook",
                "details", e.getMessage()
            ));
        }
    }
    
    // ============================================================================
    // MÉTODOS AUXILIARES
    // ============================================================================
    
    /**
     * Validar tipo de relatório
     */
    private boolean isValidReportType(String type) {
        return Arrays.asList("summary", "inspections", "maintenance", "alerts", "equipment").contains(type);
    }
    
    /**
     * Gerar dados do relatório baseado no tipo
     */
    private Map<String, Object> generateReportData(String type, Map<String, Object> filters) {
        return switch (type) {
            case "summary" -> generateSummaryData();
            case "inspections" -> generateInspectionsData();
            case "maintenance" -> generateMaintenanceData();
            case "alerts" -> generateAlertsData();
            case "equipment" -> generateEquipmentData();
            default -> new HashMap<>();
        };
    }
    
    private Map<String, Object> generateSummaryData() {
        return Map.ofEntries(
            Map.entry("type", "summary"),
            Map.entry("generatedAt", LocalDateTime.now()),
            Map.entry("statistics", Map.ofEntries(
                Map.entry("totalExtinguishers", extinguisherRepository.count()),
                Map.entry("totalUnits", unitRepository.count()),
                Map.entry("totalInspections", inspectionRepository.count()),
                Map.entry("totalMaintenances", maintenanceRepository.count())
            ))
        );
    }
    
    private Map<String, Object> generateInspectionsData() {
        List<?> inspections = inspectionRepository.findAll();
        return Map.of(
            "type", "inspections",
            "generatedAt", LocalDateTime.now(),
            "count", inspections.size(),
            "data", inspections
        );
    }
    
    private Map<String, Object> generateMaintenanceData() {
        List<?> maintenances = maintenanceRepository.findAll();
        return Map.of(
            "type", "maintenance",
            "generatedAt", LocalDateTime.now(),
            "count", maintenances.size(),
            "data", maintenances
        );
    }
    
    private Map<String, Object> generateAlertsData() {
        List<?> alerts = alertRepository.findAll();
        return Map.of(
            "type", "alerts",
            "generatedAt", LocalDateTime.now(),
            "count", alerts.size(),
            "data", alerts
        );
    }
    
    private Map<String, Object> generateEquipmentData() {
        List<?> equipment = extinguisherRepository.findAll();
        return Map.of(
            "type", "equipment",
            "generatedAt", LocalDateTime.now(),
            "count", equipment.size(),
            "data", equipment
        );
    }
    
    /**
     * Enviar payload para webhook n8n de forma assíncrona
     */
    private void sendToWebhookAsync(String webhookUrl, Map<String, Object> payload) {
        new Thread(() -> {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
                
                // Enviar POST request
                restTemplate.postForEntity(webhookUrl, entity, String.class);
                
                System.out.println("✓ Webhook enviado para: " + webhookUrl);
            } catch (Exception e) {
                System.err.println("✗ Erro ao enviar webhook: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}
