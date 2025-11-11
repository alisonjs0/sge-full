package com.example.SGE.Controller;

import com.example.SGE.Entity.InspectionEntity;
import com.example.SGE.Repository.InspectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/inspections")
public class InspectionController {
    
    @Autowired
    private final InspectionRepository inspectionRepository;

    public InspectionController(InspectionRepository inspectionRepository) {
        this.inspectionRepository = inspectionRepository;
    }

    @PostMapping
    public InspectionEntity createInspection(@RequestBody InspectionEntity inspectionEntity) {
        System.out.println("=== Entrando no método POST createInspection ===");
        System.out.println("Dados recebidos: " + inspectionEntity);
        return inspectionRepository.save(inspectionEntity);
    }

    @GetMapping
    public List<InspectionEntity> getAllInspections() {
        return inspectionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InspectionEntity> getInspection(@PathVariable long id) {
        return inspectionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delInspection(@PathVariable Long id) {
        Optional<InspectionEntity> inspectionOptional = inspectionRepository.findById(id);

        if (inspectionOptional.isPresent()) {
            inspectionRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<InspectionEntity> updateInspection(@PathVariable Long id, @RequestBody InspectionEntity updateInspection) {
        System.out.println("=== Entrando no método PUT updateInspection ===");
        System.out.println("ID: " + id + " | Dados recebidos: " + updateInspection);
        
        Optional<InspectionEntity> existingInspectionOptional = inspectionRepository.findById(id);

        if (existingInspectionOptional.isPresent()) {
            InspectionEntity existingInspection = existingInspectionOptional.get();

            existingInspection.setInspectionDate(updateInspection.getInspectionDate());
            existingInspection.setInspectionAuthor(updateInspection.getInspectionAuthor());
            existingInspection.setExtinguisher(updateInspection.getExtinguisher());
            existingInspection.setManometro(updateInspection.getManometro());
            existingInspection.setSeal(updateInspection.getSeal());
            existingInspection.setRotulo(updateInspection.getRotulo());
            existingInspection.setDamages(updateInspection.getDamages());
            existingInspection.setObstructions(updateInspection.getObstructions());
            existingInspection.setSinalizacao(updateInspection.getSinalizacao());
            existingInspection.setSuporteFixacao(updateInspection.getSuporteFixacao());
            existingInspection.setObservations(updateInspection.getObservations());
            existingInspection.setNextInspectionDate(updateInspection.getNextInspectionDate());

            InspectionEntity savedInspection = inspectionRepository.save(existingInspection);
            return ResponseEntity.ok(savedInspection);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}
