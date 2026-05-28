# Battleship 🚢

[![Build & publish Docker image](https://github.com/IGE-122975/DiscoveriesBattleshipGame/actions/workflows/docker-publish.yml/badge.svg)](https://github.com/IGE-122975/DiscoveriesBattleshipGame/actions/workflows/docker-publish.yml)
[![Complete CI/CD pipeline](https://github.com/IGE-122975/DiscoveriesBattleshipGame/actions/workflows/completeWorkflow.yaml/badge.svg)](https://github.com/IGE-122975/DiscoveriesBattleshipGame/actions/workflows/completeWorkflow.yaml)
[![CodeQL](https://github.com/IGE-122975/DiscoveriesBattleshipGame/actions/workflows/codeql.yml/badge.svg)](https://github.com/IGE-122975/DiscoveriesBattleshipGame/actions/workflows/codeql.yml)

🌐 **Jogar online:** [__SNAPDEPLOY_URL__](__SNAPDEPLOY_URL__) ← substituir pelo URL real assim que o serviço estiver no ar (ver [`docs/operations/snapdeploy.md`](docs/operations/snapdeploy.md)).
📚 **Documentação:** https://ige-122975.github.io/DiscoveriesBattleshipGame/ (site Maven + Javadoc + landing custom)
🐳 **Imagem Docker:** `ige122975/battleship-game` ([Docker Hub](https://hub.docker.com/r/ige122975/battleship-game))

## Nickname do Grupo
IGE - RDM

## Membros da Equipa
| Número | Nome |
|--------|------|
| 122975 | Manuel Ferreira |
| 123002 | Rui Coelho |
| 123901 | Dinis Silva |

## Como executar

### Online (recomendado para quem só quer experimentar)
Basta abrir o link na badge "Jogar online" acima. O contentor é
reiniciado automaticamente sempre que uma nova versão é publicada
em `main`.

### Dentro de Docker (qualquer máquina)
```bash
docker run -it --rm ige122975/battleship-game:latest
```

### Localmente com Maven
Requer Java 17 e Maven 3.6+.

```bash
mvn -B clean package
java -jar target/Battleship-1.0-SNAPSHOT.jar
```

### Dentro do DevContainer (recomendado para desenvolvimento)
Garante o mesmo ambiente que a pipeline CI/CD usa.

* IntelliJ: *File → Remote Development → Dev Containers → New*
* VS Code: *Reopen in Container*

A pasta `.devcontainer/` traz JDK 17, Maven, Git, GitHub CLI e o
necessário para o `mvn test` correr sem configuração extra.

## Pipeline CI/CD

Pipeline principal em [`.github/workflows/completeWorkflow.yaml`](.github/workflows/completeWorkflow.yaml).
Por cada push para `main`:

1. Checkout (com histórico completo, para o Sonar).
2. Resolução de dependências.
3. `mvn clean compile`.
4. `mvn test`.
5. Quality Gate no SonarCloud (saltado se `SONAR_TOKEN` não estiver definido).
6. Build da imagem Docker.
7. Publish para Docker Hub.
8. Geração e deploy do GitHub Pages.

Auxiliares: `docker-publish.yml` (build/publish dedicado), `codeql.yml`
(análise de segurança), `pr-notify.yml` (comentário em PR), `labels.yml`
(sincronização de labels).

## Descrição

Versão académica do clássico jogo da batalha naval, tematizada com
embarcações da era dos Descobrimentos.

### Porta-aviões (Galleon)
O galeão era um grande navio de guerra e carga dos séculos XVI e XVII, robusto, com vários conveses e muitas peças de artilharia, usado em longas viagens oceânicas e batalhas navais.

### Fragata (Frigate)
A fragata é um navio de guerra relativamente rápido e manobrável, com boa autonomia, usado sobretudo para escolta de comboios, patrulha e luta contra submarinos ou outros navios de superfície.

### Nau (Carrack)
A nau (carrack) era um grande navio das Grandes Navegações, mais volumoso e estável que a caravela, capaz de transportar muitos tripulantes, carga e também artilharia em viagens oceânicas de longa distância.

### Caravela (Caravel)
A caravela era um navio leve e muito manobrável, com baixo calado, ideal para explorar costas, rios e realizar viagens de descoberta, sacrificando capacidade de carga em troca de velocidade e agilidade.

### Barca (Barge)
A barca (barge) é uma embarcação simples, de fundo chato e pouca profundidade, usada sobretudo para transporte de pessoas ou carga em rios, portos e águas calmas, com pouca ou nenhuma artilharia.

## Para contribuir

Ver [`CONTRIBUTING.md`](CONTRIBUTING.md). Resumo:

* Trabalhar dentro do DevContainer.
* Branches: `feat/<topic>`, `fix/<topic>`, `docs/<topic>`.
* Conventional Commits, PRs com revisor.
* Issues seguem os templates em `.github/ISSUE_TEMPLATE/`.
