# Contrat API commun

Toutes les versions doivent respecter strictement ce contrat afin que les mêmes scripts k6 puissent être utilisés sans modification.

## Endpoints

| Méthode | Endpoint | Réponse attendue |
|---|---|---|
| GET | `/products` | `ProductResponse[]` |
| GET | `/products/{id}` | `ProductResponse` |
| GET | `/orders/{id}` | `OrderResponse` |
| GET | `/customers/{id}/orders` | `OrderResponse[]` |
| POST | `/orders` | `OrderResponse` |
| GET | `/external/recommendations/{id}?delayMs=300` | `RecommendationResponse` |
| GET | `/stress/cpu?n=35` | `CpuStressResponse` |

## ProductResponse

```json
{
  "id": 1,
  "name": "Product 1",
  "price": 10.56,
  "stock": 362
}
```

## OrderResponse

```json
{
  "id": 1,
  "customerId": 601,
  "status": "CREATED",
  "createdAt": "2026-05-15T16:30:20.287275",
  "items": [
    {
      "productId": 363,
      "productName": "Product 363",
      "quantity": 4,
      "unitPrice": 76.22
    }
  ]
}
```

## CreateOrderRequest

```json
{
  "customerId": 1,
  "items": [
    {
      "productId": 4,
      "quantity": 2
    },
    {
      "productId": 8,
      "quantity": 1
    }
  ]
}
```

## RecommendationResponse

```json
{
  "productId": 4,
  "recommendedProductIds": [5, 6, 7],
  "delayMs": 300
}
```

## CpuStressResponse

```json
{
  "n": 35,
  "result": 9227465,
  "durationMs": 123
}
```

## Codes HTTP

| Cas | Code |
|---|---:|
| Ressource trouvée | 200 |
| Ressource créée via `POST /orders` | 200 ou 201, à standardiser dans le groupe |
| Ressource absente | 404 |
| Requête invalide | 400 |
| Erreur serveur | 500 |

Pour une comparaison stricte, il est recommandé d’utiliser `200 OK` pour `POST /orders` dans les trois versions, sauf décision commune inverse.
