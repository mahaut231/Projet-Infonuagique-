import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  stages: [
    { duration: "30s", target: 50 },
    { duration: "1m", target: 50 },
    { duration: "30s", target: 200 },
    { duration: "1m", target: 200 },
    { duration: "30s", target: 0 },
  ],
};

const BASE_URL = __ENV.BASE_URL || "http://localhost:8082";

function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

export default function () {
  const r = Math.random();

  if (r < 0.60) {
    const productId = randomInt(1, 500);
    const res = http.get(`${BASE_URL}/products/${productId}`);
    check(res, { "GET product 200": (r) => r.status === 200 });
  } else if (r < 0.80) {
    const orderId = randomInt(1, 5000);
    const res = http.get(`${BASE_URL}/orders/${orderId}`);
    check(res, { "GET order 200": (r) => r.status === 200 });
  } else if (r < 0.90) {
    const customerId = randomInt(1, 1000);
    const res = http.get(`${BASE_URL}/customers/${customerId}/orders`);
    check(res, { "GET customer orders 200": (r) => r.status === 200 });
  } else {
    const payload = JSON.stringify({
      customerId: randomInt(1, 1000),
      items: [
        { productId: randomInt(1, 500), quantity: randomInt(1, 4) },
        { productId: randomInt(1, 500), quantity: randomInt(1, 4) },
      ],
    });

    const res = http.post(`${BASE_URL}/orders`, payload, {
      headers: { "Content-Type": "application/json" },
    });

    check(res, { "POST order 200": (r) => r.status === 200 });
  }

  sleep(1);
}