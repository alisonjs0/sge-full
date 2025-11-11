package com.example.SGE.Repository;

import com.example.SGE.Entity.MaintenanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<MaintenanceEntity, Long> {
    // Buscar manutenções por extintor
    List<MaintenanceEntity> findByExtinguisherId(Long extintorId);
    
    // Buscar manutenções por responsável
    List<MaintenanceEntity> findByResponsavelId(Long responsavelId);
    
    // Buscar manutenções por status
    List<MaintenanceEntity> findByStatus(String status);
    
    // Buscar manutenções por tipo
    List<MaintenanceEntity> findByTipo(String tipo);
}
