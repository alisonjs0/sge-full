package com.example.SGE.Config;

import com.example.SGE.Entity.UserEntity;
import com.example.SGE.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Cria usuário admin se não existir
            String adminEmail = "admin@teste.com";
            Optional<UserEntity> maybeAdmin = userRepository.findByEmail(adminEmail);
            if (maybeAdmin.isEmpty()) {
                UserEntity admin = new UserEntity(
                        "Administrador",
                        "00000000000",
                        "ADMIN",
                        adminEmail,
                        passwordEncoder.encode("123456"),
                        "ROLE_ADMIN"
                );
                admin.setCreatedDate(LocalDateTime.now());
                userRepository.save(admin);
                System.out.println("[DataInitializer] Usuário admin criado: " + adminEmail);
            } else {
                System.out.println("[DataInitializer] Usuário admin já existe: " + adminEmail);
            }

            // Garantir coluna missing que causava erro SQL nas rotas (Postgres)
            try {
                jdbcTemplate.execute("ALTER TABLE extintor ADD COLUMN IF NOT EXISTS localizacao varchar(255);");
                System.out.println("[DataInitializer] Garantida coluna extintor.localizacao");
            } catch (Exception e) {
                System.err.println("[DataInitializer] Não foi possível garantir coluna extintor.localizacao: " + e.getMessage());
            }

            // Garantir constraint UNIQUE para numero_identificacao (se já existir, captura exceção)
            try {
                jdbcTemplate.execute("ALTER TABLE extintor ADD CONSTRAINT uq_extintor_numero_identificacao UNIQUE (numero_identificacao);");
                System.out.println("[DataInitializer] Criada constraint UNIQUE extintor.numero_identificacao");
            } catch (Exception e) {
                System.out.println("[DataInitializer] Constraint UNIQUE extintor.numero_identificacao já existe ou falhou: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("[DataInitializer] Erro durante inicialização de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

