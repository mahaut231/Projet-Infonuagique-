# Projet Infonuagique — Comparaison Web Frameworks

Ce projet compare trois approches backend Spring autour d’une API REST commune :

- Spring MVC + Spring Data JPA
- Spring WebFlux + R2DBC
- Spring MVC + Virtual Threads

L’objectif est d’évaluer la scalabilité, la latence, l’utilisation des ressources et le comportement sous surcharge dans un contexte proche d’une application cloud.

## Architecture

| Service | Port | Rôle |
|---|---:|---|
| PostgreSQL | 5432 | Base de données commune |
| Mock service | 9090 | Service externe simulé avec latence contrôlée |
| Spring MVC + JPA | 8081 | Version impérative bloquante |
| Spring WebFlux + R2DBC | 8082 | Version réactive non bloquante |
| Spring MVC + Virtual Threads | 8083 | Version impérative avec threads légers Java 21 |

## Lancement de la version WebFlux avec Docker

```powershell
docker compose --profile webflux up --build -d
```

## Lancement de la version MVC-JPA avec Docker

```powershell
docker compose --profile mvc up -d
```

## Vérification

```powershell
docker compose ps

curl.exe "http://localhost:8082/actuator/health"
curl.exe "http://localhost:8082/products/1"
curl.exe "http://localhost:8082/external/recommendations/4?delayMs=300"
```
(Utilisez le bon port selon l'approche que vous voulez utiliser)

## Endpoints communs

| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/products` | Liste de produits |
| GET | `/products/{id}` | Détail d’un produit |
| GET | `/orders/{id}` | Détail d’une commande |
| GET | `/customers/{id}/orders` | Commandes d’un client |
| POST | `/orders` | Création d’une commande |
| GET | `/external/recommendations/{id}?delayMs=300` | Appel externe simulé |
| GET | `/stress/cpu?n=35` | Charge CPU |

## Tests k6

```powershell
k6 run -e BASE_URL=http://localhost:8082 benchmark/smoke-test.js

k6 run -e BASE_URL=http://localhost:8082 benchmark/scenario-crud.js --summary-export benchmark/results/docker-webflux-crud-summary.json

k6 run -e BASE_URL=http://localhost:8082 -e DELAY_MS=300 benchmark/scenario-io.js --summary-export benchmark/results/docker-webflux-io-300-summary.json

k6 run -e BASE_URL=http://localhost:8082 -e N=35 benchmark/scenario-cpu.js --summary-export benchmark/results/docker-webflux-cpu-summary.json
```

(Utilisez le bon port selon l'approche que vous voulez utiliser)

## Métriques système

Pendant les tests :

```powershell
docker stats --no-stream common-postgres mock-service webflux-r2dbc
```

(Remplacer _webflux-r2dbc_ par _mvc-jpa_ pour tester avec mvc-jpa)

Capture dans un fichier :

```powershell
docker stats --no-stream common-postgres mock-service webflux-r2dbc > benchmark/results/docker-stats-webflux.txt
```
(Remplacer _webflux-r2dbc_ par _mvc-jpa_ pour tester avec mvc-jpa)

## Règles WebFlux

La version WebFlux doit rester strictement non bloquante :

- pas de `Thread.sleep()`
- pas de `.block()`
- pas de Spring Data JPA
- pas de client HTTP bloquant
- accès base avec R2DBC
- appels externes avec WebClient

## Nettoyage

```powershell
docker compose --profile webflux down
docker compose --profile webflux down -v
```

(Remplacer _webflux par _mvc_ pour nettoyer mvc-jpa)
