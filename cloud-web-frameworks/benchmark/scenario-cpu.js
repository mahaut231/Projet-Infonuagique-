import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  stages: [
    { duration: "30s", target: 10 },
    { duration: "1m", target: 10 },
    { duration: "30s", target: 25 },
    { duration: "1m", target: 25 },
    { duration: "30s", target: 50 },
    { duration: "1m", target: 50 },
    { duration: "30s", target: 0 },
  ],
  thresholds: {
    "http_req_duration{expected_response:true}": ["p(95)<2000"],
    http_req_failed: ["rate<0.01"],
  },
};

const BASE_URL = __ENV.BASE_URL || "http://localhost:8082";
const N = __ENV.N || "35";

export default function () {
  const res = http.get(`${BASE_URL}/stress/cpu?n=${N}`);

  check(res, {
    "cpu stress 200": (r) => r.status === 200,
  });

  sleep(0.5);
}