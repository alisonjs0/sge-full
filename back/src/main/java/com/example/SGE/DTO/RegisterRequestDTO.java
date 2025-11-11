package com.example.SGE.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

public record RegisterRequestDTO(
    @NotBlank(message = "O nome é obrigatorio")
    String nome,

    @NotBlank(message = "O email é obrigatorio")
    @Email(message = "Formato de email invalido")
    String email,

    @NotBlank(message = "O cpf é obrigatorio")
    String cpf,

    @NotBlank(message = "A senha é obrigatória")
    String senha,

    String cargo,
    String roles,
    @CreatedDate
    LocalDateTime createdDate
) {}
