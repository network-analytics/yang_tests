package insa.benchmark;

import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class BenchmarkRunner {

    private static final Path BENCHMARK_DIR = Paths.get("../yang/benchmark")
            .toAbsolutePath()
            .normalize();

    public static void main(String[] args) throws Exception {
        String[] testCases = getTestCases().toArray(String[]::new);

        Options options = new OptionsBuilder()
                .include("insa\\.benchmark\\.Benchmark\\.loadContext")
                .param("testCase", testCases)
                .addProfiler(GCProfiler.class)
                .result("benchmark.csv")
                .resultFormat(ResultFormatType.CSV)
                .build();

        new Runner(options).run();
    }

    private static List<String> getTestCases() throws Exception {
        try (var paths = Files.list(BENCHMARK_DIR)) {
            return paths
                    .filter(Files::isDirectory)
                    .map(path -> path.getFileName().toString())
                    .sorted()
                    .toList();
        }
    }
}