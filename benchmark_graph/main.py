import matplotlib.pyplot as plt
from pathlib import Path

import pandas as pd

BENCHMARK_FOLDER = Path("../yang/benchmark")
RESULT = "benchmark.csv"
USEFUL_COLUMNS = ["Score", "Unit", "Param: testCase"]


def get_folder_size_bytes(folder_path: Path) -> int:
  total = 0
  for file in folder_path.rglob("*"):
    if file.is_file():
      total += file.stat().st_size
  return total


def create_folder_size_df() -> pd.DataFrame:
  rows = []

  for folder in BENCHMARK_FOLDER.iterdir():
    if folder.is_dir():
      size_b = get_folder_size_bytes(folder)

      rows.append({
        "testCase": folder.name,
        "folderSizeB": size_b,
      })

  return pd.DataFrame(rows)


def clean_metric_df(metric_df: pd.DataFrame):
  return metric_df.rename(columns={
    "Score": "score",
    "Unit": "unit",
    "Param: testCase": "testCase"
  }).reset_index(drop=True)


def extract_metric(df: pd.DataFrame, col):
  return df[
    df["Benchmark"] == col
    ][USEFUL_COLUMNS].copy()


def create_df(df: pd.DataFrame, col):
  d = extract_metric(df, col)
  d = clean_metric_df(d)
  return d


def generate_graph(df: pd.DataFrame, x, y, output_file):
  plt.figure(figsize=(10, 6))

  plt.plot(df[x], df[y], marker="o")

  plt.xlabel(x)
  plt.ylabel(y)
  plt.title(f"{y} by {x}")

  plt.xticks(rotation=45, ha="right")
  plt.tight_layout()

  plt.savefig(output_file, dpi=200)
  plt.close()


def main():
  folder_size_df = create_folder_size_df()

  all_metrics = pd.read_csv(RESULT)

  load_time_df = create_df(all_metrics, "insa.benchmark.Benchmark.loadContext").merge(
    folder_size_df, how="inner", on="testCase")

  allocation_df = create_df(all_metrics, "insa.benchmark.Benchmark.loadContext:gc.alloc.rate.norm").merge(
    folder_size_df, how="inner", on="testCase"
  )

  object_memory_df = create_df(all_metrics, "Benchmark.objectMemory").merge(
    folder_size_df, how="inner", on="testCase"
  )

  generate_graph(load_time_df, "folderSizeB", "score","load_time.png")
  generate_graph(allocation_df, "folderSizeB", "score","allocation.png")
  generate_graph(object_memory_df, "folderSizeB", "score","memory.png")


if __name__ == "__main__":
  main()
