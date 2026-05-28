# Environnement de test

Ce fichier décrit l’environnement utilisé pour les benchmarks. Il doit être mis à jour lorsque les tests sont déplacés de la machine locale vers la VM commune.

## Environnement local actuel

| Élément | Version / configuration |
|---|---|
| OS | Windows 11 |
| Java | Temurin OpenJDK 21.0.11 |
| Maven | 3.9.16 |
| Spring Boot | 3.3.5 |
| PostgreSQL | 16 |
| Mock service | Node 20 Alpine |
| Docker | Docker version 29.4.3, build 055a478 |
| Docker Compose | Docker Compose version v5.1.4 |
| k6 | k6.exe v2.0.0 (commit/8c3be52cc1, go1.26.3, windows/amd64) |

Commandes utiles :

```powershell
java -version
mvn -version
docker --version
docker compose version
k6 version
```

## Services Docker

| Service | Port | Limites |
|---|---:|---|
| PostgreSQL | 5432 | 1 vCPU, 1536 Mo RAM |
| Mock service | 9090 | 0.5 vCPU, 512 Mo RAM |
| WebFlux R2DBC | 8082 | 2 vCPU, 1536 Mo RAM |
| MVC JPA | 8081 | 2 vCPU, 1536 Mo RAM |
| MVC Virtual Threads | 8083 | 2 vCPU, 1536 Mo RAM |

## Configuration des benchmarks

Chaque test est exécuté avec une seule application Spring active à la fois. PostgreSQL et le mock-service restent communs. Les exports k6 sont stockés dans `benchmark/results`.

| Script | Objectif |
|---|---|
| `smoke-test.js` | Vérifier que tous les endpoints répondent |
| `scenario-crud.js` | Simuler une application métier classique |
| `scenario-io.js` | Tester les appels externes avec latence contrôlée |
| `scenario-cpu.js` | Tester un traitement CPU-bound |

## Environnement cible VM

| Élément | Configuration cible |
|---|---|
| OS | Ubuntu Server 24.04 LTS |
| CPU | 4 vCPU |
| RAM | 8 Go |
| Stockage | 40 Go |
| Java | OpenJDK 21 |
| Build | Maven |
| Conteneurisation | Docker + Docker Compose |
| Tests | k6 |
| Outils | Git, curl, jq, Docker Stats, outils JDK |

## Limites méthodologiques

Sur la machine locale, k6 peut consommer des ressources en même temps que les services testés. Les résultats locaux servent donc surtout à valider le protocole et la stabilité fonctionnelle. Les résultats définitifs devront être produits sur la VM commune.
