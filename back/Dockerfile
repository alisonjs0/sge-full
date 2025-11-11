# Estágio de Build (Para compilar e empacotar o Java)
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copia os arquivos de configuração do Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Dar permissão de execução ao Maven wrapper
RUN chmod +x ./mvnw

# Baixa as dependências do Maven
RUN ./mvnw dependency:go-offline -B

# Copia o código fonte e compila o projeto
COPY src src
RUN ./mvnw clean package -DskipTests -B

# Estágio Final (Imagem de produção otimizada)
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Criar usuário não-root para segurança
RUN adduser --system --no-create-home spring

# Copia o JAR compilado do estágio de build
COPY --from=build /app/target/SGE-*.jar app.jar

# Configura o usuário não-root
USER spring

# Expõe a porta que o Spring Boot usa
EXPOSE 8080

# Configura variáveis de ambiente para otimização da JVM
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport"

# Comando para rodar a aplicação Spring Boot
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]