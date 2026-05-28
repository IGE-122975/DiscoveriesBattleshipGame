FROM eclipse-temurin:17-jdk


RUN apt-get update && \
    apt-get install -y maven git && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*


WORKDIR /workspace