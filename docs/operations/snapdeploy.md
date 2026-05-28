# SnapDeploy operations runbook (Ficha 6 — Tarefa I)

This document covers the cloud-side of Ficha 6 Tarefa I: standing up a
publicly-reachable version of the Battleship game on SnapDeploy, kept
in sync with the image we publish to Docker Hub.

## Why SnapDeploy

* Free tier, no credit card required.
* Pulls images straight from Docker Hub — no extra CI plumbing.
* Single public URL we can hand to other groups for testing.

## One-time setup (owner: IGE-122975 — created the Docker Hub repo)

1. Sign up at https://snapdeploy.io/ with the same GitHub account that
   owns this repo (`IGE-122975`).
2. **Projects → New Service → From Docker image**.
3. Image name: `<DOCKERHUB_USERNAME>/battleship-game:latest`
   (replace `<DOCKERHUB_USERNAME>` with the value of the
   `DOCKERHUB_USERNAME` secret used in the GitHub workflows).
4. **Pull policy**: *Always* — so re-deploys grab the newest `latest` tag.
5. **Run command**: leave blank (`docker run -it` is set by SnapDeploy).
6. **Resources**: free tier is enough for an interactive CLI game.
7. **Environment variables**: none required.
8. Click **Deploy**.

SnapDeploy will assign a URL of the form
`https://<service-name>.snapdeploy.app/`. Record it.

## Wiring the URL into the repo

After SnapDeploy gives you the URL, two files need to be updated:

* **README.md** — add a "Try it online" line near the top.
* **docs/index.html** — replace the placeholder `href` in the
  "Try it online" card.

Both placeholders are the literal string `__SNAPDEPLOY_URL__` so
search-and-replace gets you there in one shot:

```bash
SNAP_URL='https://battleship-rdm.snapdeploy.app/'   # ← real URL here
sed -i "s|__SNAPDEPLOY_URL__|${SNAP_URL}|g" README.md docs/index.html
git add README.md docs/index.html
git commit -m "docs(snapdeploy): wire public URL into README and landing"
git push
```

## Re-deploys

Each push to `main` triggers `completeWorkflow.yaml`, which pushes a
new `:latest` tag to Docker Hub. SnapDeploy's *Always* pull policy will
pick it up on the next request, so there's nothing to do on the
SnapDeploy side after the initial setup.

If you need to force a re-pull (e.g. you tagged `latest` manually):
**Service → Redeploy** in the SnapDeploy dashboard.

## Inter-group exercise (Tarefa I.4–I.7)

1. Trade your SnapDeploy URL with another group (Slack thread, course
   forum, etc.).
2. Use it as a real end user. Try to break it: invalid input, very
   large boards, illegal placements, race conditions across multiple
   browser tabs / SSH sessions, etc.
3. File bugs in **their** repo using the "Inter-group bug report"
   template (we created the same one in `.github/ISSUE_TEMPLATE/`),
   one per concrete defect or improvement idea — at least **two** per
   team member as required by I.6.
4. When inbound bugs arrive in our repo, triage with the help of an LLM:
   feed the description plus the last ~10 commits and have it propose
   the likely root cause / affected classes. Assign and label
   accordingly.

## Troubleshooting

| Symptom                                  | Likely cause                                | Fix                                                                |
| ---------------------------------------- | ------------------------------------------- | ------------------------------------------------------------------ |
| SnapDeploy shows `ImagePullBackOff`      | Docker Hub repo is private / wrong username | Make the repo public in Docker Hub or paste registry credentials.  |
| Service starts then exits immediately    | The Battleship app expects stdin             | SnapDeploy must run with `-i`; pick a "Terminal" service template. |
| Page loads but game state never advances | Players sharing one container                | Use SnapDeploy's per-session scaling, or accept the limitation.    |
| Auto re-deploy doesn't pick up `latest`  | SnapDeploy pull policy = `IfNotPresent`     | Switch to `Always`.                                                |
