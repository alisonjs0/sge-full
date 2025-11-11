package com.example.SGE.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inspecoes")
public class InspectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inspecoes")
    private Long id;

    // Temporariamente nullable=true para evitar falha de migração em bancos existentes
    @Column(name = "data_inspecao", nullable = true)
    private LocalDateTime inspectionDate;

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id_usuario")
    private UserEntity inspectionAuthor;

    @ManyToOne
    @JoinColumn(name = "extintor_id", referencedColumnName = "id_extintor")
    private ExtinguisherEntity extinguisher;

    // Booleans marcados como nullable=true para permitir schema update sem erro em registros antigos
    @Column(name = "manometro", nullable = true)
    private Boolean manometro;

    @Column(name = "lacre", nullable = true)
    private Boolean seal;

    @Column(name = "rotulo", nullable = true)
    private Boolean rotulo;

    @Column(name = "danos", nullable = true)
    private Boolean damages;

    @Column(name = "obstrucoes", nullable = true)
    private Boolean obstructions;

    @Column(name = "sinalizacao", nullable = true)
    private Boolean sinalizacao;

    @Column(name = "suporte_fixacao", nullable = true)
    private Boolean suporteFixacao;

    @Column(name = "observacoes")
    private String observations;

    @Column(name = "proxima_inspecao")
    private LocalDateTime nextInspectionDate;

    public InspectionEntity() {}

    // Construtor ajustado
    public InspectionEntity(LocalDateTime inspectionDate, UserEntity inspectionAuthor, ExtinguisherEntity extinguisher,
                            Boolean manometro, Boolean seal, Boolean rotulo, Boolean damages,
                            Boolean obstructions, Boolean sinalizacao, Boolean suporteFixacao, String observations,
                            LocalDateTime nextInspectionDate) {
        this.inspectionDate = inspectionDate;
        this.inspectionAuthor = inspectionAuthor;
        this.extinguisher = extinguisher;
        this.manometro = manometro;
        this.seal = seal;
        this.rotulo = rotulo;
        this.damages = damages;
        this.obstructions = obstructions;
        this.sinalizacao = sinalizacao;
        this.suporteFixacao = suporteFixacao;
        this.observations = observations;
        this.nextInspectionDate = nextInspectionDate;
    }

    @PrePersist
    @PreUpdate
    private void applyDefaults() {
        if (this.inspectionDate == null) {
            this.inspectionDate = LocalDateTime.now();
        }
        if (this.manometro == null) this.manometro = false;
        if (this.seal == null) this.seal = false;
        if (this.rotulo == null) this.rotulo = false;
        if (this.damages == null) this.damages = false;
        if (this.obstructions == null) this.obstructions = false;
        if (this.sinalizacao == null) this.sinalizacao = false;
        if (this.suporteFixacao == null) this.suporteFixacao = false;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getInspectionDate() { return inspectionDate; }
    public void setInspectionDate(LocalDateTime inspectionDate) { this.inspectionDate = inspectionDate; }
    public UserEntity getInspectionAuthor() { return inspectionAuthor; }
    public void setInspectionAuthor(UserEntity inspectionAuthor) { this.inspectionAuthor = inspectionAuthor; }
    public ExtinguisherEntity getExtinguisher() { return extinguisher; }
    public void setExtinguisher(ExtinguisherEntity extinguisher) { this.extinguisher = extinguisher; }
    public Boolean getManometro() { return manometro; }
    public void setManometro(Boolean manometro) { this.manometro = manometro; }
    public Boolean getSeal() { return seal; }
    public void setSeal(Boolean seal) { this.seal = seal; }
    public Boolean getRotulo() { return rotulo; }
    public void setRotulo(Boolean rotulo) { this.rotulo = rotulo; }
    public Boolean getDamages() { return damages; }
    public void setDamages(Boolean damages) { this.damages = damages; }
    public Boolean getObstructions() { return obstructions; }
    public void setObstructions(Boolean obstructions) { this.obstructions = obstructions; }
    public Boolean getSinalizacao() { return sinalizacao; }
    public void setSinalizacao(Boolean sinalizacao) { this.sinalizacao = sinalizacao; }
    public Boolean getSuporteFixacao() { return suporteFixacao; }
    public void setSuporteFixacao(Boolean suporteFixacao) { this.suporteFixacao = suporteFixacao; }
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    public LocalDateTime getNextInspectionDate() { return nextInspectionDate; }
    public void setNextInspectionDate(LocalDateTime nextInspectionDate) { this.nextInspectionDate = nextInspectionDate; }
}