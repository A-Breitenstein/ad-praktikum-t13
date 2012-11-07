package aufgabe2.algorithm;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.*;

import aufgabe2.data.DWUtilityClass;
import aufgabe2.data.TestFileGenerator;
import aufgabe2.interfaces.DataWrapper;


public class ExternerMergeSortTest {
	

	@Test
	public void testBlockSort() {
		int[] testelems = {6,3,7,2,8,7,345,8,323, 7,3,0,7,23,6,7,-4,546,34};
		int[] expectedArr =Arrays.copyOf(testelems,testelems.length); 
		Arrays.sort(expectedArr); //Java-Standard-implementierung vom sortieren
		
		DataWrapper test = DWUtilityClass.createNewDataWrapper(testelems,testelems.length); 
		DataWrapper expected = DWUtilityClass.createNewDataWrapper(expectedArr, expectedArr.length);
		
		ExternerMergeSort.blockSort_insertion(test.getData(),0,testelems.length-1);
		assertEquals(expected, test); 
	}
	
	@Test
	public void testBlockSortEmpty() {
		int[] testelems = {};
		int[] expectedArr =Arrays.copyOf(testelems,testelems.length); 
		
		DataWrapper test = DWUtilityClass.createNewDataWrapper(testelems,testelems.length); 
		DataWrapper expected = DWUtilityClass.createNewDataWrapper(expectedArr, expectedArr.length);
		
		ExternerMergeSort.blockSort_insertion(test.getData(),0,testelems.length-1);
		assertEquals(expected, test); 
	}
	
	@Test
	public void testBlockSort_quick() {
		int[] testelems = {6,3,7,2,8,7,345,8,323, 7,3,0,7,23,6,7,-4,546,34,12};
		int[] expectedArr =Arrays.copyOf(testelems,testelems.length); 
		Arrays.sort(expectedArr); //Java-Standard-implementierung vom sortieren
		
		DataWrapper test = DWUtilityClass.createNewDataWrapper(testelems,testelems.length); 
		DataWrapper expected = DWUtilityClass.createNewDataWrapper(expectedArr, expectedArr.length);
		
		ExternerMergeSort.blockSort_quick(test.getData(),0,testelems.length-1);
		//System.out.println(Arrays.toString(test.getData()));
		assertEquals(expected, test); 
	}
	
	
	@Test //@Ignore 
	public void testMergeSortAlgorithm() {
		TestFileGenerator.createTestFile("DataManagerTest",100,10);
        ExternerMergeSort.sort("DataManagerTest","");
        assertTrue(TestFileGenerator.isSorted("DataManagerTest"));
	}

}
