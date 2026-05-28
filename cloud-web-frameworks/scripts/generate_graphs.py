#!/usr/bin/env python3
"""
Generate graphs from k6 summary JSON files and optional k6 CSV time-series files.

Expected location:
  benchmark/results/*.json
  benchmark/results/*.csv

Output:
  benchmark/results/graphs/*.png

Usage from project root:
  python scripts/generate_graphs.py

Optional:
  python scripts/generate_graphs.py --results-dir benchmark/results --out-dir benchmark/results/graphs
"""

from __future__ import annotations

import argparse
import json
import math
import re
from pathlib import Path
from typing import Any

import matplotlib.pyplot as plt
import pandas as pd


def parse_filename(path: Path) -> tuple[str, str]:
    """
    Extract framework and scenario from common names such as:
      docker-webflux-crud-summary.json
      docker-webflux-io-300-summary.json
      docker-webflux-cpu-summary.json
      webflux-crud-timeseries.csv
      webflux-io-300-timeseries.csv
    """
    name = path.stem.lower()
    name = name.replace("docker-", "")
    name = name.replace("-summary", "")
    name = name.replace("-timeseries", "")

    if "webflux" in name:
        framework = "WebFlux + R2DBC"
    elif "virtual" in name:
        framework = "MVC + Virtual Threads"
    elif "mvc" in name or "jpa" in name:
        framework = "MVC + JPA"
    else:
        framework = name.split("-")[0].upper()

    if "io" in name:
        m = re.search(r"io[-_]?(\d+)", name)
        scenario = f"I/O {m.group(1)} ms" if m else "I/O"
    elif "cpu" in name:
        scenario = "CPU"
    elif "crud" in name:
        scenario = "CRUD"
    elif "smoke" in name:
        scenario = "Smoke"
    else:
        scenario = "Unknown"

    return framework, scenario


def safe_metric_value(metrics: dict[str, Any], metric_name: str, key: str, default: float = math.nan) -> float:
    try:
        value = metrics[metric_name][key]
        return float(value)
    except Exception:
        return default


def load_summary_files(results_dir: Path) -> pd.DataFrame:
    rows: list[dict[str, Any]] = []

    for path in sorted(results_dir.glob("*.json")):
        try:
            data = json.loads(path.read_text(encoding="utf-8"))
        except Exception as exc:
            print(f"[WARN] Cannot read {path}: {exc}")
            continue

        metrics = data.get("metrics", {})
        framework, scenario = parse_filename(path)

        row = {
            "file": path.name,
            "framework": framework,
            "scenario": scenario,
            "req_s": safe_metric_value(metrics, "http_reqs", "rate"),
            "duration_avg_ms": safe_metric_value(metrics, "http_req_duration", "avg"),
            "duration_med_ms": safe_metric_value(metrics, "http_req_duration", "med"),
            "duration_p90_ms": safe_metric_value(metrics, "http_req_duration", "p(90)"),
            "duration_p95_ms": safe_metric_value(metrics, "http_req_duration", "p(95)"),
            "duration_p99_ms": safe_metric_value(metrics, "http_req_duration", "p(99)"),
            "duration_max_ms": safe_metric_value(metrics, "http_req_duration", "max"),
            "error_rate": safe_metric_value(metrics, "http_req_failed", "rate"),
            "iterations": safe_metric_value(metrics, "iterations", "count"),
            "http_reqs": safe_metric_value(metrics, "http_reqs", "count"),
        }
        rows.append(row)

    return pd.DataFrame(rows)


def bar_chart(df: pd.DataFrame, metric: str, ylabel: str, title: str, out_file: Path) -> None:
    if df.empty or metric not in df.columns:
        return

    pivot = df.pivot_table(index="scenario", columns="framework", values=metric, aggfunc="mean")
    if pivot.empty:
        return

    ax = pivot.plot(kind="bar", figsize=(10, 5))
    ax.set_title(title)
    ax.set_xlabel("Scénario")
    ax.set_ylabel(ylabel)
    ax.legend(title="Version")
    plt.xticks(rotation=0)
    plt.tight_layout()
    plt.savefig(out_file, dpi=160)
    plt.close()


def load_k6_csv(path: Path) -> pd.DataFrame | None:
    try:
        df = pd.read_csv(path)
    except Exception as exc:
        print(f"[WARN] Cannot read CSV {path}: {exc}")
        return None

    if "timestamp" not in df.columns or "metric_name" not in df.columns or "metric_value" not in df.columns:
        print(f"[WARN] CSV {path} does not look like a k6 CSV output.")
        return None

    df["timestamp"] = pd.to_datetime(df["timestamp"], unit="s", errors="coerce")
    df = df.dropna(subset=["timestamp"])
    return df


def timeseries_duration_chart(csv_path: Path, out_dir: Path) -> None:
    df = load_k6_csv(csv_path)
    if df is None:
        return

    framework, scenario = parse_filename(csv_path)

    durations = df[df["metric_name"] == "http_req_duration"].copy()
    if durations.empty:
        return

    durations = durations.set_index("timestamp").sort_index()
    durations["metric_value"] = pd.to_numeric(durations["metric_value"], errors="coerce")
    durations = durations.dropna(subset=["metric_value"])

    # P95 by 10-second buckets
    p95 = durations["metric_value"].resample("10s").quantile(0.95).dropna()
    avg = durations["metric_value"].resample("10s").mean().dropna()

    if p95.empty:
        return

    plt.figure(figsize=(10, 5))
    plt.plot(p95.index, p95.values, label="P95")
    plt.plot(avg.index, avg.values, label="Moyenne")
    plt.title(f"Latence au cours du temps — {framework} — {scenario}")
    plt.xlabel("Temps")
    plt.ylabel("Latence HTTP (ms)")
    plt.legend()
    plt.xticks(rotation=30)
    plt.tight_layout()
    out_file = out_dir / f"{csv_path.stem}-latency-timeseries.png"
    plt.savefig(out_file, dpi=160)
    plt.close()


def timeseries_throughput_chart(csv_path: Path, out_dir: Path) -> None:
    df = load_k6_csv(csv_path)
    if df is None:
        return

    framework, scenario = parse_filename(csv_path)

    durations = df[df["metric_name"] == "http_req_duration"].copy()
    if durations.empty:
        return

    durations = durations.set_index("timestamp").sort_index()
    req_s = durations["metric_value"].resample("10s").count() / 10.0
    req_s = req_s.dropna()

    if req_s.empty:
        return

    plt.figure(figsize=(10, 5))
    plt.plot(req_s.index, req_s.values)
    plt.title(f"Débit au cours du temps — {framework} — {scenario}")
    plt.xlabel("Temps")
    plt.ylabel("Requêtes/s")
    plt.xticks(rotation=30)
    plt.tight_layout()
    out_file = out_dir / f"{csv_path.stem}-throughput-timeseries.png"
    plt.savefig(out_file, dpi=160)
    plt.close()


def write_summary_table(df: pd.DataFrame, out_dir: Path) -> None:
    if df.empty:
        return

    cols = [
        "framework",
        "scenario",
        "req_s",
        "duration_avg_ms",
        "duration_med_ms",
        "duration_p95_ms",
        "duration_p99_ms",
        "duration_max_ms",
        "error_rate",
        "http_reqs",
    ]
    available = [c for c in cols if c in df.columns]
    table = df[available].copy()

    for c in table.columns:
        if c not in {"framework", "scenario"}:
            table[c] = pd.to_numeric(table[c], errors="coerce").round(3)

    table.to_csv(out_dir / "summary-table.csv", index=False, encoding="utf-8")

    md = table.to_markdown(index=False)
    (out_dir / "summary-table.md").write_text(md + "\n", encoding="utf-8")


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--results-dir", default="benchmark/results")
    parser.add_argument("--out-dir", default="benchmark/results/graphs")
    args = parser.parse_args()

    results_dir = Path(args.results_dir)
    out_dir = Path(args.out_dir)
    out_dir.mkdir(parents=True, exist_ok=True)

    summaries = load_summary_files(results_dir)
    if summaries.empty:
        print(f"[WARN] No k6 summary JSON files found in {results_dir}.")
    else:
        write_summary_table(summaries, out_dir)
        bar_chart(
            summaries,
            "req_s",
            "Requêtes/s",
            "Débit moyen par scénario",
            out_dir / "summary-throughput.png",
        )
        bar_chart(
            summaries,
            "duration_avg_ms",
            "Latence moyenne (ms)",
            "Latence moyenne par scénario",
            out_dir / "summary-latency-avg.png",
        )
        bar_chart(
            summaries,
            "duration_p95_ms",
            "Latence P95 (ms)",
            "Latence P95 par scénario",
            out_dir / "summary-latency-p95.png",
        )
        bar_chart(
            summaries,
            "error_rate",
            "Taux d’erreur",
            "Taux d’erreur par scénario",
            out_dir / "summary-error-rate.png",
        )

    csv_files = sorted(results_dir.glob("*.csv"))
    if not csv_files:
        print(f"[INFO] No k6 CSV time-series files found in {results_dir}.")
    else:
        for csv_file in csv_files:
            timeseries_duration_chart(csv_file, out_dir)
            timeseries_throughput_chart(csv_file, out_dir)

    print(f"[OK] Graphs generated in: {out_dir.resolve()}")


if __name__ == "__main__":
    main()
