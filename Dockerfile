# ---------- Build ----------
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /app

# Copia a pasta .mvn de forma explícita para o diretório atual
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Garante a quebra de linha correta do script (previne erros no Windows) e permissão
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Baixa as dependências (utiliza o wrapper copiado)
RUN ./mvnw dependency:go-offline

# Copia o código
COPY src ./src

# Gera o JAR
RUN ./mvnw clean package -DskipTests

# ---------- Runtime ----------
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copia o JAR gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
