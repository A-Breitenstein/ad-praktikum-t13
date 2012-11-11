package aufgabe2.algorithm;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;
import java.io.File;

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
	
	@Test @Ignore
	public void testBlockSort_Time() {
		long duration=0;
		int durchlaeufe = 30;
		for (int i=0; i<durchlaeufe; i++){
			int[] testelems = initRandomArray(1000000, 10000000, -1000000);
			DataWrapper test = DWUtilityClass.createNewDataWrapper(testelems,testelems.length); 
			long start = System.currentTimeMillis();
			ExternerMergeSort.blockSort_quick(test.getData(),0,testelems.length-1);
			duration += System.currentTimeMillis() - start;
			assertTrue(isSorted(testelems));
		}
		System.out.println("Dauer der Sortierung (durchschnittlich): " + (duration / durchlaeufe) + "ms");
	}
	
	
	@Test 
	public void testMergeSortAlgorithm() {
		String InputFilePath = "DataManagerTest";
		String outputFilePath = null;
		TestFileGenerator.createTestFile(InputFilePath,1000,10);
		outputFilePath = ExternerMergeSort.sort(InputFilePath);
		
        System.out.println("Sortieren abgeschlossen. Prüfe sortierung...");
        assertTrue(TestFileGenerator.isSorted(outputFilePath));
        
        //Aufräumen
        deleteFile(InputFilePath);
        deleteFile(outputFilePath);
	}
	
	private static void deleteFile(String path){
		File file = new File(path);
		if (file.exists())
			file.delete();
	}
	
	
    private static int[] initRandomArray(int arraySize, int upperBound, int lowerBound) {
        System.gc();
        int array[] = new int[arraySize];
        Random random = new Random();

        upperBound += (1 + Math.abs(lowerBound));

        for(int i = 0; i < array.length; i++){
            array[i] = random.nextInt(upperBound)+lowerBound;
        }

        return array;
    }
    private static boolean isSorted(int[] data){
    	if(data.length==0)
    		return true;
    	for (int i = 1; i < data.length; i++) {
			if(data[i-1]>data[i])
				return false;
		}
    	return true;
    }

}
