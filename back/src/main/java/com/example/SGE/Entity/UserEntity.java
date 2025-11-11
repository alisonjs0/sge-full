package com.example.SGE.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Entity
@Table(name = "usuario")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;
    @Column(name = "nome", length = 100 , nullable = false)
    private String nome;
    @Column(name = "cpf", length = 11, unique = true, nullable = false, columnDefinition = "CHAR(11)")
    private String cpf;
    @Column(name = "cargo", length = 100, nullable = false)
    private String cargo;
    @Column(name = "email", length = 150, unique = true, nullable = false)
    private String email;

    private String senha;

    private String roles;

    private LocalDateTime createdDate;

    public UserEntity() {}

    public UserEntity(String nome, String cpf, String cargo, String email, String senha, String roles) {
        this.nome = nome;
        this.cpf = cpf;
        this.cargo = cargo;
        this.email = email;
        this.senha = senha;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Fix: Adiciona o prefixo ROLE_ se não existir
        // Spring Security's hasAnyRole() automaticamente procura por "ROLE_" + roleName
        String roleWithPrefix = roles;
        if (roles != null && !roles.startsWith("ROLE_")) {
            roleWithPrefix = "ROLE_" + roles;
        }
        return Collections.singletonList(new SimpleGrantedAuthority(roleWithPrefix));
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return senha;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    //    public String getSetor() {
//        return setor;
//    }
//
//    public void setSetor(String setor) {
//        this.setor = setor;
//    }
}
