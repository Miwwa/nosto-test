# === Build Frontend ===
FROM node:22 AS frontend-builder
WORKDIR /app/frontend
COPY ./frontend ./
RUN npm install
RUN npm run build

# === Build Backend ===
FROM maven:3.9-eclipse-temurin-23 AS backend-builder
WORKDIR /app/backend
COPY backend/pom.xml .
RUN mvn dependency:go-offline
COPY backend/src ./src
COPY --from=frontend-builder /app/frontend/dist ./src/main/resources/webroot
RUN mvn package -DskipTests

# === Final Runtime Image ===
FROM eclipse-temurin:23
WORKDIR /app
COPY --from=backend-builder /app/backend/target/*-fat.jar ./app.jar
EXPOSE 8888
CMD ["java", "-jar", "app.jar"]
