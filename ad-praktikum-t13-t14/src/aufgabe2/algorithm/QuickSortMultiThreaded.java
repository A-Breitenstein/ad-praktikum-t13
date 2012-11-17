package aufgabe2.algorithm;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: Sven
 * Date: 17.11.12
 * Time: 17:15
 */
public class QuickSortMultiThreaded {
    private int[] data;
    private ExecutorService threadPool;
    private int startLinks,startRechts;
    private int depth = 0;
    public static int threadCountMax = 32; // ab 50.000.000 unter 32 threads
    private int depthMax = (int)(threadCountMax/2);

    private synchronized void increaseDepth(){
        depth++;
    }
    private synchronized void decreaseDepth(){
        depth--;
    }
    private QuickSortMultiThreaded(int[] data, int startLinks, int startRechts,ExecutorService threadPool) {
        this.data = data;
        this.threadPool = threadPool;
        this.startLinks = startLinks;
        this.startRechts = startRechts;
    }
    public static boolean sort(int[] data, int startLinks, int startRechts,ExecutorService threadPool){
        QuickSortMultiThreaded tmp = new QuickSortMultiThreaded(data, startLinks, startRechts, threadPool);
        return tmp.start();

    }
    private boolean start(){
        boolean result = false;
        if(startRechts> 10000){
            Future<Boolean> future;
            future = threadPool.submit(new quickSort(startLinks,startRechts));
            try {
                result = future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ExecutionException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }else {
               blockSort_quick(data,startLinks,startRechts);
        }
        return true;
    }

    class quickSort implements Callable{
        private int links, rechts;
        quickSort( int links, int rechts) {
            this.links = links;
            this.rechts = rechts;

        }

        @Override
        public Object call() throws Exception {

            if (rechts - links < 10) {
                blockSort_insertion(data, links, rechts);
//                threadPool.submit(new InsertionSort(links,rechts));
            } else {
                int positionPivot = quickSwap(data, links, rechts);
                if(depth<depthMax){
                    increaseDepth();
                    Future<Boolean> resultLeft,resultRight;

                    resultLeft = threadPool.submit(new quickSort(links, positionPivot - 1));
                    resultRight = threadPool.submit(new quickSort(positionPivot + 1, rechts));
//                    System.out.println(Thread.currentThread().getName()+":  is waiting ...");
                    resultLeft.get();
                    resultRight.get();
                     decreaseDepth();
                }else{
//                    System.out.println(Thread.currentThread().getName()+": is working ...");
                    blockSort_quick(data, links, positionPivot - 1);
                    blockSort_quick(data, positionPivot + 1, rechts);
                }
            }
            return true;
        }
    }

   class InsertionSort implements Callable{

        private int links,rechts;

        InsertionSort(int links, int rechts) {
            this.links = links;
            this.rechts = rechts;
        }

        @Override
        public Object call() throws Exception {
            for (int i = links + 1; i <= rechts; i++) {
                int j = i;
                int itemToSort = data[i];
                while (j > 0 && data[j - 1] > itemToSort) {
                    // insert
                    data[j] = data[j - 1];
                    j = j - 1;
                }
                data[j] = itemToSort;
            }
            return true;
        }
    }


    //Sortieralgorithmen-@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    /**
     * Sortiert die Daten von der linken bis zur rechten Grenze mit InsertionSort
     * @param data das Array, auf welchem sortiert werden soll
     * @param links die linke Grenze (einschließlich), ab welcher mit der Sortierung begonnen werden soll
     * @param rechts die rechte Grenze (einschließlich), bis zu welcher sortiert werden soll
     */
    private static void blockSort_insertion(int[] data, int links, int rechts) {
        for (int i = links + 1; i <= rechts; i++) {
            int j = i;
            int itemToSort = data[i];
            while (j > 0 && data[j - 1] > itemToSort) {
                // insert
                data[j] = data[j - 1];
                j = j - 1;
            }
            data[j] = itemToSort;
        }

    }

    /**
     *Sortiert die Daten von der linken bis zur rechten Grenze mit QuickSort
     * @param data das Array, auf welchem sortiert werden soll
     * @param links die linke Grenze (einschließlich), ab welcher mit der Sortierung begonnen werden soll
     * @param rechts die rechte Grenze (einschließlich), bis zu welcher sortiert werden soll
     * @return
     */
   private static void blockSort_quick(int[] data, int links, int rechts) {
        if (rechts - links < 10) {
            blockSort_insertion(data, links, rechts);
        } else {
            int positionPivot = quickSwap(data, links, rechts);
            blockSort_quick(data, links, positionPivot - 1);
            blockSort_quick(data, positionPivot + 1, rechts);
        }
    }

    /**
     * Hilfsmethode für blockSort_quick: Ermittelt ein Pivot-Element und sortiert die liste so, dass alle Elemente kleiner als das Pivot-Element links davon stehen, die größeren Element rechts
     * @param data das Array, auf welchem sortiert werden soll
     * @param links die linke Grenze (einschließlich), ab welcher mit der Sortierung begonnen werden soll
     * @param rechts die rechte Grenze (einschließlich), bis zu welcher sortiert werden soll
     * @return der Index des Pivot-ELements
     */
    private static int quickSwap(int[] data, int links, int rechts) {

        int i = links;
        int j = rechts - 1; // Starte mit j links vom Pivotelement
        int pivot = data[rechts];
        while (i <= j) {
            while ((data[i] <= pivot) && (i < rechts))
                i++;
            while ((links <= j) && (data[j] > pivot))
                j--;
            // a[j].key ≤ pivot
            if (i < j) {
                swap(data, i, j);
            }
        }
        swap(data, i, rechts); // Pivotelement in die Mitte tauschen
        return i;
    }

    /**
     * Hilfsmethode für quickSwap: vertauscht zwei Elemente miteinander
     * @param data das Array, auf welchem vertauscht werden soll
     * @param pos1 der Index des 1. Elements
     * @param pos2 der Index des 2. Elements
     */
    private static void swap(int[] data, int pos1, int pos2) {
        int tmp = data[pos1];
        data[pos1] = data[pos2];
        data[pos2] = tmp;
    }




}
