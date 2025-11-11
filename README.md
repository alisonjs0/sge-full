# 🔥 Fire Guard - Sistema de Gestão de Extintores

Sistema completo para gerenciamento de extintores, inspeções e manutenções.

---

## 🚀 Como Rodar o Projeto

### Pré-requisitos
- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)

### ⚡ Início Rápido (3 comandos)

```bash
# 1. Clone o repositório
git clone https://github.com/Alisonjs0/Trabalho-ads-back.git
cd Trabalho-ads-back

# 2. Inicie todos os serviços
docker-compose up -d

# 3. Acesse a aplicação
# Frontend: http://localhost:3000
# Backend API: http://localhost:8081
```

**Pronto! 🎉** O sistema já está rodando com banco de dados populado.

---

## 📦 O que é instalado automaticamente?

Ao executar `docker-compose up -d`, três serviços são iniciados:

1. **PostgreSQL** (porta 5434)
   - Imagem: `alisonjs0/fire-guard-postgres:latest`
   - Banco pré-configurado com dados de exemplo
   - 12 usuários, 12 extintores, 12 unidades, 10 inspeções, 6 manutenções

2. **Backend Spring Boot** (porta 8081)
   - API REST com autenticação JWT
   - Conecta automaticamente ao PostgreSQL

3. **Frontend Next.js** (porta 3000)
   - Interface web moderna
   - Conecta automaticamente ao Backend

---

## 🔐 Login Padrão

```
Email: admin@teste.com
Senha: 123456
```

Outros usuários de teste:
- `user1@teste.com` até `user11@teste.com` (senha: `123456`)

---

## 📋 Comandos Úteis

### Ver logs em tempo real
```bash
docker-compose logs -f
```

### Ver logs de um serviço específico
```bash
docker-compose logs -f postgres
docker-compose logs -f backend
docker-compose logs -f frontend
```

### Parar todos os serviços
```bash
docker-compose down
```

### Reiniciar um serviço
```bash
docker-compose restart backend
```

### Verificar status dos serviços
```bash
docker-compose ps
```

### Remover tudo (incluindo volumes)
```bash
docker-compose down -v
```

---

## 🔧 Desenvolvimento Local (Opcional)

Se você quer desenvolver sem Docker:

### Backend
```bash
cd Trabalho-ads-back

# Iniciar apenas o PostgreSQL no Docker
docker-compose up -d postgres

# Rodar o backend localmente
./mvnw spring-boot:run
```

### Frontend
```bash
cd fire_guard

# Instalar dependências
npm install

# Rodar em modo desenvolvimento
npm run dev
```

---

## 🗄️ Banco de Dados

### Conectar ao PostgreSQL
```bash
# Via Docker
docker exec -it fire-guard-postgres psql -U postgres -d postgres

# Via cliente local (se tiver psql instalado)
psql -h localhost -p 5434 -U postgres -d postgres
```

**Senha:** `896271`

### Ver dados
```sql
-- Listar tabelas
\dt

-- Contar registros
SELECT 'usuarios' as tabela, COUNT(*) FROM usuario
UNION ALL
SELECT 'extintores', COUNT(*) FROM extintor
UNION ALL
SELECT 'unidades', COUNT(*) FROM unidade;
```

---

## 🏗️ Estrutura do Projeto

```
Projeto/
├── docker-compose.yml          # Orquestração dos serviços
├── README.md                   # Este arquivo
├── PROJETO_DOCKER_CONFIG.md    # Documentação técnica detalhada
│
├── fire_guard/                 # Frontend (Next.js + TypeScript)
│   ├── src/
│   │   ├── app/               # Páginas da aplicação
│   │   ├── components/        # Componentes React
│   │   ├── context/           # Contextos (Auth, etc)
│   │   ├── hooks/             # Hooks customizados
│   │   └── service/           # Serviços de API
│   ├── Dockerfile
│   └── package.json
│
└── Trabalho-ads-back/         # Backend (Spring Boot + Java)
    ├── src/
    │   └── main/
    │       ├── java/com/example/SGE/
    │       │   ├── Config/        # Configurações (Security, JWT)
    │       │   ├── Controller/    # Endpoints REST
    │       │   ├── Entity/        # Entidades JPA
    │       │   ├── Repository/    # Repositórios
    │       │   └── Service/       # Lógica de negócio
    │       └── resources/
    │           └── application.properties
    ├── Dockerfile
    └── pom.xml
```

---

## 🌐 Endpoints da API

Base URL: `http://localhost:8081`

### Autenticação
- `POST /auth/login` - Login
- `POST /auth/register` - Registro

### Recursos (requer autenticação)
- `GET /extinguisher` - Listar extintores
- `GET /unit` - Listar unidades
- `GET /inspection` - Listar inspeções
- `GET /maintenance` - Listar manutenções
- `GET /alert` - Listar alertas

---

## 🐳 Imagem Docker

O banco de dados está disponível publicamente no Docker Hub:

```bash
# Baixar apenas o banco de dados
docker pull alisonjs0/fire-guard-postgres:latest

# Rodar apenas o banco
docker run -d \
  --name fire-guard-postgres \
  -p 5434:5432 \
  -e POSTGRES_PASSWORD=896271 \
  alisonjs0/fire-guard-postgres:latest
```

---

## 🔍 Troubleshooting

### Porta já em uso
Se a porta 3000, 8081 ou 5434 já estiver em uso:

```bash
# Ver o que está usando a porta
sudo lsof -i :3000
sudo lsof -i :8081
sudo lsof -i :5434

# Matar o processo
sudo kill -9 <PID>
```

### Container não inicia
```bash
# Ver logs detalhados
docker-compose logs

# Reconstruir imagens
docker-compose build --no-cache
docker-compose up -d
```

### Erro de conexão com o banco
```bash
# Verificar se o PostgreSQL está rodando
docker-compose ps postgres

# Reiniciar o banco
docker-compose restart postgres

# Esperar 10 segundos e reiniciar o backend
docker-compose restart backend
```

### Limpar tudo e começar do zero
```bash
# Parar e remover tudo
docker-compose down -v

# Baixar imagem atualizada
docker pull alisonjs0/fire-guard-postgres:latest

# Iniciar novamente
docker-compose up -d
```

---

## 📊 Dados de Exemplo

O banco vem pré-populado com:

- ✅ 12 usuários (1 admin + 11 usuários comuns)
- ✅ 12 unidades (diferentes localizações)
- ✅ 12 extintores (diversos tipos e capacidades)
- ✅ 10 inspeções (histórico de verificações)
- ✅ 6 manutenções (registros de manutenção)
- ✅ 10 alertas (notificações do sistema)

---

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

---

## 📄 Licença

Este projeto está sob a licença MIT.

---

## 👥 Equipe

Desenvolvido como projeto acadêmico.

---

## 📞 Suporte

- **Docker Hub**: https://hub.docker.com/r/alisonjs0/fire-guard-postgres
- **Documentação Técnica**: [PROJETO_DOCKER_CONFIG.md](./PROJETO_DOCKER_CONFIG.md)
- **Issues**: https://github.com/Alisonjs0/Trabalho-ads-back/issues

---

**🎉 Projeto 100% containerizado - Clone e rode em segundos!**
