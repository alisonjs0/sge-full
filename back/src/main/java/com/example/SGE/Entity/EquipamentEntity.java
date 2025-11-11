package com.example.SGE.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tipoequipamento")
public class EquipamentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipamento")
    private long id;

    @Column(name = "nome", length = 100, nullable = false)
    private String name;

    public EquipamentEntity(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public EquipamentEntity() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
