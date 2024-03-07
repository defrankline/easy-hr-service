FROM gradle:jdk17-alpine AS build

WORKDIR /app

COPY . .

RUN gradle clean && gradle build --no-daemon -x test

FROM openjdk:17-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]