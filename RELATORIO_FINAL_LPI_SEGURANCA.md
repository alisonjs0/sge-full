# Análise de Medidas de Segurança Implementadas - Projetos LPI - 4º ADS 2025

**Nome dos Alunos(as):** [Seu Nome Aqui]  
**TURMA/CURSO:** 4º ADS  
**Local:** UNINASSAU Caruaru, PE  
**Data:** 28/11/2025  

## Objetivo
Avaliar e apresentar medidas de segurança aplicadas em projetos de software, considerando frontend, backend, banco de dados, canais de comunicação e integração de sistemas.

---

## Formulário de Avaliação de Segurança

### 1. Segurança no Frontend
**Medidas aplicadas:**
*   **Integração Segura com API:** O frontend consome a API respeitando as políticas de CORS restritas aplicadas no backend.
*   **Gestão de Sessão:** Utilização de Tokens JWT para manter a sessão do usuário, enviados via cabeçalho `Authorization: Bearer`.
*   **Feedback de Validação:** Exibição de erros de validação vindos do backend (ex: senha fraca, campos obrigatórios).

**Sugestões de melhoria:**
*   Implementar Content Security Policy (CSP) para mitigar XSS.
*   Armazenar o JWT em Cookies `HttpOnly` e `Secure` ao invés de `localStorage` para maior proteção contra roubo de tokens via XSS.

### 2. Segurança no Backend
**Medidas aplicadas:**
*   **Autenticação e Autorização:** Implementação de Spring Security com JWT. Controle de acesso baseado em Roles (RBAC), onde apenas ADMINs podem criar novos usuários.
*   **Proteção de Credenciais:** Remoção de senhas hardcoded do código fonte (`application.properties` e `docker-compose.yml`), utilizando variáveis de ambiente.
*   **Validação de Input:** Uso de Bean Validation (`@Valid`, `@Pattern`, `@Size`) para garantir senhas fortes (mínimo 6 caracteres, letras maiúsculas/minúsculas, números e especiais) e formato de CPF.
*   **Rate Limiting:** Implementação de limite de requisições (Bucket4j) no endpoint de login (5 tentativas/minuto) para prevenir força bruta.
*   **Proteção CSRF:** Habilitação do `CookieCsrfTokenRepository`.
*   **Logs Seguros:** Remoção de logs que expunham dados sensíveis (tokens/senhas) e uso de Logger SLF4J.

**Sugestões de melhoria:**
*   Implementar mecanismo de Refresh Token com rotação.
*   Adicionar auditoria de logs (quem acessou o quê e quando) persistida em banco.

### 3. Segurança no Banco de Dados
**Medidas aplicadas:**
*   **Criptografia de Senhas:** Armazenamento de senhas utilizando algoritmo **BCrypt** com fator de custo (strength) aumentado para **12**, garantindo hash robusto e salt individual.
*   **Prevenção de SQL Injection:** Uso de JPA/Hibernate que utiliza *Prepared Statements* por padrão, prevenindo injeção de SQL.
*   **Segurança de Conexão:** Credenciais de acesso ao banco gerenciadas via variáveis de ambiente, sem exposição no repositório.

**Sugestões de melhoria:**
*   Criptografia de dados em repouso (TDE - Transparent Data Encryption).
*   Criação de usuário de banco de dados com privilégios mínimos (evitar uso do usuário `postgres` root para a aplicação).

### 4. Canais e Comunicação
**Medidas aplicadas:**
*   **CORS Restrito:** Configuração de Cross-Origin Resource Sharing permitindo apenas origens confiáveis (`http://localhost:3000` e `http://localhost:8081`) e métodos específicos.
*   **Arquitetura REST:** Comunicação stateless via HTTP/JSON.

**Sugestões de melhoria:**
*   Habilitar HTTPS (TLS 1.3) no servidor de produção.
*   Implementar HSTS (HTTP Strict Transport Security) para forçar conexões seguras.

### 5. Integrações e APIs
**Medidas aplicadas:**
*   **Autenticação de API:** Todos os endpoints críticos (exceto login e health check) exigem token JWT válido.
*   **Bloqueio de Escalação de Privilégios:** O endpoint de cadastro ignora tentativas de injeção de roles administrativas via JSON, forçando o perfil "USER" ou exigindo autenticação de ADMIN.
*   **Tratamento de Erros:** Respostas HTTP adequadas (401, 403, 429) sem vazar stack traces ou detalhes internos da infraestrutura.

**Sugestões de melhoria:**
*   Implementar um API Gateway para centralizar a segurança de borda.
*   Adicionar validação de assinatura de payload para integrações críticas.

### 6. Testes de Segurança / Ciclo de Desenvolvimento
**Medidas aplicadas:**
*   **Análise de Vulnerabilidades:** Identificação e correção baseada em relatório de segurança prévio (SAST manual).
*   **Testes Manuais:** Verificação de endpoints com `curl` para confirmar bloqueios de acesso (403) e rate limiting (429).
*   **Revisão de Código:** Refatoração para remover código morto e inseguro (ex: endpoint de token infinito).

**Sugestões de melhoria:**
*   Automatizar testes de segurança (DAST) no pipeline de CI/CD (ex: OWASP ZAP).
*   Realizar testes de penetração (Pentest) periódicos.

### 7. Recursos e Infraestrutura
**Segurança aplicada em:**
*   **Servidores / Containers / Docker:** Orquestração via Docker Compose com injeção segura de variáveis de ambiente (`.env`).
*   **Backend / Frontend:** Separação clara de responsabilidades, reduzindo o acoplamento e superfície de ataque.
*   **Banco de dados:** Execução isolada em container, sem exposição direta à internet (apenas para a rede interna do Docker ou localhost controlado).

**Sugestões de melhoria:**
*   Executar containers com usuários não-privilegiados (non-root).
*   Implementar scan de vulnerabilidades nas imagens Docker (ex: Trivy).

---
**Entrega:**
*   [x] Formulário preenchido.
*   [x] Demonstração da aplicação funcionando (Backend rodando na porta 8081 com todas as proteções ativas).
*   [x] Avaliação baseada em identificação de riscos, medidas aplicadas e sugestões de melhoria.
