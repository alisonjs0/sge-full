package com.example.SGE.Controller;

import com.example.SGE.Entity.EquipamentEntity;
import com.example.SGE.Repository.EquipamentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/equipament")
public class EquipamentController {

    private final EquipamentRepository equipamentRepository;

    public EquipamentController(EquipamentRepository equipamentRepository) {
        this.equipamentRepository = equipamentRepository;
    }

    @PostMapping
    public EquipamentEntity equipamentCreate(@RequestBody EquipamentEntity equipament) {
        return equipamentRepository.save(equipament);
    }

    @GetMapping
    public List<EquipamentEntity> getAllEquipaments() {
        return equipamentRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipamentEntity> getEquipamentId(@PathVariable long id) {
        return equipamentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delEquipament(@PathVariable Long id) {
        Optional<EquipamentEntity> equipamentOptional = equipamentRepository.findById(id);

        if (equipamentOptional.isPresent()) {
            equipamentRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipamentEntity> updateEquipament(@PathVariable Long id, @RequestBody EquipamentEntity updateEquipament) {
        Optional<EquipamentEntity> existingEquipamentOptional = equipamentRepository.findById(id);

        if (existingEquipamentOptional.isPresent()) {
            EquipamentEntity existingEquipament = existingEquipamentOptional.get();
            existingEquipament.setName(updateEquipament.getName());
            equipamentRepository.save(existingEquipament);
            return ResponseEntity.ok(existingEquipament); // 200 OK com o usuário atualizado
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found se o ID não existir
        }
    }
}
