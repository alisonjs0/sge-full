#!/bin/bash

# Script para executar o backend com variáveis de ambiente do .env

# Carregar variáveis do .env
set -a
source .env
set +a

# Executar Maven
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8081"
