package com.example.SGE.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerta")
public class AlertEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Long id;

    @Column(name = "tipo_alerta", nullable = false, length = 150)
    private String tipoAlerta;

    @Column(name = "mensagem", nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @Column(name = "prioridade", length = 50)
    private String prioridade; // baixa, media, alta, critica

    @Column(name = "status", length = 50)
    private String status; // pendente, lido, resolvido

    @ManyToOne
    @JoinColumn(name = "extintor_id", referencedColumnName = "id_extintor")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ExtinguisherEntity extintor;

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id_usuario")
    @JsonIgnoreProperties({"password", "authorities", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled", "username"})
    private UserEntity usuario;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "data_leitura")
    private LocalDateTime dataLeitura;

    @Column(name = "data_resolucao")
    private LocalDateTime dataResolucao;

    public AlertEntity() {
        this.status = "pendente";
        this.prioridade = "media";
    }

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        if (status == null) {
            status = "pendente";
        }
        if (prioridade == null) {
            prioridade = "media";
        }
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoAlerta() {
        return tipoAlerta;
    }

    public void setTipoAlerta(String tipoAlerta) {
        this.tipoAlerta = tipoAlerta;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        if ("lido".equals(status) && dataLeitura == null) {
            dataLeitura = LocalDateTime.now();
        }
        if ("resolvido".equals(status) && dataResolucao == null) {
            dataResolucao = LocalDateTime.now();
        }
    }

    public ExtinguisherEntity getExtintor() {
        return extintor;
    }

    public void setExtintor(ExtinguisherEntity extintor) {
        this.extintor = extintor;
    }

    public UserEntity getUsuario() {
        return usuario;
    }

    public void setUsuario(UserEntity usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataLeitura() {
        return dataLeitura;
    }

    public void setDataLeitura(LocalDateTime dataLeitura) {
        this.dataLeitura = dataLeitura;
    }

    public LocalDateTime getDataResolucao() {
        return dataResolucao;
    }

    public void setDataResolucao(LocalDateTime dataResolucao) {
        this.dataResolucao = dataResolucao;
    }
}
