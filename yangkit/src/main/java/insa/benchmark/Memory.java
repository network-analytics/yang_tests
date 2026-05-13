package insa.benchmark;

import org.openjdk.jol.info.GraphLayout;
import org.yangcentral.yangkit.model.api.schema.YangSchemaContext;
import org.yangcentral.yangkit.model.api.stmt.Leaf;
import org.yangcentral.yangkit.model.api.stmt.SchemaNode;
import org.yangcentral.yangkit.model.api.stmt.SchemaNodeContainer;
import org.yangcentral.yangkit.parser.YangYinParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

public class Memory {

    static String BENCHMARK_DIR = "../yang/benchmark/";

    public static void main(String[] args) throws Exception {
        System.out.println("Benchmark;(testCase);Mode;Cnt;Score;Error;Units");
        var testCases = getTestCases();
        for (String testCase : testCases) {
            printObjectMemory(testCase);
        }
    }

    private static List<String> getTestCases() throws IOException {
        try (var paths = Files.list(Path.of(BENCHMARK_DIR))) {
            return paths
                    .filter(Files::isDirectory)
                    .map(path -> path.getFileName().toString())
                    .sorted()
                    .toList();
        }
    }

    private static int countLeavesInContainer(SchemaNodeContainer container) {
        int count = 0;

        for (Object childObject : container.getEffectiveSchemaNodeChildren()) {
            if (!(childObject instanceof SchemaNode child)) {
                continue;
            }

            if (child instanceof Leaf) {
                count++;
            }

            if (child instanceof SchemaNodeContainer childContainer) {
                count += countLeavesInContainer(childContainer);
            }
        }

        return count;
    }

    private static void printObjectMemory(String testCase) throws Exception {
        YangSchemaContext context = YangYinParser.parse(
                Paths.get(BENCHMARK_DIR + testCase)
                        .toAbsolutePath()
                        .normalize()
                        .toFile()
        );


        context.validate();

        long objectSizeBytes = GraphLayout.parseInstance(context).totalSize();

        int nbImports = 0;
        for(var module : context.getModules()){
            nbImports += module.getImports().size();
        }

        int nbLeaves = 0;
        for(var module : context.getModules()){
            nbLeaves += countLeavesInContainer(module);
        }

        System.out.printf(
                Locale.US,
                "Benchmark.moduleCount,normal,1,1,%d,0,u,%s%n",
                context.getModules().size(),
                testCase
        );
        System.out.printf(
                Locale.US,
                "Benchmark.importCount,normal,1,1,%d,0,u,%s%n",
                nbImports,
                testCase
        );
        System.out.printf(
                Locale.US,
                "Benchmark.nbStatements,normal,1,1,%d,0,B,%s%n",
                nbLeaves,
                testCase
        );
        System.out.printf(
                Locale.US,
                "Benchmark.objectMemory,normal,1,1,%d,0,B,%s%n",
                objectSizeBytes,
                testCase
        );
    }
}