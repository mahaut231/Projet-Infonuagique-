#!/usr/bin/env bash
# Installation de l'environnement de benchmark sur Ubuntu Server 24.04 LTS.
# Usage : bash scripts/vm-setup.sh
set -euo pipefail

MAVEN_VERSION="3.9.9"
MAVEN_URL="https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz"

echo "=== Mise à jour apt ==="
sudo apt-get update -y
sudo apt-get install -y \
    ca-certificates curl gnupg lsb-release \
    git jq unzip

# ── Docker ────────────────────────────────────────────────────────────────────
echo "=== Installation de Docker Engine ==="
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg \
    | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" \
    | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt-get update -y
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

sudo systemctl enable --now docker
sudo usermod -aG docker "$USER"

# ── Java 21 ───────────────────────────────────────────────────────────────────
echo "=== Installation de Java 21 ==="
sudo apt-get install -y openjdk-21-jdk

# ── Maven ─────────────────────────────────────────────────────────────────────
echo "=== Installation de Maven ${MAVEN_VERSION} ==="
curl -fsSL "${MAVEN_URL}" -o /tmp/maven.tar.gz
sudo tar -xzf /tmp/maven.tar.gz -C /opt
sudo ln -sf "/opt/apache-maven-${MAVEN_VERSION}/bin/mvn" /usr/local/bin/mvn
rm /tmp/maven.tar.gz

# ── k6 ────────────────────────────────────────────────────────────────────────
echo "=== Installation de k6 ==="
curl -fsSL https://dl.k6.io/key.gpg \
    | sudo gpg --dearmor -o /usr/share/keyrings/k6-archive-keyring.gpg
echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] \
https://dl.k6.io/deb stable main" \
    | sudo tee /etc/apt/sources.list.d/k6.list > /dev/null
sudo apt-get update -y
sudo apt-get install -y k6

# ── Vérification ──────────────────────────────────────────────────────────────
echo ""
echo "=== Versions installées ==="
java  -version
mvn   -version
docker --version
docker compose version
k6    version
echo ""
echo "=== Installation terminée ==="
echo "IMPORTANT : déconnecte-toi et reconnecte-toi pour activer le groupe docker."
echo "            Ensuite lance : bash scripts/run-benchmarks.sh"
