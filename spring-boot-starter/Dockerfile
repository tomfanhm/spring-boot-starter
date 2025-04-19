# ---------- build stage ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY . .
RUN ./mvnw -B -DskipTests clean package

# ---------- runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/target/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]