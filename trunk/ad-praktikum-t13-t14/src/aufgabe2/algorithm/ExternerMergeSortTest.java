package aufgabe2.algorithm;

import static junit.framework.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import aufgabe2.data.DWUtilityClass;
import aufgabe2.interfaces.DataWrapper;


public class ExternerMergeSortTest {
	

	@Test
	public void testBlockSort() {
		int[] testelems = {6,3,7,2,8,7,345,8,323, 7,3,0,7,23,6,7,-4,546,34};
		int[] expectedArr =Arrays.copyOf(testelems,testelems.length); 
		Arrays.sort(expectedArr); //Java-Standard-implementierung vom sortieren
		
		DataWrapper test = DWUtilityClass.createNewDataWrapper(testelems,testelems.length); 
		DataWrapper expected = DWUtilityClass.createNewDataWrapper(expectedArr, expectedArr.length);
		
		ExternerMergeSort.blockSort(test);
		assertEquals(expected, test); 
	}
	
	@Test
	public void testBlockSortEmpty() {
		int[] testelems = {};
		int[] expectedArr =Arrays.copyOf(testelems,testelems.length); 
		
		DataWrapper test = DWUtilityClass.createNewDataWrapper(testelems,testelems.length); 
		DataWrapper expected = DWUtilityClass.createNewDataWrapper(expectedArr, expectedArr.length);
		
		ExternerMergeSort.blockSort(test);
		assertEquals(expected, test); 
	}
	
	@Test 
	public void testMergeSortAlgorithm() {
		
	}

}
