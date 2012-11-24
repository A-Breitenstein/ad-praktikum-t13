package aufgabe2.algorithm.parallel;

import java.nio.IntBuffer;
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
    private IntBuffer data;
    private ExecutorService threadPool;
    private int startLinks,startRechts;
    private int depth = 0;
    public static int threadCountMax = 64; // ab 50.000.000 unter 32 threads
    public Integer threads=0;
    private int depthMax = (int)(threadCountMax/2);
    private static final int insertion_sort_grenze = 10;//47

    private synchronized void increaseDepth(){
        depth++;
    }
    private synchronized void decreaseDepth(){
        depth--;
    }
    private QuickSortMultiThreaded(IntBuffer data, int startLinks, int startRechts,ExecutorService threadPool) {
        this.data = data;
        this.threadPool = threadPool;
        this.startLinks = startLinks;
        this.startRechts = startRechts;
    }
    public static boolean sort(IntBuffer data, int startLinks, int startRechts,ExecutorService threadPool){
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
             Thread.currentThread().setPriority(10);
            if (rechts - links < insertion_sort_grenze) {
                blockSort_insertion(data, links, rechts);
//                threadPool.submit(new InsertionSort(links,rechts));
            } else {
                int positionPivot = quickSwap(data, links, rechts);

//                synchronized (threads) {
//                    threads -= 1;
//                }

                    if(depth<depthMax){
//                if(threads + 2 <= threadCountMax){//depth<depthMax){
//                    synchronized (threads) {
//						threads += 2;
//					}
                	increaseDepth();
                    Future<Boolean> resultLeft,resultRight;

                    
                    resultLeft = threadPool.submit(new quickSort(links, positionPivot - 1));
                    resultRight = threadPool.submit(new quickSort(positionPivot + 1, rechts));
//               System.out.println(Thread.currentThread().getName()+":  is waiting ...");
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
                int itemToSort = data.get(i);
                while (j > 0 && data.get(j - 1) > itemToSort) {
                    // insert
                    data.put(j, data.get(j - 1));
                    j = j - 1;
                }
                data.put(j, itemToSort);
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
    private static void blockSort_insertion(IntBuffer data, int links, int rechts) {
        for (int i = links + 1; i <= rechts; i++) {
            int j = i;
            int itemToSort = data.get(i);
            while (j > 0 && data.get(j - 1) > itemToSort) {
                // insert
                data.put(j, data.get(j - 1));
                j = j - 1;
            }
            data.put(j, itemToSort);
        }

    }

    /**
     *Sortiert die Daten von der linken bis zur rechten Grenze mit QuickSort
     * @param data das Array, auf welchem sortiert werden soll
     * @param links die linke Grenze (einschließlich), ab welcher mit der Sortierung begonnen werden soll
     * @param rechts die rechte Grenze (einschließlich), bis zu welcher sortiert werden soll
     * @return
     */
   public static void blockSort_quick(IntBuffer data, int links, int rechts) {
        if (rechts - links < insertion_sort_grenze) {
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
    private static int quickSwap(IntBuffer data, int links, int rechts) {

        int i = links;
        int j = rechts - 1; // Starte mit j links vom Pivotelement
        int pivot = data.get(rechts);
        while (i <= j) {
            while ((data.get(i) <= pivot) && (i < rechts))
                i++;
            while ((links <= j) && (data.get(j) > pivot))
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
    private static void swap(IntBuffer data, int pos1, int pos2) {
        int tmp = data.get(pos1);
        data.put(pos1, data.get(pos2));
        data.put(pos2, tmp);
    }




}
