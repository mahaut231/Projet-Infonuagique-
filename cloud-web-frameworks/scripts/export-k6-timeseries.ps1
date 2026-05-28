# Export k6 time-series CSV files.
# Run from the project root while WebFlux is running on http://localhost:8082.

New-Item -ItemType Directory -Force -Path benchmark\results | Out-Null

k6 run `
  -e BASE_URL=http://localhost:8082 `
  --out csv=benchmark/results/webflux-crud-timeseries.csv `
  benchmark/scenario-crud.js

k6 run `
  -e BASE_URL=http://localhost:8082 `
  -e DELAY_MS=300 `
  --out csv=benchmark/results/webflux-io-300-timeseries.csv `
  benchmark/scenario-io.js

k6 run `
  -e BASE_URL=http://localhost:8082 `
  -e N=35 `
  --out csv=benchmark/results/webflux-cpu-timeseries.csv `
  benchmark/scenario-cpu.js
