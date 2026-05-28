import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  vus: 5,
  duration: "30s",
};

const BASE_URL = __ENV.BASE_URL || "http://localhost:8082";

export default function () {
  const productId = Math.floor(Math.random() * 500) + 1;
  const orderId = Math.floor(Math.random() * 5000) + 1;
  const customerId = Math.floor(Math.random() * 1000) + 1;

  let productRes = http.get(`${BASE_URL}/products/${productId}`);
  check(productRes, {
    "GET /products/{id} status 200": (r) => r.status === 200,
  });

  let orderRes = http.get(`${BASE_URL}/orders/${orderId}`);
  check(orderRes, {
    "GET /orders/{id} status 200": (r) => r.status === 200,
  });

  let customerOrdersRes = http.get(`${BASE_URL}/customers/${customerId}/orders`);
  check(customerOrdersRes, {
    "GET /customers/{id}/orders status 200": (r) => r.status === 200,
  });

  let externalRes = http.get(`${BASE_URL}/external/recommendations/${productId}?delayMs=100`);
  check(externalRes, {
    "GET /external/recommendations status 200": (r) => r.status === 200,
  });

  let cpuRes = http.get(`${BASE_URL}/stress/cpu?n=30`);
  check(cpuRes, {
    "GET /stress/cpu status 200": (r) => r.status === 200,
  });

  sleep(1);
}