# Projet Infonuagique — Comparaison Web Frameworks

Ce projet compare trois approches backend Spring pour une API REST commune :

- Spring MVC + Spring Data JPA
- Spring WebFlux + R2DBC
- Spring MVC + Virtual Threads

L'objectif est d'évaluer la scalabilité, la latence, l'utilisation des ressources et le comportement sous surcharge.

## Architecture

Services communs :

- PostgreSQL : port 5432
- Mock service externe : port 9090
- Spring WebFlux : port 8082

Les autres versions utiliseront :

- Spring MVC + JPA : port 8081
- Spring MVC + Virtual Threads : port 8083

## Lancement WebFlux avec Docker

```powershell
docker compose --profile webflux up --build -d