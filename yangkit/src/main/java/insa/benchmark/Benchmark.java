package insa.benchmark;

import org.dom4j.DocumentException;
import org.openjdk.jmh.annotations.*;
import org.yangcentral.yangkit.model.api.schema.YangSchemaContext;
import org.yangcentral.yangkit.parser.YangParserException;
import org.yangcentral.yangkit.parser.YangYinParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@State(Scope.Benchmark)
public class Benchmark {

    @Param({
            "tc_00",
            "tc_01",
            "tc_02",
            "tc_03"
    })
    public String testCase;

    private File moduleDirectory;

    @Setup(Level.Trial)
    public void setup() {
        moduleDirectory = Paths.get("../yang/benchmark/" + testCase)
                .toAbsolutePath()
                .normalize()
                .toFile();
    }

    @org.openjdk.jmh.annotations.Benchmark
    public YangSchemaContext loadContext()
            throws DocumentException, IOException, YangParserException {
        return YangYinParser.parse(moduleDirectory);
    }
}