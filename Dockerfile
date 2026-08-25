# ---------- Build ----------
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /app

# Copia arquivos do Maven

COPY mvnw pom.xml ./

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Copia o código
COPY src src

# Gera o JAR
RUN ./mvnw clean package -DskipTests

# ---------- Runtime ----------
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
