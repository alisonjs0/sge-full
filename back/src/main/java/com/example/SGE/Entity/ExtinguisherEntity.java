package com.example.SGE.Entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "extintor")
public class ExtinguisherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_extintor")
    private long id;

    // Temporariamente nullable=true para permitir criação da coluna em um banco existente sem falha
    @Column(name = "numero_identificacao", nullable = true, length = 50, unique = true)
    private String numeroIdentificacao;

    // Temporariamente nullable=true (antes estava false e a coluna não foi criada por dados existentes)
    @Column(name = "localizacao", nullable = true, length = 255)
    private String localizacao;

    @Column(name = "validade", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date validade;

    @Column(name = "tipo_agente", length = 50, nullable = true)
    private String tipoAgente;

    @Column(name = "classe_incendio", length = 10, nullable = true)
    private String classeIncendio;

    @Column(name = "capacidade", length = 20)
    private String capacidade;

    @Column(name = "data_fabricacao", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date dataFabricacao;

    @Column(name = "fabricante", length = 100)
    private String fabricante;

    // Temporariamente nullable=true para evitar falha de adição; será preenchido com default ATIVO
    @Column(name = "status", length = 20, nullable = true)
    private String status;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "unidade_id")
    private Long unidadeId;

    @ManyToOne
    @JoinColumn(name = "tipoequipamento_id")
    private EquipamentEntity equipamentoId;

    public ExtinguisherEntity() {}

    // Construtor completo
    public ExtinguisherEntity(String numeroIdentificacao, String localizacao, String tipoAgente, String classeIncendio,
                               String capacidade, Date validade, Date dataFabricacao,
                               String fabricante, String status, String observacoes, Long unidadeId) {
        this.numeroIdentificacao = numeroIdentificacao;
        this.localizacao = localizacao;
        this.tipoAgente = tipoAgente;
        this.classeIncendio = classeIncendio;
        this.capacidade = capacidade;
        this.validade = validade;
        this.dataFabricacao = dataFabricacao;
        this.fabricante = fabricante;
        this.status = status;
        this.observacoes = observacoes;
        this.unidadeId = unidadeId;
    }

    @PrePersist
    @PreUpdate
    private void applyDefaults() {
        if (this.status == null || this.status.isBlank()) {
            this.status = "ATIVO"; // default
        }
    }

    // Getters e Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNumeroIdentificacao() { return numeroIdentificacao; }
    public void setNumeroIdentificacao(String numeroIdentificacao) { this.numeroIdentificacao = numeroIdentificacao; }
    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
    public Date getValidade() { return validade; }
    public void setValidade(Date validade) { this.validade = validade; }
    public String getTipoAgente() { return tipoAgente; }
    public void setTipoAgente(String tipoAgente) { this.tipoAgente = tipoAgente; }
    public String getClasseIncendio() { return classeIncendio; }
    public void setClasseIncendio(String classeIncendio) { this.classeIncendio = classeIncendio; }
    public String getCapacidade() { return capacidade; }
    public void setCapacidade(String capacidade) { this.capacidade = capacidade; }
    public Date getDataFabricacao() { return dataFabricacao; }
    public void setDataFabricacao(Date dataFabricacao) { this.dataFabricacao = dataFabricacao; }
    public String getFabricante() { return fabricante; }
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Long getUnidadeId() { return unidadeId; }
    public void setUnidadeId(Long unidadeId) { this.unidadeId = unidadeId; }
    public EquipamentEntity getEquipamentoId() { return equipamentoId; }
    public void setEquipamentoId(EquipamentEntity equipamentoId) { this.equipamentoId = equipamentoId; }
}