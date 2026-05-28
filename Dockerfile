# ---- Stage 1: build the jar + runtime dependencies ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /src

# Cache dependencies first for faster rebuilds
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Build the thin jar AND copy runtime deps (log4j etc) to target/lib.
# -Dmaven.test.skip=true skips both test compilation and execution
# (AppTest.java in this project still uses JUnit 3/4 imports which break with JUnit 6).
COPY src ./src
RUN mvn -B -q clean package -Dmaven.test.skip=true \
    && mvn -B -q dependency:copy-dependencies \
       -DincludeScope=runtime \
       -DoutputDirectory=target/lib

# ---- Stage 2: minimal runtime image ----
# Using JRE 17 (debian-slim) rather than alpine: musl libc + Log4j2's StackWalker
# can have issues identifying caller classes in some scenarios.
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the project jar plus runtime libraries
COPY --from=build /src/target/Battleship-1.0-SNAPSHOT.jar /app/app.jar
COPY --from=build /src/target/lib /app/lib

# App is interactive (reads from stdin in Tasks.taskB). Run with `docker run -it`.
ENTRYPOINT ["java","-cp","/app/app.jar:/app/lib/*","iscteiul.ista.App"]
