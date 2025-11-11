package com.example.SGE.Controller;

import com.example.SGE.Entity.ExtinguisherEntity;
import com.example.SGE.Repository.ExtinguisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/extinguisher")
public class ExtinguisherController {

    @Autowired
    private final ExtinguisherRepository extinguisherRepository;

    public ExtinguisherController(ExtinguisherRepository extinguisherRepository){
        this.extinguisherRepository = extinguisherRepository;
    }

    @PostMapping
    public ExtinguisherEntity createExtinguisher(@RequestBody ExtinguisherEntity extinguisherEntity) {
        return extinguisherRepository.save(extinguisherEntity);
    }

    @GetMapping
    public List<ExtinguisherEntity> getAllExtinguishers() {
        return extinguisherRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExtinguisherEntity> getExtinguisher(@PathVariable long id) {
        return extinguisherRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delExtinguisher(@PathVariable Long id) {
        Optional<ExtinguisherEntity> extinguisherOptional = extinguisherRepository.findById(id);

        if (extinguisherOptional.isPresent()) {
            extinguisherRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExtinguisherEntity> updateUser(@PathVariable Long id, @RequestBody ExtinguisherEntity updateExtinguisher) {
        System.out.println("=== ExtinguisherController.updateUser ===");
        System.out.println("🔵 PUT Request recebido para ID: " + id);
        System.out.println("🔵 Dados recebidos: " + updateExtinguisher);
        
        Optional<ExtinguisherEntity> existingExtinguisherOptional = extinguisherRepository.findById(id);

        if (existingExtinguisherOptional.isPresent()) {
            ExtinguisherEntity existingExtinguisher = existingExtinguisherOptional.get();

            // Atualizar TODOS os campos com os dados recebidos
            existingExtinguisher.setNumeroIdentificacao(updateExtinguisher.getNumeroIdentificacao());
            existingExtinguisher.setLocalizacao(updateExtinguisher.getLocalizacao());
            existingExtinguisher.setValidade(updateExtinguisher.getValidade());
            existingExtinguisher.setTipoAgente(updateExtinguisher.getTipoAgente());
            existingExtinguisher.setClasseIncendio(updateExtinguisher.getClasseIncendio());
            existingExtinguisher.setCapacidade(updateExtinguisher.getCapacidade());
            existingExtinguisher.setDataFabricacao(updateExtinguisher.getDataFabricacao());
            existingExtinguisher.setFabricante(updateExtinguisher.getFabricante());
            existingExtinguisher.setStatus(updateExtinguisher.getStatus());
            existingExtinguisher.setObservacoes(updateExtinguisher.getObservacoes());
            if (updateExtinguisher.getUnidadeId() != null) {
                existingExtinguisher.setUnidadeId(updateExtinguisher.getUnidadeId());
            }

            extinguisherRepository.save(existingExtinguisher);
            
            System.out.println("✅ Extintor atualizado com sucesso!");
            return ResponseEntity.ok(existingExtinguisher);
        } else {
            System.out.println("❌ Extintor não encontrado com ID: " + id);
            return ResponseEntity.notFound().build();
        }
    }
}
