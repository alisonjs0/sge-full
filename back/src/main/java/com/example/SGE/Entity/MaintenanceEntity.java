package com.example.SGE.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "manutencoes")
public class MaintenanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_manutencao")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "extintor_id", referencedColumnName = "id_extintor")
    private ExtinguisherEntity extinguisher;

    @Column(name = "tipo", nullable = false)
    private String tipo; // preventiva, corretiva, recarga, teste_hidrostatico

    @Column(name = "status", nullable = false)
    private String status; // agendada, em_andamento, concluida, cancelada

    @Column(name = "data_agendada")
    private LocalDateTime dataAgendada;

    @Column(name = "data_realizada")
    private LocalDateTime dataRealizada;

    @ManyToOne
    @JoinColumn(name = "responsavel_id", referencedColumnName = "id_usuario")
    private UserEntity responsavel;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "custo")
    private Double custo;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "proxima_manutencao")
    private LocalDateTime proximaManutencao;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public MaintenanceEntity() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExtinguisherEntity getExtinguisher() {
        return extinguisher;
    }

    public void setExtinguisher(ExtinguisherEntity extinguisher) {
        this.extinguisher = extinguisher;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataAgendada() {
        return dataAgendada;
    }

    public void setDataAgendada(LocalDateTime dataAgendada) {
        this.dataAgendada = dataAgendada;
    }

    public LocalDateTime getDataRealizada() {
        return dataRealizada;
    }

    public void setDataRealizada(LocalDateTime dataRealizada) {
        this.dataRealizada = dataRealizada;
    }

    public UserEntity getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UserEntity responsavel) {
        this.responsavel = responsavel;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getCusto() {
        return custo;
    }

    public void setCusto(Double custo) {
        this.custo = custo;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getProximaManutencao() {
        return proximaManutencao;
    }

    public void setProximaManutencao(LocalDateTime proximaManutencao) {
        this.proximaManutencao = proximaManutencao;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
