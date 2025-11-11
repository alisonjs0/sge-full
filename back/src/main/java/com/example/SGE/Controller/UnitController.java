package com.example.SGE.Controller;

import com.example.SGE.Entity.UnitEntity;
import com.example.SGE.Entity.UserEntity;
import com.example.SGE.Repository.UnitRepository;
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
@RequestMapping("/unit")
@CrossOrigin(origins = "*")
public class UnitController {
    
    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private UserRepository userRepository;

    // Listar todas as unidades
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<UnitEntity>> getAllUnits() {
        List<UnitEntity> units = unitRepository.findAll();
        return ResponseEntity.ok(units);
    }

    // Buscar unidade por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UnitEntity> getUnit(@PathVariable Long id) {
        Optional<UnitEntity> unit = unitRepository.findById(id);
        return unit.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Criar nova unidade
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> createUnit(@RequestBody Map<String, Object> unitData) {
        try {
            UnitEntity unit = new UnitEntity();
            
            // Campos obrigatórios
            if (unitData.containsKey("nome")) {
                unit.setNome(unitData.get("nome").toString());
            } else {
                return ResponseEntity.badRequest().body("Nome é obrigatório");
            }

            // Campos opcionais
            if (unitData.containsKey("descricao")) {
                unit.setDescricao(unitData.get("descricao").toString());
            }

            if (unitData.containsKey("ativo")) {
                unit.setAtivo(Boolean.valueOf(unitData.get("ativo").toString()));
            }

            // Responsável
            if (unitData.containsKey("responsavelId")) {
                Long responsavelId = Long.valueOf(unitData.get("responsavelId").toString());
                Optional<UserEntity> responsavel = userRepository.findById(responsavelId);
                if (responsavel.isPresent()) {
                    unit.setResponsavel(responsavel.get());
                } else {
                    return ResponseEntity.badRequest().body("Responsável não encontrado");
                }
            }

            UnitEntity savedUnit = unitRepository.save(unit);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUnit);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar unidade: " + e.getMessage());
        }
    }

    // Atualizar unidade
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> updateUnit(@PathVariable Long id, @RequestBody Map<String, Object> unitData) {
        try {
            Optional<UnitEntity> existingUnitOptional = unitRepository.findById(id);
            
            if (!existingUnitOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            UnitEntity unit = existingUnitOptional.get();

            // Atualiza campos
            if (unitData.containsKey("nome")) {
                unit.setNome(unitData.get("nome").toString());
            }

            if (unitData.containsKey("descricao")) {
                unit.setDescricao(unitData.get("descricao").toString());
            }

            if (unitData.containsKey("ativo")) {
                unit.setAtivo(Boolean.valueOf(unitData.get("ativo").toString()));
            }

            // Responsável
            if (unitData.containsKey("responsavelId")) {
                if (unitData.get("responsavelId") != null && !unitData.get("responsavelId").toString().isEmpty()) {
                    Long responsavelId = Long.valueOf(unitData.get("responsavelId").toString());
                    Optional<UserEntity> responsavel = userRepository.findById(responsavelId);
                    if (responsavel.isPresent()) {
                        unit.setResponsavel(responsavel.get());
                    }
                } else {
                    unit.setResponsavel(null);
                }
            }

            UnitEntity updatedUnit = unitRepository.save(unit);
            return ResponseEntity.ok(updatedUnit);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar unidade: " + e.getMessage());
        }
    }

    // Deletar unidade
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUnit(@PathVariable Long id) {
        try {
            if (!unitRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            unitRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao deletar unidade: " + e.getMessage());
        }
    }

    // Buscar unidades ativas
    @GetMapping("/ativas")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<UnitEntity>> getActiveUnits() {
        List<UnitEntity> units = unitRepository.findByAtivo(true);
        return ResponseEntity.ok(units);
    }
}





