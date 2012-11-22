package aufgabe2.algorithm.parallel.stolen;

/**
 * Created with IntelliJ IDEA.
 * User: Sven
 * Date: 21.11.12
 * Time: 22:40
 */
import java.nio.IntBuffer;
import java.util.concurrent.ForkJoinPool;

public class ForkJoinQuicksortStrategy extends ParallelSortStrategy {
    public ForkJoinQuicksortStrategy(int numberOfThreads) {
        super("Fork Join Quicksort", numberOfThreads);
        pool = new ForkJoinPool(numberOfThreads);
    }

    @Override
    public void sort(IntBuffer a) {
        Quicksort.forkJoinQuicksort((ForkJoinPool) pool, a);
    }
}