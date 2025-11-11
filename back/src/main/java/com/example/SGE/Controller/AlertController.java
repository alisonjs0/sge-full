package com.example.SGE.Controller;

import com.example.SGE.Entity.AlertEntity;
import com.example.SGE.Entity.ExtinguisherEntity;
import com.example.SGE.Entity.UserEntity;
import com.example.SGE.Repository.AlertRepository;
import com.example.SGE.Repository.ExtinguisherRepository;
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
@RequestMapping("/alert")
@CrossOrigin(origins = "*")
public class AlertController {
    
    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private ExtinguisherRepository extinguisherRepository;

    @Autowired
    private UserRepository userRepository;

    // Listar todos os alertas
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<AlertEntity>> getAllAlerts() {
        List<AlertEntity> alerts = alertRepository.findAll();
        return ResponseEntity.ok(alerts);
    }

    // Buscar alerta por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AlertEntity> getAlertById(@PathVariable Long id) {
        Optional<AlertEntity> alert = alertRepository.findById(id);
        return alert.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Criar novo alerta
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> createAlert(@RequestBody Map<String, Object> alertData) {
        try {
            AlertEntity alert = new AlertEntity();
            
            // Campos obrigatórios
            if (alertData.containsKey("tipoAlerta") && alertData.get("tipoAlerta") != null) {
                alert.setTipoAlerta(alertData.get("tipoAlerta").toString());
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Tipo de alerta é obrigatório"));
            }

            if (alertData.containsKey("mensagem") && alertData.get("mensagem") != null) {
                alert.setMensagem(alertData.get("mensagem").toString());
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Mensagem é obrigatória"));
            }

            // Campos opcionais com valores padrão
            if (alertData.containsKey("prioridade") && alertData.get("prioridade") != null) {
                alert.setPrioridade(alertData.get("prioridade").toString());
            } else {
                alert.setPrioridade("media");
            }

            if (alertData.containsKey("status") && alertData.get("status") != null) {
                alert.setStatus(alertData.get("status").toString());
            } else {
                alert.setStatus("pendente");
            }

            // Extintor (opcional)
            if (alertData.containsKey("extintorId") && alertData.get("extintorId") != null) {
                try {
                    Long extintorId = Long.valueOf(alertData.get("extintorId").toString());
                    Optional<ExtinguisherEntity> extintor = extinguisherRepository.findById(extintorId);
                    if (extintor.isPresent()) {
                        alert.setExtintor(extintor.get());
                    }
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body(Map.of("error", "ID do extintor inválido"));
                }
            }

            // Usuário (opcional)
            if (alertData.containsKey("usuarioId") && alertData.get("usuarioId") != null) {
                try {
                    Long usuarioId = Long.valueOf(alertData.get("usuarioId").toString());
                    Optional<UserEntity> usuario = userRepository.findById(usuarioId);
                    if (usuario.isPresent()) {
                        alert.setUsuario(usuario.get());
                    }
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body(Map.of("error", "ID do usuário inválido"));
                }
            }

            AlertEntity savedAlert = alertRepository.save(alert);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAlert);
        } catch (Exception e) {
            e.printStackTrace(); // Para debug
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao criar alerta: " + e.getMessage()));
        }
    }

    // Atualizar alerta
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> updateAlert(@PathVariable Long id, @RequestBody Map<String, Object> alertData) {
        try {
            Optional<AlertEntity> existingAlertOptional = alertRepository.findById(id);
            
            if (!existingAlertOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            AlertEntity alert = existingAlertOptional.get();

            // Atualiza campos
            if (alertData.containsKey("tipoAlerta")) {
                alert.setTipoAlerta(alertData.get("tipoAlerta").toString());
            }

            if (alertData.containsKey("mensagem")) {
                alert.setMensagem(alertData.get("mensagem").toString());
            }

            if (alertData.containsKey("prioridade")) {
                alert.setPrioridade(alertData.get("prioridade").toString());
            }

            if (alertData.containsKey("status")) {
                alert.setStatus(alertData.get("status").toString());
            }

            // Extintor
            if (alertData.containsKey("extintorId")) {
                if (alertData.get("extintorId") != null && !alertData.get("extintorId").toString().isEmpty()) {
                    Long extintorId = Long.valueOf(alertData.get("extintorId").toString());
                    Optional<ExtinguisherEntity> extintor = extinguisherRepository.findById(extintorId);
                    extintor.ifPresent(alert::setExtintor);
                } else {
                    alert.setExtintor(null);
                }
            }

            // Usuário
            if (alertData.containsKey("usuarioId")) {
                if (alertData.get("usuarioId") != null && !alertData.get("usuarioId").toString().isEmpty()) {
                    Long usuarioId = Long.valueOf(alertData.get("usuarioId").toString());
                    Optional<UserEntity> usuario = userRepository.findById(usuarioId);
                    usuario.ifPresent(alert::setUsuario);
                } else {
                    alert.setUsuario(null);
                }
            }

            AlertEntity updatedAlert = alertRepository.save(alert);
            return ResponseEntity.ok(updatedAlert);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar alerta: " + e.getMessage());
        }
    }

    // Deletar alerta
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAlert(@PathVariable Long id) {
        try {
            if (!alertRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            alertRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao deletar alerta: " + e.getMessage());
        }
    }

    // Marcar alerta como lido
    @PatchMapping("/{id}/lido")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            Optional<AlertEntity> alertOpt = alertRepository.findById(id);
            if (alertOpt.isPresent()) {
                AlertEntity alert = alertOpt.get();
                alert.setStatus("lido");
                alertRepository.save(alert);
                return ResponseEntity.ok(alert);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao marcar alerta como lido: " + e.getMessage());
        }
    }

    // Marcar alerta como resolvido
    @PatchMapping("/{id}/resolvido")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> markAsResolved(@PathVariable Long id) {
        try {
            Optional<AlertEntity> alertOpt = alertRepository.findById(id);
            if (alertOpt.isPresent()) {
                AlertEntity alert = alertOpt.get();
                alert.setStatus("resolvido");
                alertRepository.save(alert);
                return ResponseEntity.ok(alert);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao marcar alerta como resolvido: " + e.getMessage());
        }
    }

    // Buscar alertas não lidos
    @GetMapping("/nao-lidos")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<AlertEntity>> getUnreadAlerts() {
        List<AlertEntity> alerts = alertRepository.findUnreadAlerts();
        return ResponseEntity.ok(alerts);
    }

    // Buscar alertas por status
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<AlertEntity>> getAlertsByStatus(@PathVariable String status) {
        List<AlertEntity> alerts = alertRepository.findByStatusOrderByDataCriacaoDesc(status);
        return ResponseEntity.ok(alerts);
    }

    // Buscar alertas por prioridade
    @GetMapping("/prioridade/{prioridade}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<AlertEntity>> getAlertsByPriority(@PathVariable String prioridade) {
        List<AlertEntity> alerts = alertRepository.findByPrioridade(prioridade);
        return ResponseEntity.ok(alerts);
    }

    // Buscar alertas por extintor
    @GetMapping("/extintor/{extintorId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<AlertEntity>> getAlertsByExtinguisher(@PathVariable Long extintorId) {
        List<AlertEntity> alerts = alertRepository.findByExtintorId(extintorId);
        return ResponseEntity.ok(alerts);
    }
}
