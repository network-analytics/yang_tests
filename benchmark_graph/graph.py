import re
from pathlib import Path

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd

RESULT = "benchmark.csv"
OUTPUT_DIR = Path("graphs")

METRIC_MAPPING = {
  "insa.benchmark.Benchmark.loadContext": "processTimeUs",
  "insa.benchmark.Benchmark.loadContext:gc.alloc.rate.norm": "allocationMemoryB",
  "Benchmark.objectMemory": "objectMemoryB",
  "Benchmark.moduleCount": "moduleCount",
  "Benchmark.importCount": "importCount",
  "Benchmark.leafCount": "leafCount",
}


def detect_family(test_case: str) -> str:
  test_case = str(test_case).lower()

  if "linear" in test_case:
    return "linear"
  if "star" in test_case:
    return "star"
  if "dense" in test_case:
    return "dense"
  if test_case == "tc_00":
    return "baseline"

  return "unknown"


def detect_experiment(test_case: str) -> str:
  test_case = str(test_case).lower()

  if test_case.startswith("tc_module_"):
    return "module"
  if test_case.startswith("tc_leaf_"):
    return "leaf"
  if test_case.startswith("tc_dependency_density_"):
    return "dependency_density"

  if test_case.startswith("tc_linear_") or test_case.startswith("tc_star_") or test_case.startswith("tc_dense_"):
    return "module"

  return "unknown"


def extract_dependency_density(test_case: str):
  match = re.search(r"_f(\d+)", str(test_case).lower())

  if match:
    return int(match.group(1))

  return None


def read_benchmark_results() -> pd.DataFrame:
  df = pd.read_csv(RESULT)

  df = df[df["Benchmark"].isin(METRIC_MAPPING.keys())].copy()

  df["metric"] = df["Benchmark"].map(METRIC_MAPPING)

  df = df[["Param: testCase", "metric", "Score"]].rename(columns={
    "Param: testCase": "testCase"
  })

  result_df = (
    df.pivot_table(
      index="testCase",
      columns="metric",
      values="Score",
      aggfunc="first"
    )
    .reset_index()
  )

  for column in METRIC_MAPPING.values():
    if column not in result_df.columns:
      result_df[column] = pd.NA

  result_df["family"] = result_df["testCase"].apply(detect_family)
  result_df["experiment"] = result_df["testCase"].apply(detect_experiment)
  result_df["dependency_density"] = result_df["testCase"].apply(extract_dependency_density)

  numeric_columns = [
    "moduleCount",
    "importCount",
    "leafCount",
    "objectMemoryB",
    "allocationMemoryB",
    "processTimeUs",
    "dependency_density",
  ]

  for column in numeric_columns:
    result_df[column] = pd.to_numeric(result_df[column], errors="coerce")

  result_df["processTimeMs"] = (result_df["processTimeUs"] / 1000).round(2)
  result_df["allocationMemoryMiB"] = (result_df["allocationMemoryB"] / 1024 / 1024).round(2)
  result_df["objectMemoryMiB"] = (result_df["objectMemoryB"] / 1024 / 1024).round(2)

  result_df["importsPerModule"] = (
      result_df["importCount"] / result_df["moduleCount"]
  ).round(2)

  result_df["leafPerModule"] = (
      result_df["leafCount"] / result_df["moduleCount"]
  ).round(2)

  result_df["timePerModuleMs"] = (
      result_df["processTimeMs"] / result_df["moduleCount"]
  ).round(4)

  result_df["timePerLeafMs"] = (
      result_df["processTimeMs"] / result_df["leafCount"]
  ).round(6)

  result_df = result_df[[
    "testCase",
    "family",
    "experiment",
    "moduleCount",
    "importCount",
    "leafCount",
    "dependency_density",
    "importsPerModule",
    "leafPerModule",
    "processTimeUs",
    "processTimeMs",
    "allocationMemoryB",
    "allocationMemoryMiB",
    "objectMemoryB",
    "objectMemoryMiB",
    "timePerModuleMs",
    "timePerLeafMs",
  ]]

  return result_df


def generate_graph_by_family(
    df: pd.DataFrame,
    x: str,
    y: str,
    output_file: str,
    title: str | None = None,
    x_label: str | None = None,
    y_label: str | None = None,
    log_y: bool = False,
    show_slope: bool = True
):
  plot_df = df.dropna(subset=[x, y]).copy()

  if plot_df.empty:
    print(f"Skip {output_file}: no data")
    return

  plt.figure(figsize=(10, 6))

  for family, group in plot_df.groupby("family"):
    group = group.sort_values(by=x)

    x_values = group[x].astype(float).to_numpy()
    y_values = group[y].astype(float).to_numpy()

    label = family

    if show_slope and len(group) >= 2:
      slope, intercept = np.polyfit(x_values, y_values, 1)
      label = f"{family} | slope={slope:.4f}"

    plt.plot(
      x_values,
      y_values,
      marker="o",
      label=label
    )

    if show_slope and len(group) >= 2:
      trendline = slope * x_values + intercept
      plt.plot(
        x_values,
        trendline,
        linestyle="--",
        alpha=0.6
      )

  plt.xlabel(x_label or x)
  plt.ylabel(y_label or y)
  plt.title(title or f"{y} by {x}")

  if log_y:
    plt.yscale("log")

  plt.legend(title="family")
  plt.tight_layout()

  plt.savefig(output_file, dpi=200)
  plt.close()

  print(f"Generated: {output_file}")


def generate_graphs(df: pd.DataFrame):
  OUTPUT_DIR.mkdir(exist_ok=True)

  module_df = df[df["experiment"] == "module"].copy()
  leaf_df = df[df["experiment"] == "leaf"].copy()
  dependency_density_df = df[df["experiment"] == "dependency_density"].copy()

  generate_graph_by_family(
    module_df,
    x="moduleCount",
    y="processTimeMs",
    output_file="graphs/module_count_vs_process_time.png",
    title="Process time by module count",
    x_label="Number of modules",
    y_label="Process time (ms/op)"
  )

  generate_graph_by_family(
    module_df,
    x="moduleCount",
    y="allocationMemoryMiB",
    output_file="graphs/module_count_vs_allocation_memory.png",
    title="Allocation memory by module count",
    x_label="Number of modules",
    y_label="Allocation memory (MiB/op)"
  )

  generate_graph_by_family(
    module_df,
    x="moduleCount",
    y="objectMemoryMiB",
    output_file="graphs/module_count_vs_object_memory.png",
    title="Object memory by module count",
    x_label="Number of modules",
    y_label="Object memory (MiB)"
  )

  generate_graph_by_family(
    dependency_density_df,
    x="dependency_density",
    y="processTimeMs",
    output_file="graphs/dependency_density_vs_process_time.png",
    title="Process time by dependency-density",
    x_label="dependency-density",
    y_label="Process time (ms/op)"
  )

  generate_graph_by_family(
    dependency_density_df,
    x="dependency_density",
    y="allocationMemoryMiB",
    output_file="graphs/dependency_density_vs_allocation_memory.png",
    title="Allocation memory by dependency-density",
    x_label="dependency-density",
    y_label="Allocation memory (MiB/op)"
  )

  generate_graph_by_family(
    dependency_density_df,
    x="dependency_density",
    y="objectMemoryMiB",
    output_file="graphs/dependency_density_vs_object_memory.png",
    title="Object memory by dependency-density",
    x_label="dependency-density",
    y_label="Object memory (MiB)"
  )


def main():
  df = read_benchmark_results()

  pd.set_option("display.max_rows", None)
  pd.set_option("display.max_columns", None)
  pd.set_option("display.width", None)

  print(df.to_string(index=False))

  df.to_csv("benchmark-summary.csv", index=False)

  generate_graphs(df)


if __name__ == "__main__":
  main()
