package com.example.SGE.DTO;

import com.example.SGE.Entity.UserEntity;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Long id,
        String nome,
        String email,
        String cpf,
        String cargo,
        String roles,
        LocalDateTime createdDate
) {
    public UserResponseDTO(UserEntity user) {
        this(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getCpf(),
                user.getCargo(),
                user.getRoles(),
                user.getCreatedDate()
        );
    }
}