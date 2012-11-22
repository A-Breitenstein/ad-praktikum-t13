package aufgabe2.algorithm.parallel.stolen;

/**
 * Created with IntelliJ IDEA.
 * User: Sven
 * Date: 21.11.12
 * Time: 22:45
 */
import java.nio.IntBuffer;
import java.util.concurrent.ForkJoinPool;

/**
 * Convenience methods, in-place partition implementation
 */
public class Quicksort {

/*    *//**
     * Convenience method. Invokes a LatchQuickSortTask in the provided pool, blocking until done.
     * @param pool executes sorting tasks
     * @param a array to sort
     * @return sorted array
     * @throws InterruptedException thrown when the current thread is interrupted
     *//*
    public static int[] latchQuicksort(ExecutorService pool, int[] a) throws InterruptedException {
        LatchQuicksortTask sortingTask = new LatchQuicksortTask(a, pool);

        pool.execute(sortingTask);
        sortingTask.waitUntilSorted();

        return a;
    }*/

    /**
     * Convenience method. Invokes a ForkJoinQuickSortTask in the provided pool, blocking until done.
     * @param pool executes sorting tasks
     * @param a array to sort
     * @return sorted array
     */
    public static IntBuffer forkJoinQuicksort(ForkJoinPool pool, IntBuffer a) {
        pool.invoke(new ForkJoinQuicksortTask(a));
        return a;
    }

    /**
     * Example invocation:
     * <pre>
     * {@code
     * public static int[] sequentialQuicksort(int[] a, int left, int right) {
    while (left < right) {
    int pivotIndex = partition(a, left, right);
    sequentialQuicksort(a, left, pivotIndex);
    left = pivotIndex + 1;
    }

    return a;
    }
     * }
     * </pre>
     * @param a array to partition
     * @param left lower bound for partition
     * @param right upper bound for partition (inclusive)
     * @return pivot index - assert(a[i] < a[j]) for all i where {left <= i <= pivot}
     * and all j where {j > pivot}
     * @see ForkJoinQuicksortTask
     * @see LatchQuicksortTask
     */
    public static int partition(IntBuffer a, int left, int right) {
        // chose middle value of range for our pivot
        int pivotValue = a.get(middleIndex(left, right));

        --left;
        ++right;

        while (true) {
            do
                ++left;
            while (a.get(left) < pivotValue);

            do
                --right;
            while (a.get(right) > pivotValue);

            if (left < right) {
                int tmp = a.get(right);
                a.put(left,a.get(right));
                a.put(right,tmp);
            } else {
                return right;
            }
        }
    }

    // calculates middle index without integer overflow
    private static int middleIndex(int left, int right) {
        return left + (right - left) / 2;
    }


    ////@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    /// @@@@@@@@@@@@@@@ Copyed from Utils.arrays

    /**
     * The maximum number of runs in merge sort.
     */
    private static final int MAX_RUN_COUNT = 67;
    /**
     * The maximum length of run in merge sort.
     */
    private static final int MAX_RUN_LENGTH = 33;

    /**
     * If the length of an array to be sorted is less than this
     * constant, Quicksort is used in preference to merge sort.
     */
    private static final int QUICKSORT_THRESHOLD = 286;
    /**
     * If the length of an array to be sorted is less than this
     * constant, insertion sort is used in preference to Quicksort.
     */
    private static final int INSERTION_SORT_THRESHOLD = 47;


    /**
     * Sorts the specified range of the array.
     *
     * @param a the array to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted
     */
    public static void sort(IntBuffer a, int left, int right) {
        // Use Quicksort on small arrays
        if (right - left < QUICKSORT_THRESHOLD) {
            sort(a, left, right, true);
            return;
        }

        /*
         * Index run[i] is the start of i-th run
         * (ascending or descending sequence).
         */
        int[] run = new int[MAX_RUN_COUNT + 1];
        int count = 0; run[0] = left;

        // Check if the array is nearly sorted
        for (int k = left; k < right; run[count] = k) {
            if (a.get(k) < a.get(k+1)) { // ascending
                while (++k <= right && a.get(k - 1) <= a.get(k));
            } else if (a.get(k) > a.get(k + 1)) { // descending
                while (++k <= right && a.get(k-1) >= a.get(k));
                for (int lo = run[count] - 1, hi = k; ++lo < --hi; ) {
                    int t = a.get(lo); a.put(lo,a.get(hi));a.put(hi,t);
                }
            } else { // equal
                for (int m = MAX_RUN_LENGTH; ++k <= right && a.get(k-1)== a.get(k); ) {
                    if (--m == 0) {
                        sort(a, left, right, true);
                        return;
                    }
                }
            }

            /*
             * The array is not highly structured,
             * use Quicksort instead of merge sort.
             */
            if (++count == MAX_RUN_COUNT) {
                sort(a, left, right, true);
                return;
            }
        }

        // Check special cases
        if (run[count] == right++) { // The last run contains one element
            run[++count] = right;
        } else if (count == 1) { // The array is already sorted
            return;
        }



        //---------------- hier noch fixen ~~ ------------------

      /*
        *//*
         * Create temporary array, which is used for merging.
         * Implementation note: variable "right" is increased by 1.
         *//*
        int[] b; byte odd = 0;
        for (int n = 1; (n <<= 1) < count; odd ^= 1);

        if (odd == 0) {
            b = a; a = new int[b.length];

            for (int i = left - 1; ++i < right; a[i] = b[i]);
        } else {
            b = new int[a.length];
        }

        // Merging
        for (int last; count > 1; count = last) {
            for (int k = (last = 0) + 2; k <= count; k += 2) {
                int hi = run[k], mi = run[k - 1];
                for (int i = run[k - 2], p = i, q = mi; i < hi; ++i) {
                    if (q >= hi || p < mi && a[p] <= a[q]) {
                        b[i] = a[p++];
                    } else {
                        b[i] = a[q++];
                    }
                }
                run[++last] = hi;
            }
            if ((count & 1) != 0) {
                for (int i = right, lo = run[count - 1]; --i >= lo;
                     b[i] = a[i]
                        );
                run[++last] = right;
            }
            int[] t = a; a = b; b = t;
        }*/
    }

    /**
     * Sorts the specified range of the array by Dual-Pivot Quicksort.
     *
     * @param a the array to be sorted
     * @param left the index of the first element, inclusive, to be sorted
     * @param right the index of the last element, inclusive, to be sorted
     * @param leftmost indicates if this part is the leftmost in the range
     */
    public static void sort(IntBuffer a, int left, int right, boolean leftmost) {
        int length = right - left + 1;

        // Use insertion sort on tiny arrays
        if (length < INSERTION_SORT_THRESHOLD) {
            if (leftmost) {
                /*
                 * Traditional (without sentinel) insertion sort,
                 * optimized for server VM, is used in case of
                 * the leftmost part.
                 */
                for (int i = left, j = i; i < right; j = ++i) {
                    int ai = a.get(i+1);
                    while (ai < a.get(j)) {
                        a.put(j+1,a.get(j));
                        if (j-- == left) {
                            break;
                        }
                    }
                    a.put(j+1,ai);
                }
            } else {
                /*
                 * Skip the longest ascending sequence.
                 */
                do {
                    if (left >= right) {
                        return;
                    }
                } while (a.get(++left) >= a.get(left-1));

                /*
                 * Every element from adjoining part plays the role
                 * of sentinel, therefore this allows us to avoid the
                 * left range check on each iteration. Moreover, we use
                 * the more optimized algorithm, so called pair insertion
                 * sort, which is faster (in the context of Quicksort)
                 * than traditional implementation of insertion sort.
                 */
                for (int k = left; ++left <= right; k = ++left) {
                    int a1 = a.get(k), a2 = a.get(left);

                    if (a1 < a2) {
                        a2 = a1; a1 = a.get(left);
                    }
                    while (a1 < a.get(--k)) {
                        a.put(k+2,a.get(k));
                    }
                    a.put(++k + 1,a1);

                    while (a2 < a.get(--k)) {
                        a.put(k+1,a.get(k));
                    }
                    a.put(k+1,a2);
                }
                int last = a.get(right);

                while (last < a.get(--right)) {
                    a.put(right+1,a.get(right));
                }
                a.put(right+1,last);
            }
            return;
        }

        // Inexpensive approximation of length / 7
        int seventh = (length >> 3) + (length >> 6) + 1;

        /*
         * Sort five evenly spaced elements around (and including) the
         * center element in the range. These elements will be used for
         * pivot selection as described below. The choice for spacing
         * these elements was empirically determined to work well on
         * a wide variety of inputs.
         */
        int e3 = (left + right) >>> 1; // The midpoint
        int e2 = e3 - seventh;
        int e1 = e2 - seventh;
        int e4 = e3 + seventh;
        int e5 = e4 + seventh;

        // Sort these elements using insertion sort
        if (a.get(e2)< a.get(e1)) { int t = a.get(e2); a.put(e2,a.get(e1)); a.put(e1,t); }

        if (a.get(e3) < a.get(e2)) { int t = a.get(e3); a.put(e3,a.get(e2)); a.put(e2,t);
            if (t < a.get(e1)) {a.put(e2,a.get(e1)) ; a.put(e1,t); }
        }
        if (a.get(e4) < a.get(e3)) { int t = a.get(e4);a.put(e4,a.get(e3));a.put(e3,t);
            if (t < a.get(e2)) {a.put(e3,a.get(e2));a.put(e2,t);
                if (t < a.get(e1)) { a.put(e2,a.get(e1)); a.put(e1,t); }
            }
        }
        if (a.get(e5) < a.get(e4)) { int t = a.get(e5); a.put(e5,a.get(e4)); a.put(e4,t);
            if (t < a.get(e3)) {a.put(e4,a.get(e3));a.put(e3,t);
                if (t < a.get(e2)) { a.put(e3,a.get(e2)); a.put(e2,t);
                    if (t < a.get(e1)) {a.put(e2,a.get(e1)) ;a.put(e1,t); }
                }
            }
        }

        // Pointers
        int less  = left;  // The index of the first element of center part
        int great = right; // The index before the first element of right part

        if (a.get(e1) != a.get(e2) && a.get(e2) != a.get(e3) && a.get(e3) != a.get(e4) && a.get(e4) != a.get(e5)) {
            /*
             * Use the second and fourth of the five sorted elements as pivots.
             * These values are inexpensive approximations of the first and
             * second terciles of the array. Note that pivot1 <= pivot2.
             */
            int pivot1 = a.get(e2);
            int pivot2 = a.get(e4);

            /*
             * The first and the last elements to be sorted are moved to the
             * locations formerly occupied by the pivots. When partitioning
             * is complete, the pivots are swapped back into their final
             * positions, and excluded from subsequent sorting.
             */
            a.put(e2,a.get(left));
            a.put(e4,a.get(right));

            /*
             * Skip elements, which are less or greater than pivot values.
             */
            while (a.get(++less) < pivot1);
            while (a.get(--great) > pivot2);

            /*
             * Partitioning:
             *
             *   left part           center part                   right part
             * +--------------------------------------------------------------+
             * |  < pivot1  |  pivot1 <= && <= pivot2  |    ?    |  > pivot2  |
             * +--------------------------------------------------------------+
             *               ^                          ^       ^
             *               |                          |       |
             *              less                        k     great
             *
             * Invariants:
             *
             *              all in (left, less)   < pivot1
             *    pivot1 <= all in [less, k)     <= pivot2
             *              all in (great, right) > pivot2
             *
             * Pointer k is the first index of ?-part.
             */
            outer:
            for (int k = less - 1; ++k <= great; ) {
                int ak = a.get(k);
                if (ak < pivot1) { // Move a[k] to left part
                    a.put(k,a.get(less));
                    /*
                     * Here and below we use "a[i] = b; i++;" instead
                     * of "a[i++] = b;" due to performance issue.
                     */
                    a.put(less,ak);
                    ++less;
                } else if (ak > pivot2) { // Move a[k] to right part
                    while (a.get(great) > pivot2) {
                        if (great-- == k) {
                            break outer;
                        }
                    }
                    if (a.get(great) < pivot1) { // a[great] <= pivot2
                        a.put(k,a.get(less));
                        a.put(less,a.get(great));
                        ++less;
                    } else { // pivot1 <= a[great] <= pivot2
                        a.put(k,a.get(great));
                    }
                    /*
                     * Here and below we use "a[i] = b; i--;" instead
                     * of "a[i--] = b;" due to performance issue.
                     */
                    a.put(great,ak);
                    --great;
                }
            }

            // Swap pivots into their final positions
            a.put(left,a.get(less-1)); a.put(less-1,pivot1);
            a.put(right,a.get(great +1)); a.put(great + 1,pivot2);
            // Sort left and right parts recursively, excluding known pivots
            sort(a, left, less - 2, leftmost);
            sort(a, great + 2, right, false);

            /*
             * If center part is too large (comprises > 4/7 of the array),
             * swap internal pivot values to ends.
             */
            if (less < e1 && e5 < great) {
                /*
                 * Skip elements, which are equal to pivot values.
                 */
                while (a.get(less) == pivot1) {
                    ++less;
                }

                while (a.get(great) == pivot2) {
                    --great;
                }

                /*
                 * Partitioning:
                 *
                 *   left part         center part                  right part
                 * +----------------------------------------------------------+
                 * | == pivot1 |  pivot1 < && < pivot2  |    ?    | == pivot2 |
                 * +----------------------------------------------------------+
                 *              ^                        ^       ^
                 *              |                        |       |
                 *             less                      k     great
                 *
                 * Invariants:
                 *
                 *              all in (*,  less) == pivot1
                 *     pivot1 < all in [less,  k)  < pivot2
                 *              all in (great, *) == pivot2
                 *
                 * Pointer k is the first index of ?-part.
                 */
                outer:
                for (int k = less - 1; ++k <= great; ) {
                    int ak = a.get(k);
                    if (ak == pivot1) { // Move a[k] to left part
                        a.put(k,a.get(less));
                        a.put(less,ak);
                        ++less;
                    } else if (ak == pivot2) { // Move a[k] to right part
                        while (a.get(great) == pivot2) {
                            if (great-- == k) {
                                break outer;
                            }
                        }
                        if (a.get(great) == pivot1) { // a[great] < pivot2
                            a.put(k,a.get(less));
                            /*
                             * Even though a[great] equals to pivot1, the
                             * assignment a[less] = pivot1 may be incorrect,
                             * if a[great] and pivot1 are floating-point zeros
                             * of different signs. Therefore in float and
                             * double sorting methods we have to use more
                             * accurate assignment a[less] = a[great].
                             */
                            a.put(less,pivot1);
                            ++less;
                        } else { // pivot1 < a[great] < pivot2
                            a.put(k,a.get(great));
                        }
                        a.put(great,ak);
                        --great;
                    }
                }
            }

            // Sort center part recursively
            sort(a, less, great, false);

        } else { // Partitioning with one pivot
            /*
             * Use the third of the five sorted elements as pivot.
             * This value is inexpensive approximation of the median.
             */
            int pivot = a.get(e3);

            /*
             * Partitioning degenerates to the traditional 3-way
             * (or "Dutch National Flag") schema:
             *
             *   left part    center part              right part
             * +-------------------------------------------------+
             * |  < pivot  |   == pivot   |     ?    |  > pivot  |
             * +-------------------------------------------------+
             *              ^              ^        ^
             *              |              |        |
             *             less            k      great
             *
             * Invariants:
             *
             *   all in (left, less)   < pivot
             *   all in [less, k)     == pivot
             *   all in (great, right) > pivot
             *
             * Pointer k is the first index of ?-part.
             */
            for (int k = less; k <= great; ++k) {
                if (a.get(k) == pivot) {
                    continue;
                }
                int ak = a.get(k);
                if (ak < pivot) { // Move a[k] to left part
                    a.put(k,a.get(less));
                    a.put(less,ak);
                    ++less;
                } else { // a[k] > pivot - Move a[k] to right part
                    while (a.get(great) > pivot) {
                        --great;
                    }
                    if (a.get(great) < pivot) { // a[great] <= pivot
                        a.put(k,a.get(less));
                        a.put(less,a.get(great));
                        ++less;
                    } else { // a[great] == pivot
                        /*
                         * Even though a[great] equals to pivot, the
                         * assignment a[k] = pivot may be incorrect,
                         * if a[great] and pivot are floating-point
                         * zeros of different signs. Therefore in float
                         * and double sorting methods we have to use
                         * more accurate assignment a[k] = a[great].
                         */
                        a.put(k,pivot);
                    }
                    a.put(great,ak);
                    --great;
                }
            }

            /*
             * Sort left and right parts recursively.
             * All elements from center part are equal
             * and, therefore, already sorted.
             */
            sort(a, left, less - 1, leftmost);
            sort(a, great + 1, right, false);
        }
    }



}


