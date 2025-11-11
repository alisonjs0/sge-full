package com.example.SGE.Repository;

import com.example.SGE.Entity.AlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<AlertEntity, Long> {
    // Buscar por tipo de alerta
    List<AlertEntity> findByTipoAlerta(String tipoAlerta);
    
    // Buscar por prioridade
    List<AlertEntity> findByPrioridade(String prioridade);
    
    // Buscar por status
    List<AlertEntity> findByStatus(String status);
    
    // Buscar por extintor
    List<AlertEntity> findByExtintorId(Long extintorId);
    
    // Buscar por usuário
    List<AlertEntity> findByUsuarioId(Long usuarioId);
    
    // Buscar alertas pendentes
    List<AlertEntity> findByStatusOrderByDataCriacaoDesc(String status);
    
    // Buscar alertas não lidos
    @Query("SELECT a FROM AlertEntity a WHERE a.dataLeitura IS NULL ORDER BY a.dataCriacao DESC")
    List<AlertEntity> findUnreadAlerts();
    
    // Buscar alertas por prioridade e status
    List<AlertEntity> findByPrioridadeAndStatus(String prioridade, String status);
}
