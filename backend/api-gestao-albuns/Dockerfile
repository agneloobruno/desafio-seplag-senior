FROM ubuntu:latest
LABEL authors="agneloobruno"

# -----------------------------------------------------------------------------
# ETAPA 1: BUILD (Compilação)
# Usamos uma imagem com Maven e JDK 21 para gerar o .jar
# -----------------------------------------------------------------------------
FROM maven:3.9.6-amazoncorretto-21 AS build
WORKDIR /app

# 1. Copia apenas o pom.xml primeiro.
# O Docker faz cache dessa camada. Se o pom não mudar, ele não baixa as dependências de novo.
COPY pom.xml .
# Baixa as dependências em modo offline para preparar o cache
RUN mvn dependency:go-offline

# 2. Copia o código fonte do projeto
COPY src ./src

# 3. Compila o projeto e gera o JAR (pula testes para ser mais rápido no build, já rodamos antes)
RUN mvn clean package -DskipTests

# -----------------------------------------------------------------------------
# ETAPA 2: RUNTIME (Execução)
# Usamos uma imagem Alpine (super leve) apenas com o JRE 21 (sem Maven/Compilador)
# -----------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Define variáveis de ambiente padrão (podem ser sobrescritas no docker-compose)
ENV SPRING_PROFILES_ACTIVE=prod

# Cria um usuário não-root para segurança (Prática Sênior/DevSecOps)
# Instala utilitários necessários em runtime (ex: curl) para healthcheck
RUN apk add --no-cache curl

# Cria um usuário não-root para segurança (Prática Sênior/DevSecOps)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copia apenas o JAR gerado na etapa de build anterior
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta 8080
EXPOSE 8080

# Comando que inicia a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]