/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aufgabe2.algorithm.spielerei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * MergeSort Implementierung des Pseudocodes auf http://de.wikipedia.org/wiki/Mergesort
 * @author chrisch
 */
public class Mergesort {

    public static void main(String[] args) {

        List<Integer> tobesorted = Arrays.asList(35, 1, 2, 3, 70, 4, 0, 5, 2, 7, 25, -1, 8, 9, 11, 10);
        List<Integer> sorted = mergeSort(tobesorted);
        System.out.println(sorted);
    }

    private static List<Integer> mergeSort(List<Integer> toBeSorted) {
        int listSize = toBeSorted.size();
        if (listSize <= 1) {
            return toBeSorted; //einelementige liste ist sortiert
        } else {
            int hälfte = (listSize ) / 2;
            List<Integer> links =  toBeSorted.subList(0, hälfte);
            List<Integer> rechts = toBeSorted.subList(hälfte, listSize);
            links = mergeSort(links);
            rechts = mergeSort(rechts);

            return merge(links, rechts);
        }


    }

    private static List<Integer> merge(List<Integer> links, List<Integer> rechts) {
        List<Integer> newList = new ArrayList<>();

        while (!links.isEmpty() && !rechts.isEmpty()) // length(left) > 0 or length(right) > 0
        {
            int leftfirst = links.get(0);
            int rightfirst = rechts.get(0);
            if (leftfirst <= rightfirst) {
                newList.add(leftfirst);
                links = links.subList(1, links.size());
            } else {
                newList.add(rightfirst);
                rechts = rechts.subList(1, rechts.size()); 
            }


        }

        while (!links.isEmpty()) {
            newList.add(links.get(0));
            links = links.subList(1, links.size());
        }

        while (!rechts.isEmpty()) {
            newList.add(rechts.get(0));
            rechts = rechts.subList(1, rechts.size());
        }

        return newList;

    }
}
