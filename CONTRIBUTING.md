# Contributing to DiscoveriesBattleshipGame

> A short playbook so the three of us — and anyone reviewing or auditing
> the project — stay on the same page.

## Identity

Use your **school GitHub account** (`IGE-XXXXXX`) for every commit, PR
review and issue. Anything pushed from a personal account makes the
contribution graph confusing.

Configure once per clone:

```bash
git config user.name  "IGE-XXXXXX"
git config user.email "your-school-email@iscte-iul.pt"
```

## Local environment

Always work inside the **DevContainer** (`.devcontainer/`). It ships JDK
17, Maven 3.8.7, Git, the GitHub CLI and the docker socket — so every
member runs the same toolchain.

```bash
# IntelliJ:   New / Open → "Open in DevContainer"
# VS Code:    "Reopen in Container"
mvn -B clean test       # the post-create hook does this automatically
```

If you absolutely need to run the build on the host, you need **JDK 17**
and **Maven 3.6+**. Older Maven (e.g. apt 3.8.7) is fine as long as
this `pom.xml` is intact — it pins `maven-compiler-plugin` precisely so
the build doesn't depend on Maven's defaults.

## Branching

```
main                ← protected, only via PR
├── feat/<topic>    ← new features
├── fix/<topic>     ← bug fixes
└── docs/<topic>    ← README, javadoc, ADRs
```

* One PR per logical change. Smaller is faster to review.
* PR title format: `<area>: <imperative summary>` (mirrors commit style).
* Always assign one of the other two members as a reviewer.

## Commits

Conventional Commits, kept short:

```
fix(devcontainer): drop broken Yarn apt source
feat(game): add deterministic placement option
test: cover Position.isAdjacentTo edge cases
docs(readme): link Docker Hub image and GitHub Pages
```

Allowed prefixes: `feat`, `fix`, `docs`, `test`, `refactor`, `chore`,
`ci`, `build`, `deps`. Subject ≤ 70 chars, imperative mood, no trailing
period. Body explains *why*, not *what*.

## Issues

Open an issue **before** you start non-trivial work. Use the templates
in `.github/ISSUE_TEMPLATE/`:

* **Bug report** — for in-repo defects
* **Inter-group bug report** — for issues another group filed (Ficha 6 I)
* **DevContainer** — for environment-reproducibility problems (Ficha 6 F.3)

Set at least one `priority:` and one `type:` label.

## Code style

* `.editorconfig` is authoritative — let your IDE follow it.
* Java: 4-space indent, LF line endings, no tabs.
* Javadoc is required on every `public` and `protected` member.
* Run `mvn -B test` before pushing.

## Releases & deployment

* Each push to `main` triggers `completeWorkflow.yaml`:
  compile → test → Sonar Quality Gate → docker build → docker push →
  GitHub Pages.
* Docker images land in **Docker Hub** as `<DOCKERHUB_USER>/battleship-game`.
* GitHub Pages: https://ige-122975.github.io/DiscoveriesBattleshipGame/
* Sonar dashboard: https://sonarcloud.io/project/overview?id=IGE-122975_DiscoveriesBattleshipGame
* SnapDeploy: see `docs/operations/snapdeploy.md`.

## Code of conduct

Be precise, be kind, and assume good faith. We're three students trying
to ship software *and* not lose grade points to silly mistakes — leave
the project a little better than you found it on every PR.
