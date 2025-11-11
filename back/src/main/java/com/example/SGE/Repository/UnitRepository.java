package com.example.SGE.Repository;

import com.example.SGE.Entity.UnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<UnitEntity, Long> {
    // Buscar unidades por status ativo
    List<UnitEntity> findByAtivo(Boolean ativo);
    
    // Buscar unidades por nome
    List<UnitEntity> findByNomeContainingIgnoreCase(String nome);
    
    // Buscar unidades por responsável
    List<UnitEntity> findByResponsavelId(Long responsavelId);
}
