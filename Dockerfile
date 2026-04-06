# Mərhələ 1: Build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . .

# Hər ehtimala qarşı icazəni yenə də veririk
RUN chmod +x gradlew

# Build əmri
RUN ./gradlew clean build -x test

# Mərhələ 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]