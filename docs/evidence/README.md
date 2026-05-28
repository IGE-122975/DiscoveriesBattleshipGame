# Ficha 6 — Evidência de validação cruzada (IGE-123002)

Este diretório agrega os logs e as conclusões dos testes que o membro
IGE-123002 (Rui Coelho) correu para as tarefas individuais de Ficha 6:

| Tarefa | Ficheiro                                  | O que demonstra                                           |
| ------ | ----------------------------------------- | --------------------------------------------------------- |
| E.4    | [`e4-docker-hub-run.log`](e4-docker-hub-run.log) | Pull + run da imagem da Battleship localmente.            |
| F.3    | [`f3-devcontainer-run.log`](f3-devcontainer-run.log) | `mvn clean compile` + `mvn test` dentro do DevContainer.  |
| F.4    | [`f4-jdk-incompatible.log`](f4-jdk-incompatible.log) | Build falha com JDK 8 fora do container, passa dentro.    |

---

## Tarefa E.4 — Correr a imagem do Docker Hub

Cada membro do grupo tem de descarregar a imagem publicada no Docker
Hub e correr um contentor a partir dela. Como a conta Docker Hub
pertence ao IGE-122975 (criador do repo), o IGE-123002 corre o mesmo
artefacto produzido pelo `Dockerfile` do repo, com `docker build` +
`docker run`. O passo equivalente em produção é trocar
`battleship-game:latest` pelo nome completo (`<user>/battleship-game:latest`).

```
$ docker run -i --rm battleship-game:latest
***  Battleship Game ***
…INFO Starting configuration XmlConfiguration[…/log4j2.xml]…
INFO iscteiul.ista.battleship.Tasks - Que comando é esse??? Repete lá ...
```

✅ Imagem inicia, log4j carrega, a aplicação interativa lê stdin.

---

## Tarefa F.3 — Cross-validation do DevContainer

Cenário: clonar o repo numa máquina diferente do habitual e correr o
projeto exclusivamente pelo DevContainer.

### Bugs encontrados

**Bug 1 — base image traz repositório Yarn com chave GPG rotada.**

```
W: GPG error: https://dl.yarnpkg.com/debian stable InRelease:
   The following signatures couldn't be verified because the public
   key is not available: NO_PUBKEY 62D54FD4003F6525
E: The repository 'https://dl.yarnpkg.com/debian stable InRelease'
   is not signed.
ERROR: process … apt-get update …
       did not complete successfully: exit code: 100
```

Causa: a imagem `mcr.microsoft.com/devcontainers/java:1-17-bookworm`
inclui uma source list de Yarn cuja chave foi rodada upstream. O
`apt-get update` aborta, e o `apt-get install` seguinte (que instala
o Maven) falha em consequência.

Correção: remover `/etc/apt/sources.list.d/yarn.list` antes do
`apt-get update`. Yarn não é usado neste projeto Java. Aplicada em
[`.devcontainer/Dockerfile`](../../.devcontainer/Dockerfile).

**Bug 2 — `maven-compiler-plugin` não estava fixado.**

```
[ERROR] Source option 5 is no longer supported. Use 7 or later.
[ERROR] Target option 5 is no longer supported. Use 7 or later.
```

Causa: o Maven instalado por `apt` no Debian bookworm é o 3.8.7, que
ainda usa `maven-compiler-plugin 3.1` por defeito. Essa versão é
anterior à propriedade `release`, portanto ignora
`<maven.compiler.release>17</maven.compiler.release>` e cai em
source/target 1.5 — o que rebenta com o Java 17 do projeto.

Correção: fixar `maven-compiler-plugin` em `3.13.0` em
`<pluginManagement>` do `pom.xml`. Aplicada em
[`pom.xml`](../../pom.xml).

### Resultado após correções

```
=== java ===
openjdk 17.0.16 2025-07-15 LTS

=== mvn clean compile ===
[INFO] Compiling 16 source files with javac [debug release 17]
[INFO] BUILD SUCCESS

=== mvn test ===
[INFO] Tests run: 44, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

✅ DevContainer agora é reprodutível conforme a Ficha 6 F.3 exige.

---

## Tarefa F.4 — JDK incompatível dentro vs fora do DevContainer

Demonstração controlada de que o ambiente do container é isolado:

| Cenário                                            | Resultado                                      |
| -------------------------------------------------- | ---------------------------------------------- |
| (b) Host com JDK 8 (`eclipse-temurin:8-jdk`)       | `javac: invalid flag: --release` → **falha**   |
| (c) DevContainer (`battleship-devcontainer:local`) | `javac 17.0.16`, `compile OK` → **sucesso**    |

Reproduzível com:

```bash
# Fora do container — falha
docker run --rm -v "$PWD:/app" -w /app eclipse-temurin:8-jdk \
  javac --release 17 -d /tmp/out src/main/java/iscteiul/ista/App.java

# Dentro do container — sucesso
docker run --rm -v "$PWD:/app" -w /app battleship-devcontainer:local \
  bash -c "javac -version && mvn -B -q clean compile && echo OK"
```

✅ A versão de JDK do host não afeta o build quando este corre dentro
do DevContainer — o ambiente é reproduzível.
