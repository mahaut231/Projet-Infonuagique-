import http from "k6/http";
import { check } from "k6";

// Ramp agressif sans sleep pour trouver le point de saturation de chaque framework.
// Endpoint simple (GET /products/{id}) pour isoler le modèle de concurrence
// de la complexité des requêtes.
export const options = {
  stages: [
    { duration: "20s", target: 50 },
    { duration: "40s", target: 50 },
    { duration: "20s", target: 100 },
    { duration: "40s", target: 100 },
    { duration: "20s", target: 200 },
    { duration: "40s", target: 200 },
    { duration: "20s", target: 400 },
    { duration: "40s", target: 400 },
    { duration: "20s", target: 800 },
    { duration: "40s", target: 800 },
    { duration: "20s", target: 0 },
  ],
  thresholds: {
    http_req_failed: ["rate<0.10"],
  },
};

const BASE_URL = __ENV.BASE_URL || "http://localhost:8082";

function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

export default function () {
  const productId = randomInt(1, 500);
  const res = http.get(`${BASE_URL}/products/${productId}`);
  check(res, { "GET product 200": (r) => r.status === 200 });
}
