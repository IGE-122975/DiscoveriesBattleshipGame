# DevContainer for DiscoveriesBattleshipGame
# Base image already ships with JDK 17 (Temurin), Git, zsh, useful CLIs.
FROM mcr.microsoft.com/devcontainers/java:1-17-bookworm

# The base image ships a Yarn apt repo whose GPG key has rotated upstream;
# this causes `apt-get update` to fail with NO_PUBKEY 62D54FD4003F6525.
# We don't need Yarn for a Java project — drop the source list before updating.
# (Reported via Issue "F.3 — DevContainer build fails on bookworm base image".)
RUN rm -f /etc/apt/sources.list.d/yarn.list /etc/apt/sources.list.d/yarn.sources

# Install Maven explicitly so every group member uses the same version.
# (The base image only provides JDK; Maven via apt is reproducible enough for the lab.)
RUN apt-get update \
    && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install --no-install-recommends \
       maven \
       jq \
       vim \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Sanity check: print versions at build time so they appear in the log
RUN java --version && mvn --version && git --version
