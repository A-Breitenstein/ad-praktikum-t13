package aufgabe2.algorithm.parallel;

import java.nio.IntBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.management.InvalidApplicationException;

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
    public static int threadCountMax = 6; //Optimal schein Anzahl der Kerne * 1,33 (Markus hat 6 Kerne)// ab 50.000.000 unter 32 threads
    public Integer threads=0;
    private int depthMax = (int)(threadCountMax/2);
    private int dualPivotThreadCount = 18;
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
            //future = threadPool.submit(new quickSort(startLinks,startRechts));
            future = threadPool.submit(new DualPivotQuicksort(startLinks,startRechts));
            try {
                result = future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ExecutionException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }else {
               try {
				blockSort_quick(data,startLinks,startRechts);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

                synchronized (threads) {
                    threads -= 1;
                }

                   // if(depth<depthMax){
                boolean newThread = false;
                if (rechts - links > 100000){ //prüfen, ob es sich überhaupt lohnt, einen neuen Thread zu erstellen (nicht für 50 Elemente!)
                    synchronized (threads) {
                    	if(threads + 2 <= threadCountMax) {
                    		threads += 2;
                    		newThread = true;
                    	}
                    }
                }

                if(newThread){
                    Future<Boolean> resultLeft,resultRight;
                    resultLeft = threadPool.submit(new quickSort(links, positionPivot - 1));
                    resultRight = threadPool.submit(new quickSort(positionPivot + 1, rechts));
                    resultLeft.get();
                    resultRight.get();
                }else{
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
     *Sortiert die Daten von der linken bis zur rechten Grenze mit QuickSort
     * @param data das Array, auf welchem sortiert werden soll
     * @param links die linke Grenze (einschließlich), ab welcher mit der Sortierung begonnen werden soll
     * @param rechts die rechte Grenze (einschließlich), bis zu welcher sortiert werden soll
     * @return
     */
    public static void blockSort_quick_singleThreaded(IntBuffer data, int links, int rechts) {
        if (rechts - links < insertion_sort_grenze) {
            blockSort_insertion(data, links, rechts);
        } else {
            int positionPivot = quickSwap(data, links, rechts);
            blockSort_quick_singleThreaded(data, links, positionPivot - 1);
            blockSort_quick_singleThreaded(data, positionPivot + 1, rechts);
        }
    }



    /**
     * Sortiert die Daten von der linken bis zur rechten Grenze mit InsertionSort
     * @param data das Array, auf welchem sortiert werden soll
     * @param links die linke Grenze (einschließlich), ab welcher mit der Sortierung begonnen werden soll
     * @param rechts die rechte Grenze (einschließlich), bis zu welcher sortiert werden soll
     */
    public static void blockSort_insertion(IntBuffer data, int links, int rechts) {
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
     * @throws ExecutionException 
     * @throws InterruptedException 
     */
   public  void blockSort_quick(IntBuffer data, int links, int rechts) throws InterruptedException, ExecutionException {
        if (rechts - links < insertion_sort_grenze) {
            blockSort_insertion(data, links, rechts);
        } else {
            int positionPivot = quickSwap(data, links, rechts);
            
            boolean newThread = false;
            if (rechts - links > 100000){
                synchronized (threads) {
                	if(threads + 2 <= threadCountMax) {
                		threads += 2;
                		newThread = true;
                	}
                }
            }

            if(newThread){
                Future<Boolean> resultLeft,resultRight;
                resultLeft = threadPool.submit(new quickSort(links, positionPivot - 1));
                resultRight = threadPool.submit(new quickSort(positionPivot + 1, rechts));
                resultLeft.get();
                resultRight.get();
            }else{
                blockSort_quick(data, links, positionPivot - 1);
                blockSort_quick(data, positionPivot + 1, rechts);
            }
//            blockSort_quick(data, links, positionPivot - 1);
//            blockSort_quick(data, positionPivot + 1, rechts);
        }
    }

    /**
     * Hilfsmethode für blockSort_quick: Ermittelt ein Pivot-Element und sortiert die liste so, dass alle Elemente kleiner als das Pivot-Element links davon stehen, die größeren Element rechts
     * @param data das Array, auf welchem sortiert werden soll
     * @param links die linke Grenze (einschließlich), ab welcher mit der Sortierung begonnen werden soll
     * @param rechts die rechte Grenze (einschließlich), bis zu welcher sortiert werden soll
     * @return der Index des Pivot-ELements
     */
    public static int quickSwap(IntBuffer data, int links, int rechts) {

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

    //Dieser Algo ist Fehlerhaft und sollte desswegen nicht ausgeführt werden!
//    static int quickSwapMulitPivot(IntBuffer liste, int untereGrenze, int obereGrenze) {
//	    
//    	int links = untereGrenze;
//	    int rechts = obereGrenze;
//	    int pivot = liste.get(rechts);
//	    if (obereGrenze - untereGrenze >10000){
//	    	int p1 = liste.get(0);
//	    	int p2 = liste.get(((untereGrenze + obereGrenze) / 2));
//	    	int p3 = pivot;
//	    	
//	    	if(p1 > p2){
//	    		pivot = (p3 > p1 ? p1 : (p3 > p2 ? p3 : p2)); 
//	    	} else { //p1 <= p2
//	    		pivot = (p3 < p1 ? p1 : (p3 < p2 ? p3 : p2));
//	    	}
//	    	
//	    	//pivot = Math.min(pivot, Math.max(liste.get(0), liste.get(((untereGrenze + obereGrenze) / 2)))); 
//	    } 
//	    
//	    do {
//	      while (liste.get(links) < pivot) {
//	        links++;
//	      }
//	      while (pivot < liste.get(rechts)) {
//	        rechts--;
//	      }
//	      if (links <= rechts) {
//	    	  
//	        int tmp = liste.get(links);
//	        liste.put(links, liste.get(rechts));
//	        liste.put(rechts, tmp);
//	        links++;
//	        rechts--;
//	      }
//	    } while (links <= rechts);
//	    
//	    return rechts;
////	    if (untereGrenze < rechts) {
////	       quickSort2(liste, untereGrenze, rechts);
////	    }
////	    if (links < obereGrenze) {
////	        quickSort2(liste, links, obereGrenze);
////	    }
//	  }
    
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




    /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
    /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
    /*@@@@@@@@@@@@@@@@@@@@@@@@@@@ SINGLE THREAD VERSION DUAL PIVOT @@@@@@@@@@@@@@@@@@@@@@*/
    /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
    /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

      /*source => http://iaroslavski.narod.ru/quicksort/DualPivotQuicksort.pdf  */

    private static final int INSERTIONSORT_GRENZE = 10;
    private static final int DIST_SIZE = 13;

    // single thread version
    public static void dualPivotQuicksortSingleThreaded(IntBuffer data,int left,int right){
        int len = right - left;
        int x;
        if (len < INSERTIONSORT_GRENZE) { // insertion sort on tiny array
            QuickSortMultiThreaded.blockSort_insertion(data,left,right);
            return;
        }

        // median indexes
        int sixth = len / 6;
        int m1 = left + sixth;
        int m2 = m1 + sixth;
        int m3 = m2 + sixth;
        int m4 = m3 + sixth;
        int m5 = m4 + sixth;
// 5-element sorting network
        if (data.get(m1) > data.get(m2)) { x = data.get(m1); data.put(m1,data.get(m2)); data.put(m2,x); }
        if (data.get(m4) > data.get(m5)) { x = data.get(m4); data.put(m4,data.get(m5)); data.put(m5,x); }
        if (data.get(m1) > data.get(m3)) { x = data.get(m1); data.put(m1,data.get(m3)); data.put(m3,x); }
        if (data.get(m2) > data.get(m3)) { x = data.get(m2); data.put(m2,data.get(m3)); data.put(m3,x); }
        if (data.get(m1) > data.get(m4)) { x = data.get(m1); data.put(m1,data.get(m4)); data.put(m4,x); }
        if (data.get(m3) > data.get(m4)) { x = data.get(m3); data.put(m3,data.get(m4)); data.put(m4,x); }
        if (data.get(m2) > data.get(m5)) { x = data.get(m2); data.put(m2,data.get(m5)); data.put(m5,x); }
        if (data.get(m2) > data.get(m3)) { x = data.get(m2); data.put(m2,data.get(m3)); data.put(m3,x); }
        if (data.get(m4) > data.get(m5)) { x = data.get(m4); data.put(m4,data.get(m5)); data.put(m5,x); }

        // pivots: [ < pivot1 | pivot1 <= && <= pivot2 | > pivot2 ]
        int pivot1 = data.get(m2);
        int pivot2 = data.get(m4);
        boolean diffPivots = pivot1 != pivot2;
        data.put(m2,data.get(left));
        data.put(m4,data.get(right));
// center part pointers
        int less = left + 1;
        int great = right - 1;

        // sorting
        if (diffPivots) {
            for (int k = less; k <= great; k++) {
                x = data.get(k);
                if (x < pivot1) {
                    data.put(k,data.get(less));
                    data.put(less++,x);
                }
                else if (x > pivot2) {
                    while (data.get(great) > pivot2 && k < great) {
                        great--;
                    }
                    data.put(k,data.get(great));
                    data.put(great--,x);
                    x = data.get(k);
                    if (x < pivot1) {
                        data.put(k,data.get(less));
                        data.put(less++,x);
                    }
                }
            }
        }
        else {
            for (int k = less; k <= great; k++) {
                x = data.get(k);
                if (x == pivot1) {
                    continue;
                }
                if (x < pivot1) {
                    data.put(k,data.get(less));
                    data.put(less++, x);
                }
                else {
                    while (data.get(great) > pivot2 && k < great) {
                        great--;
                    }
                    data.put(k,data.get(great));
                    data.put(great--,x);
                    x = data.get(k);
                    if (x < pivot1) {
                        data.put(k,data.get(less));
                        data.put(less++,x);
                    }
                }
            }
        }

        // swap
        data.put(left,data.get(less - 1));
        data.put(less - 1,pivot1);
        data.put(right,data.get(great + 1));
        data.put(great + 1,pivot2);

        // left and right parts
        dualPivotQuicksortSingleThreaded(data, left, less - 2);
        dualPivotQuicksortSingleThreaded(data, great + 2, right);

        // equal elements
        if (great - less > len - DIST_SIZE && diffPivots) {
            for (int k = less; k <= great; k++) {
                x = data.get(k);
                if (x == pivot1) {
                    data.put(k,data.get(less));
                    data.put(less++, x);
                }
                else if (x == pivot2) {
                    data.put(k,data.get(great));
                    data.put(great--,x);
                    x = data.get(k);

                    if (x == pivot1) {
                        data.put(k,data.get(less));
                        data.put(less++, x);
                    }
                }
            }
        }
        // center part
        if (diffPivots) {
            dualPivotQuicksortSingleThreaded(data, less, great);
        }



    }
/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ MULTI THREADED DUAL PIOVT QUICKSORT @@@@@@@@*/
/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
class DualPivotQuicksort implements Callable {
    private int left;
    private int right;

    public DualPivotQuicksort( int left, int right) {
        this.left = left;
        this.right = right;
    }
    @Override
    public Object call() {
        return dualPivotQuicksort();
    }

    public boolean dualPivotQuicksort(){
        Thread.currentThread().setPriority(10);
        int len = right - left;
        int x;
        if (len < INSERTIONSORT_GRENZE) { // insertion sort on tiny array
            QuickSortMultiThreaded.blockSort_insertion(data, left, right);
            return true;
        }


        synchronized (threads) {
            threads -= 1;
        }

        boolean newThread = false;
        if (len > 100000){
            synchronized (threads) {
                if(threads + 2 <= dualPivotThreadCount) {
                    threads += 2;
                    newThread = true;
                }
            }
        }




//        if(depth < dualPivotThreadCount){
        if(newThread){
//            increaseDepth();
                // median indexes
                int sixth = len / 6;
                int m1 = left + sixth;
                int m2 = m1 + sixth;
                int m3 = m2 + sixth;
                int m4 = m3 + sixth;
                int m5 = m4 + sixth;
        // 5-element sorting network
                if (data.get(m1) > data.get(m2)) { x = data.get(m1); data.put(m1,data.get(m2)); data.put(m2,x); }
                if (data.get(m4) > data.get(m5)) { x = data.get(m4); data.put(m4,data.get(m5)); data.put(m5,x); }
                if (data.get(m1) > data.get(m3)) { x = data.get(m1); data.put(m1,data.get(m3)); data.put(m3,x); }
                if (data.get(m2) > data.get(m3)) { x = data.get(m2); data.put(m2,data.get(m3)); data.put(m3,x); }
                if (data.get(m1) > data.get(m4)) { x = data.get(m1); data.put(m1,data.get(m4)); data.put(m4,x); }
                if (data.get(m3) > data.get(m4)) { x = data.get(m3); data.put(m3,data.get(m4)); data.put(m4,x); }
                if (data.get(m2) > data.get(m5)) { x = data.get(m2); data.put(m2,data.get(m5)); data.put(m5,x); }
                if (data.get(m2) > data.get(m3)) { x = data.get(m2); data.put(m2,data.get(m3)); data.put(m3,x); }
                if (data.get(m4) > data.get(m5)) { x = data.get(m4); data.put(m4,data.get(m5)); data.put(m5,x); }

                // pivots: [ < pivot1 | pivot1 <= && <= pivot2 | > pivot2 ]
                int pivot1 = data.get(m2);
                int pivot2 = data.get(m4);
                boolean diffPivots = pivot1 != pivot2;
                data.put(m2,data.get(left));
                data.put(m4,data.get(right));
        // center part pointers
                int less = left + 1;
                int great = right - 1;

                // sorting
                if (diffPivots) {
                    for (int k = less; k <= great; k++) {
                        x = data.get(k);
                        if (x < pivot1) {
                            data.put(k,data.get(less));
                            data.put(less++,x);
                        }
                        else if (x > pivot2) {
                            while (data.get(great) > pivot2 && k < great) {
                                great--;
                            }
                            data.put(k,data.get(great));
                            data.put(great--,x);
                            x = data.get(k);
                            if (x < pivot1) {
                                data.put(k,data.get(less));
                                data.put(less++,x);
                            }
                        }
                    }
                }
                else {
                    for (int k = less; k <= great; k++) {
                        x = data.get(k);
                        if (x == pivot1) {
                            continue;
                        }
                        if (x < pivot1) {
                            data.put(k,data.get(less));
                            data.put(less++, x);
                        }
                        else {
                            while (data.get(great) > pivot2 && k < great) {
                                great--;
                            }
                            data.put(k,data.get(great));
                            data.put(great--,x);
                            x = data.get(k);
                            if (x < pivot1) {
                                data.put(k,data.get(less));
                                data.put(less++,x);
                            }
                        }
                    }
                }

                // swap
                data.put(left,data.get(less - 1));
                data.put(less - 1,pivot1);
                data.put(right,data.get(great + 1));
                data.put(great + 1,pivot2);
                Future<Boolean> part1 = null,part2 = null,part3 = null;
                // left and right parts
                part1 = threadPool.submit(new DualPivotQuicksort(left,less - 2));
                part2 = threadPool.submit(new DualPivotQuicksort(great + 2,right));
        //        dualPivotQuicksort(data, left, less - 2);
        //        dualPivotQuicksort(data, great + 2, right);

                // equal elements
                if (great - less > len - DIST_SIZE && diffPivots) {
                    for (int k = less; k <= great; k++) {
                        x = data.get(k);
                        if (x == pivot1) {
                            data.put(k,data.get(less));
                            data.put(less++, x);
                        }
                        else if (x == pivot2) {
                            data.put(k,data.get(great));
                            data.put(great--,x);
                            x = data.get(k);

                            if (x == pivot1) {
                                data.put(k,data.get(less));
                                data.put(less++, x);
                            }
                        }
                    }
                }
                // center part
                if (diffPivots) {
                    newThread = false;
                    synchronized (threads){
                        if(threads + 1 <= dualPivotThreadCount) {
                            threads += 1;
                            newThread = true;
                        }
                    }
                    if(newThread){
                        part3 = threadPool.submit(new DualPivotQuicksort(less,great));
                    }else
                        dualPivotQuicksortSingleThreaded(data,less,great);
        //            dualPivotQuicksort(data, less, great);

                }


                try {
                    // joining threads
                    if(part3 != null)
                        part3.get();
                    part2.get();
                    part1.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (ExecutionException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
//            decreaseDepth();
        }else{
            dualPivotQuicksortSingleThreaded(data, left, right);
        }
        return true;

    }



}


}
