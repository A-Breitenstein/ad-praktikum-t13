package aufgabe2.algorithm.test;

import static junit.framework.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import aufgabe2.algorithm.ExternerMergeSort;
//import aufgabe2.data.DataWrapperImpl;

// import aufgabe2.interfaces.DataWrapper;

public class ExternerMergeSortTest {
	

	@Test
	public void testBlockSort() {
		final int STOPPER = Integer.MIN_VALUE;
		int[] testelems = {6,3,7,2,8,7,345,8,323, 7,3,0,7,23,6,7,-4,546,34};
		
	//	DataWrapper t = DataWrapperImpl.create(testelems,testelems.length); 
	    int[] expected = Arrays.copyOf(testelems,testelems.length); 
	    
		Arrays.sort(expected); //Java-Standard-implementierung vom sortieren
	//    DataWrapper ex = DataWrapperImpl.create(expected); 
		
	    assertTrue(Arrays.equals(ExternerMergeSort.blockSort(testelems), expected)); 
	    		
	    		

	}

}
