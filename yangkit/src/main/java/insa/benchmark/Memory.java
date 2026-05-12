package insa.benchmark;

import org.openjdk.jol.info.GraphLayout;
import org.yangcentral.yangkit.model.api.schema.YangSchemaContext;
import org.yangcentral.yangkit.parser.YangYinParser;

import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

public class Memory {

    static List<String> testCases = List.of("tc_00",
            "tc_01",
            "tc_02",
            "tc_03"
    );

    public static void main(String[] args) throws Exception {
        System.out.println("Benchmark;(testCase);Mode;Cnt;Score;Error;Units");

        for (String testCase : testCases) {
            printObjectMemory(testCase);
        }
    }

    private static void printObjectMemory(String testCase) throws Exception {
        YangSchemaContext context = YangYinParser.parse(
                Paths.get("../yang/benchmark/" + testCase)
                        .toAbsolutePath()
                        .normalize()
                        .toFile()
        );

        long objectSizeBytes = GraphLayout.parseInstance(context).totalSize();

        System.out.printf(
                Locale.US,
                "Benchmark.objectMemory,normal,1,1,%d,0,B,%s%n",
                objectSizeBytes,
                testCase
        );
    }
}