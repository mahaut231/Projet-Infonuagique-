import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  stages: [
    { duration: "30s", target: 50 },
    { duration: "1m", target: 50 },
    { duration: "30s", target: 200 },
    { duration: "1m", target: 200 },
    { duration: "30s", target: 500 },
    { duration: "1m", target: 500 },
    { duration: "30s", target: 0 },
  ],
  thresholds: {
    "http_req_duration{expected_response:true}": ["p(95)<2000"],
    http_req_failed: ["rate<0.01"],
  },
};

const BASE_URL = __ENV.BASE_URL || "http://localhost:8082";
const DELAY_MS = __ENV.DELAY_MS || "300";

function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

export default function () {
  const productId = randomInt(1, 500);

  const res = http.get(`${BASE_URL}/external/recommendations/${productId}?delayMs=${DELAY_MS}`);

  check(res, {
    "external recommendation 200": (r) => r.status === 200,
  });

  sleep(0.2);
}