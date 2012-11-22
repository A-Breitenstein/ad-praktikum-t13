package aufgabe2_old.algorithm;

import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: Sven
 * Date: 21.11.12
 * Time: 22:39
 */
abstract class ParallelSortStrategy implements AutoCloseable {
    private final String description;
    ExecutorService pool;

    ParallelSortStrategy(String algorithmName, int numberOfThreads) {
        description = algorithmName + ", Threads=" + numberOfThreads;
    }

    public abstract void sort(int[] a);

    public String getDescription() {
        return description;
    }

    @Override
    public void close() throws Exception {
        if (pool != null)
            pool.shutdown();
    }
}