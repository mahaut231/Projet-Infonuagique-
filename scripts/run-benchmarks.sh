#!/usr/bin/env bash
# Exécute la suite complète de benchmarks pour les 3 frameworks.
# Usage : bash scripts/run-benchmarks.sh
# Doit être lancé depuis la racine du dépôt.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"
CWF="$ROOT_DIR/cloud-web-frameworks"
RESULTS="$CWF/benchmark/results"

mkdir -p "$RESULTS"

run_suite() {
    local PROFILE="$1"   # docker profile (webflux | mvc | virtual-threads)
    local PORT="$2"       # port de l'application
    local PREFIX="$3"     # préfixe pour les fichiers de résultats
    local CONTAINER="$4"  # nom du container pour docker stats

    local BASE_URL="http://localhost:${PORT}"

    echo ""
    echo "══════════════════════════════════════════════"
    echo "  Framework : ${PREFIX}  (port ${PORT})"
    echo "══════════════════════════════════════════════"

    echo "--- Démarrage des services (profile: ${PROFILE}) ---"
    docker compose --profile "$PROFILE" up --build -d
    echo "--- Attente que l'app soit prête (healthcheck) ---"
    for i in $(seq 1 30); do
        if curl -sf "${BASE_URL}/actuator/health" > /dev/null 2>&1; then
            echo "    → Prête après ${i}s"
            break
        fi
        sleep 2
        if [ "$i" -eq 30 ]; then
            echo "ERREUR : le service ${PREFIX} n'a pas démarré en 60s" >&2
            docker compose --profile "$PROFILE" logs
            docker compose --profile "$PROFILE" down -v
            exit 1
        fi
    done

    echo "--- Smoke test ---"
    k6 run -e BASE_URL="$BASE_URL" "$CWF/benchmark/smoke-test.js" || true

    echo "--- Scénario CRUD ---"
    k6 run -e BASE_URL="$BASE_URL" \
        "$CWF/benchmark/scenario-crud.js" \
        --summary-export "${RESULTS}/${PREFIX}-crud-summary.json" || true

    echo "--- Scénario I/O-bound (delay=300ms) ---"
    k6 run -e BASE_URL="$BASE_URL" -e DELAY_MS=300 \
        "$CWF/benchmark/scenario-io.js" \
        --summary-export "${RESULTS}/${PREFIX}-io-300-summary.json" || true

    echo "--- Scénario CPU-bound (N=35) ---"
    k6 run -e BASE_URL="$BASE_URL" -e N=35 \
        "$CWF/benchmark/scenario-cpu.js" \
        --summary-export "${RESULTS}/${PREFIX}-cpu-summary.json" || true

    echo "--- Scénario Saturation ---"
    k6 run -e BASE_URL="$BASE_URL" \
        "$CWF/benchmark/scenario-saturation.js" \
        --summary-export "${RESULTS}/${PREFIX}-saturation-summary.json" || true

    echo "--- Métriques Docker ---"
    docker stats --no-stream common-postgres mock-service "$CONTAINER" \
        | tee "${RESULTS}/docker-stats-${PREFIX}.txt"

    echo "--- Arrêt des services ---"
    docker compose --profile "$PROFILE" down -v
}

cd "$CWF"

run_suite "webflux"        "8082" "webflux"         "webflux-r2dbc"
run_suite "mvc"            "8081" "mvc-jpa"          "mvc-jpa"
run_suite "virtual-threads" "8083" "mvc-virtual-threads" "mvc-virtual-threads"

echo ""
echo "=== Tous les benchmarks terminés ==="
echo "Résultats dans : $RESULTS"
ls -lh "$RESULTS"
