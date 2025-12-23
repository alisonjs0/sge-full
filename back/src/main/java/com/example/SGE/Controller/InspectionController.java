package com.example.SGE.Controller;

import com.example.SGE.Entity.ExtinguisherEntity;
import com.example.SGE.Entity.InspectionEntity;
import com.example.SGE.Repository.ExtinguisherRepository;
import com.example.SGE.Repository.InspectionRepository;
import com.example.SGE.Service.ExtinguisherStatusService;
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

    @Autowired
    private final ExtinguisherRepository extinguisherRepository;

    @Autowired
    private final ExtinguisherStatusService statusService;

    public InspectionController(InspectionRepository inspectionRepository,
                                ExtinguisherRepository extinguisherRepository,
                                ExtinguisherStatusService statusService) {
        this.inspectionRepository = inspectionRepository;
        this.extinguisherRepository = extinguisherRepository;
        this.statusService = statusService;
    }

    // Determina se a inspeção está conforme baseado no checklist
    private boolean isInspectionConforme(InspectionEntity inspection) {
        boolean manometroOk = Boolean.TRUE.equals(inspection.getManometro());
        boolean rotuloOk = Boolean.TRUE.equals(inspection.getRotulo());
        boolean lacreOk = Boolean.TRUE.equals(inspection.getSeal());
        boolean sinalizacaoOk = Boolean.TRUE.equals(inspection.getSinalizacao());
        boolean suporteOk = Boolean.TRUE.equals(inspection.getSuporteFixacao());
        boolean semDanos = !Boolean.TRUE.equals(inspection.getDamages());
        boolean semObstrucoes = !Boolean.TRUE.equals(inspection.getObstructions());

        return manometroOk && rotuloOk && lacreOk && sinalizacaoOk && suporteOk && semDanos && semObstrucoes;
    }

    @PostMapping
    public InspectionEntity createInspection(@RequestBody InspectionEntity inspectionEntity) {
        System.out.println("=== Entrando no método POST createInspection ===");
        System.out.println("Dados recebidos: " + inspectionEntity);
        // Caso uma inspeção seja criada para um extintor, atualizar o status automaticamente
        if (inspectionEntity.getExtinguisher() != null && inspectionEntity.getExtinguisher().getId() != null) {
            Long extinguisherId = inspectionEntity.getExtinguisher().getId();
            Optional<ExtinguisherEntity> extinguisherOpt = extinguisherRepository.findById(extinguisherId);

            if (extinguisherOpt.isPresent()) {
                ExtinguisherEntity extinguisher = extinguisherOpt.get();
                // Usar a entidade gerenciada no vínculo da inspeção
                inspectionEntity.setExtinguisher(extinguisher);

                boolean conforme = isInspectionConforme(inspectionEntity);
                String statusAutomatico = statusService.calculateStatus(extinguisher.getValidade());
                String novoStatus = conforme ? statusAutomatico : "nao_conforme";

                extinguisher.setStatus(novoStatus);
                extinguisherRepository.save(extinguisher);
            }
        }

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

            // Atualizar status do extintor se a inspeção estiver vinculada
            if (existingInspection.getExtinguisher() != null && existingInspection.getExtinguisher().getId() != null) {
                Long extinguisherId = existingInspection.getExtinguisher().getId();
                Optional<ExtinguisherEntity> extinguisherOpt = extinguisherRepository.findById(extinguisherId);

                if (extinguisherOpt.isPresent()) {
                    ExtinguisherEntity extinguisher = extinguisherOpt.get();

                    boolean conforme = isInspectionConforme(existingInspection);
                    String statusAutomatico = statusService.calculateStatus(extinguisher.getValidade());
                    String novoStatus = conforme ? statusAutomatico : "nao_conforme";

                    extinguisher.setStatus(novoStatus);
                    extinguisherRepository.save(extinguisher);
                }
            }

            InspectionEntity savedInspection = inspectionRepository.save(existingInspection);
            return ResponseEntity.ok(savedInspection);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}
