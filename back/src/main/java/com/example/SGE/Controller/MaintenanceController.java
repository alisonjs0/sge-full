package com.example.SGE.Controller;

import com.example.SGE.Entity.ExtinguisherEntity;
import com.example.SGE.Entity.MaintenanceEntity;
import com.example.SGE.Entity.UserEntity;
import com.example.SGE.Repository.ExtinguisherRepository;
import com.example.SGE.Repository.MaintenanceRepository;
import com.example.SGE.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/maintenance")
@CrossOrigin(origins = "*")
public class MaintenanceController {

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private ExtinguisherRepository extinguisherRepository;

    @Autowired
    private UserRepository userRepository;

    // Listar todas as manutenções
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<MaintenanceEntity>> getAllMaintenances() {
        List<MaintenanceEntity> maintenances = maintenanceRepository.findAll();
        return ResponseEntity.ok(maintenances);
    }

    // Buscar manutenção por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<MaintenanceEntity> getMaintenanceById(@PathVariable Long id) {
        Optional<MaintenanceEntity> maintenance = maintenanceRepository.findById(id);
        return maintenance.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Criar nova manutenção
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> createMaintenance(@RequestBody Map<String, Object> maintenanceData) {
        try {
            MaintenanceEntity maintenance = new MaintenanceEntity();
            
            // Extintor
            if (maintenanceData.containsKey("extintorId")) {
                Long extintorId = Long.valueOf(maintenanceData.get("extintorId").toString());
                Optional<ExtinguisherEntity> extintor = extinguisherRepository.findById(extintorId);
                if (extintor.isPresent()) {
                    maintenance.setExtinguisher(extintor.get());
                } else {
                    return ResponseEntity.badRequest().body("Extintor não encontrado");
                }
            }

            // Responsável
            if (maintenanceData.containsKey("responsavelId")) {
                Long responsavelId = Long.valueOf(maintenanceData.get("responsavelId").toString());
                Optional<UserEntity> responsavel = userRepository.findById(responsavelId);
                if (responsavel.isPresent()) {
                    maintenance.setResponsavel(responsavel.get());
                } else {
                    return ResponseEntity.badRequest().body("Responsável não encontrado");
                }
            }

            // Campos simples
            if (maintenanceData.containsKey("tipo")) {
                maintenance.setTipo(maintenanceData.get("tipo").toString());
            }
            if (maintenanceData.containsKey("status")) {
                maintenance.setStatus(maintenanceData.get("status").toString());
            }
            if (maintenanceData.containsKey("dataAgendada") && maintenanceData.get("dataAgendada") != null) {
                maintenance.setDataAgendada(java.time.LocalDateTime.parse(maintenanceData.get("dataAgendada").toString()));
            }
            if (maintenanceData.containsKey("dataRealizada") && maintenanceData.get("dataRealizada") != null) {
                maintenance.setDataRealizada(java.time.LocalDateTime.parse(maintenanceData.get("dataRealizada").toString()));
            }
            if (maintenanceData.containsKey("descricao")) {
                maintenance.setDescricao(maintenanceData.get("descricao").toString());
            }
            if (maintenanceData.containsKey("custo") && maintenanceData.get("custo") != null) {
                maintenance.setCusto(Double.valueOf(maintenanceData.get("custo").toString()));
            }
            if (maintenanceData.containsKey("observacoes")) {
                maintenance.setObservacoes(maintenanceData.get("observacoes").toString());
            }
            if (maintenanceData.containsKey("proximaManutencao") && maintenanceData.get("proximaManutencao") != null) {
                maintenance.setProximaManutencao(java.time.LocalDateTime.parse(maintenanceData.get("proximaManutencao").toString()));
            }

            MaintenanceEntity savedMaintenance = maintenanceRepository.save(maintenance);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMaintenance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar manutenção: " + e.getMessage());
        }
    }

    // Atualizar manutenção
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> updateMaintenance(@PathVariable Long id, @RequestBody Map<String, Object> maintenanceData) {
        try {
            Optional<MaintenanceEntity> existingMaintenance = maintenanceRepository.findById(id);
            
            if (!existingMaintenance.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            MaintenanceEntity maintenance = existingMaintenance.get();

            // Extintor
            if (maintenanceData.containsKey("extintorId")) {
                Long extintorId = Long.valueOf(maintenanceData.get("extintorId").toString());
                Optional<ExtinguisherEntity> extintor = extinguisherRepository.findById(extintorId);
                if (extintor.isPresent()) {
                    maintenance.setExtinguisher(extintor.get());
                }
            }

            // Responsável
            if (maintenanceData.containsKey("responsavelId")) {
                Long responsavelId = Long.valueOf(maintenanceData.get("responsavelId").toString());
                Optional<UserEntity> responsavel = userRepository.findById(responsavelId);
                if (responsavel.isPresent()) {
                    maintenance.setResponsavel(responsavel.get());
                }
            }

            // Campos simples
            if (maintenanceData.containsKey("tipo")) {
                maintenance.setTipo(maintenanceData.get("tipo").toString());
            }
            if (maintenanceData.containsKey("status")) {
                maintenance.setStatus(maintenanceData.get("status").toString());
            }
            if (maintenanceData.containsKey("dataAgendada")) {
                if (maintenanceData.get("dataAgendada") != null) {
                    maintenance.setDataAgendada(java.time.LocalDateTime.parse(maintenanceData.get("dataAgendada").toString()));
                } else {
                    maintenance.setDataAgendada(null);
                }
            }
            if (maintenanceData.containsKey("dataRealizada")) {
                if (maintenanceData.get("dataRealizada") != null) {
                    maintenance.setDataRealizada(java.time.LocalDateTime.parse(maintenanceData.get("dataRealizada").toString()));
                } else {
                    maintenance.setDataRealizada(null);
                }
            }
            if (maintenanceData.containsKey("descricao")) {
                maintenance.setDescricao(maintenanceData.get("descricao").toString());
            }
            if (maintenanceData.containsKey("custo")) {
                if (maintenanceData.get("custo") != null) {
                    maintenance.setCusto(Double.valueOf(maintenanceData.get("custo").toString()));
                } else {
                    maintenance.setCusto(null);
                }
            }
            if (maintenanceData.containsKey("observacoes")) {
                maintenance.setObservacoes(maintenanceData.get("observacoes").toString());
            }
            if (maintenanceData.containsKey("proximaManutencao")) {
                if (maintenanceData.get("proximaManutencao") != null) {
                    maintenance.setProximaManutencao(java.time.LocalDateTime.parse(maintenanceData.get("proximaManutencao").toString()));
                } else {
                    maintenance.setProximaManutencao(null);
                }
            }

            MaintenanceEntity updatedMaintenance = maintenanceRepository.save(maintenance);
            return ResponseEntity.ok(updatedMaintenance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar manutenção: " + e.getMessage());
        }
    }

    // Deletar manutenção
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMaintenance(@PathVariable Long id) {
        try {
            if (!maintenanceRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            maintenanceRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao deletar manutenção: " + e.getMessage());
        }
    }

    // Buscar manutenções por extintor
    @GetMapping("/extintor/{extintorId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<MaintenanceEntity>> getMaintenancesByExtinguisher(@PathVariable Long extintorId) {
        List<MaintenanceEntity> maintenances = maintenanceRepository.findByExtinguisherId(extintorId);
        return ResponseEntity.ok(maintenances);
    }

    // Buscar manutenções por responsável
    @GetMapping("/responsavel/{responsavelId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<MaintenanceEntity>> getMaintenancesByResponsavel(@PathVariable Long responsavelId) {
        List<MaintenanceEntity> maintenances = maintenanceRepository.findByResponsavelId(responsavelId);
        return ResponseEntity.ok(maintenances);
    }

    // Buscar manutenções por status
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<MaintenanceEntity>> getMaintenancesByStatus(@PathVariable String status) {
        List<MaintenanceEntity> maintenances = maintenanceRepository.findByStatus(status);
        return ResponseEntity.ok(maintenances);
    }

    // Buscar manutenções por tipo
    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<MaintenanceEntity>> getMaintenancesByTipo(@PathVariable String tipo) {
        List<MaintenanceEntity> maintenances = maintenanceRepository.findByTipo(tipo);
        return ResponseEntity.ok(maintenances);
    }
}
