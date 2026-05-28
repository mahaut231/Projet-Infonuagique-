INSERT INTO customers (name, email)
SELECT
    'Customer ' || i,
    'customer' || i || '@example.com'
FROM generate_series(1, 1000) AS i;

INSERT INTO products (name, price, stock)
SELECT
    'Product ' || i,
    ROUND((random() * 100 + 1)::numeric, 2),
    (random() * 1000)::int
FROM generate_series(1, 500) AS i;

INSERT INTO orders (customer_id, status, created_at)
SELECT
    ((random() * 999)::int + 1),
    'CREATED',
    NOW() - ((random() * 30)::int || ' days')::interval
FROM generate_series(1, 5000);

INSERT INTO order_items (order_id, product_id, quantity)
SELECT
    ((random() * 4999)::int + 1),
    ((random() * 499)::int + 1),
    ((random() * 4)::int + 1)
FROM generate_series(1, 15000);