package com.example.SGE.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

public record RegisterRequestDTO(
    @NotBlank(message = "O nome é obrigatorio")
    @jakarta.validation.constraints.Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    String nome,

    @NotBlank(message = "O email é obrigatorio")
    @Email(message = "Formato de email invalido")
    String email,

    @NotBlank(message = "O cpf é obrigatorio")
    @jakarta.validation.constraints.Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos numéricos")
    String cpf,

    @NotBlank(message = "A senha é obrigatória")
    @jakarta.validation.constraints.Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$", message = "Senha deve ter no mínimo 6 caracteres, incluindo maiúscula, minúscula, número e caractere especial")
    String senha,

    @NotBlank(message = "O cargo é obrigatório")
    String cargo,
    
    @CreatedDate
    LocalDateTime createdDate
) {}
