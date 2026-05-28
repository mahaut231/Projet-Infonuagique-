# Graph generation scripts

These scripts generate graphs from your existing k6 summary JSON exports and optional k6 CSV time-series exports.

## 1. Generate CSV time-series exports

Run from the project root:

```powershell
.\scripts\export-k6-timeseries.ps1
```

This creates:

```text
benchmark/results/webflux-crud-timeseries.csv
benchmark/results/webflux-io-300-timeseries.csv
benchmark/results/webflux-cpu-timeseries.csv
```

## 2. Install Python dependencies

```powershell
pip install pandas matplotlib tabulate
```

`tabulate` is only needed for the Markdown summary table.

## 3. Generate graphs

```powershell
python scripts/generate_graphs.py
```

Output directory:

```text
benchmark/results/graphs/
```

Generated files include:

```text
summary-throughput.png
summary-latency-avg.png
summary-latency-p95.png
summary-error-rate.png
summary-table.csv
summary-table.md
*-latency-timeseries.png
*-throughput-timeseries.png
```

## Notes

- Summary JSON files are enough for bar charts.
- CSV files are required for time-series graphs.
- The current setup keeps k6 on the local machine and the app in Docker, which matches the current implementation.
