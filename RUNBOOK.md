# Runbook — Exécution des tests

Ce fichier décrit la procédure standard pour lancer les tests de manière reproductible.

## 1. Démarrer WebFlux

```powershell
docker compose --profile webflux up --build -d
docker compose ps
```

## 2. Vérifier l’API

```powershell
curl.exe "http://localhost:8082/actuator/health"
curl.exe "http://localhost:8082/products/1"
curl.exe "http://localhost:8082/external/recommendations/4?delayMs=300"
```

## 3. Smoke test

```powershell
k6 run -e BASE_URL=http://localhost:8082 benchmark/smoke-test.js
```

Critère de validation : `checks_succeeded = 100%`.

## 4. Scénario CRUD

```powershell
k6 run -e BASE_URL=http://localhost:8082 benchmark/scenario-crud.js --summary-export benchmark/results/docker-webflux-crud-summary.json
```

## 5. Scénario I/O-bound

```powershell
k6 run -e BASE_URL=http://localhost:8082 -e DELAY_MS=300 benchmark/scenario-io.js --summary-export benchmark/results/docker-webflux-io-300-summary.json
```

## 6. Scénario CPU-bound

```powershell
k6 run -e BASE_URL=http://localhost:8082 -e N=35 benchmark/scenario-cpu.js --summary-export benchmark/results/docker-webflux-cpu-summary.json
```

## 7. Scénario Saturation (point de rupture)

```powershell
k6 run -e BASE_URL=http://localhost:8082 benchmark/scenario-saturation.js --summary-export benchmark/results/docker-webflux-saturation-summary.json
```

Ce scénario monte jusqu'à 800 VUs sans sleep sur GET /products/{id}. L'objectif est de trouver le seuil où la latence explose et/ou le taux d'erreur dépasse 10%. Adapter `BASE_URL` et le préfixe du fichier JSON pour chaque framework.

## 8. Capturer les métriques Docker

```powershell
docker stats --no-stream common-postgres mock-service webflux-r2dbc
docker stats --no-stream common-postgres mock-service webflux-r2dbc > benchmark/results/docker-stats-webflux.txt
```

## 9. Arrêter les services

```powershell
docker compose --profile webflux down
docker compose --profile webflux down -v
```

## 10. Adaptation aux autres versions

Pour MVC + JPA :

```powershell
k6 run -e BASE_URL=http://localhost:8081 benchmark/scenario-crud.js
```

Pour MVC + Virtual Threads :

```powershell
k6 run -e BASE_URL=http://localhost:8083 benchmark/scenario-crud.js
```

Les scripts de benchmark ne doivent pas être modifiés.
