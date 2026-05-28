const express = require("express");

const app = express();
const port = 9090;

app.get("/recommendations/:productId", async (req, res) => {
  const productId = Number(req.params.productId);
  const delayMs = Number(req.query.delayMs || 300);

  await new Promise(resolve => setTimeout(resolve, delayMs));

  res.json({
    productId,
    recommendedProductIds: [
      productId + 1,
      productId + 2,
      productId + 3
    ],
    delayMs
  });
});

app.get("/health", (req, res) => {
  res.json({ status: "UP" });
});

app.listen(port, () => {
  console.log(`Mock service running on port ${port}`);
});