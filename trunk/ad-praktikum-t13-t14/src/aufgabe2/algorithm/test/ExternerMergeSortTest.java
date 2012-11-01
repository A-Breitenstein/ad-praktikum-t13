package aufgabe2.algorithm.test;

import static org.junit.Assert.*;

import org.junit.Test;

import aufgabe2.algorithm.ExternerMergeSort;
//import aufgabe2.data.DataWrapperImpl;

import aufgabe2.interfaces.DataWrapper;

public class ExternerMergeSortTest {

	@Test
	public void testBlockSort() {
		int[] testelems = {6,3,7,2,8,7,345,8,323, 7,3,0,7,23,6,7,-4,546,34};
	//	DataWrapper t = DataWrapperImpl.create(testelems,testelems.length); 
	    int[] expected = {-4,0,2,3,3,6,6,7,7,7,7,8,8,23,34,323,345,546};
	//    DataWrapper ex = DataWrapperImpl.create(expected); 
	    assertEquals(ExternerMergeSort.blockSort(testelems),expected); 
	    		
	    		

	}

}
