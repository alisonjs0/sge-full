package com.example.SGE.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuração de Beans para a aplicação
 */
@Configuration
public class BeansConfig {
    
    /**
     * Bean RestTemplate para chamadas HTTP
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
