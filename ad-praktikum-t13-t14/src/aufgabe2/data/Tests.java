package aufgabe2.data;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertTrue;
import static aufgabe2.data.Constants.*;

import java.io.File;

import org.junit.*;

import aufgabe2.algorithm.ExternerMergeSort;


public final class Tests {

	@Test 
	public void testValidBufferConstants() {
		//Positiver Wert?
		assertTrue(BUFFERSIZE_APPLICATION > 0);
		assertTrue(BUFFERSIZE_SORTARRAY > 0);
		assertTrue(BUFFERSIZE_MERGEREAD > 0);
		assertTrue(BUFFERSIZE_MERGEWRITE > 0);
		assertTrue(INITBLOCKINTEGERS > 0);
		//Gültiger Wert (lassen sich dort ganze Integers speichern ohne Rest?)
		assertTrue(BUFFERSIZE_SORTARRAY % INTSIZE == 0);
		assertTrue(BUFFERSIZE_MERGEREAD % INTSIZE == 0);
		assertTrue(BUFFERSIZE_MERGEWRITE % INTSIZE == 0);
		//Werden implementierungstechnische Limits nicht überschritten?
		assertTrue(INITBLOCKINTEGERS <= Integer.MAX_VALUE - 8); //Lässt sich ein Array mit dieser BlockSize überhaupt darstellen? Der genaue Wert ist von der Java-Maschine abhängig. Hier wird in Kommentar 9 zu MAXINTEGER-8 geraten: http://stackoverflow.com/questions/3038392/do-java-arrays-have-a-maximum-size
		assertTrue(INITBLOCKINTEGERS * INTSIZE == BUFFERSIZE_SORTARRAY);//Stimmt die Länge des Arrays mit dem dafür reserviertem Speicher überein?
		assertTrue(BUFFERSIZE_MERGEREAD <= Integer.MAX_VALUE);
		assertTrue(BUFFERSIZE_MERGEWRITE <= Integer.MAX_VALUE);
		//Wird die Gesammtkapazität des Speichers nicht überschritten? (Bedingungen können sich je nach Implementierungen ändern!)
		assertTrue( BUFFERSIZE_SORTARRAY <= BUFFERSIZE_APPLICATION);
		assertTrue( BUFFERSIZE_SORTARRAY <= BUFFERSIZE_APPLICATION);
		assertTrue( BUFFERSIZE_MERGEREAD * 4 + BUFFERSIZE_MERGEWRITE * 4 <= BUFFERSIZE_APPLICATION); //Jeweils zwei Dateien und zwei Treads, also mal 4!
	}
	@Test
	public void testPrototype(){
		
	}
	
	@Test
	public void testMergeSortAlgorithm() {
		//deleteFile("EnddateiSorted");
		String InputFilePath = "DataManagerTest";
		String outputFilePath = null;
		
		//TestFileGenerator.createTestFile(InputFilePath,10000000,100);
		outputFilePath = ExternerMergeSort.sort(InputFilePath);
        System.out.println("Sortieren abgeschlossen. Prüfe Sortierung...");
        assertTrue(TestFileGenerator.isSorted(outputFilePath));
        
	}
	@Test
	public void testMergeSortAlgorithm2() {
	
        //assertTrue(TestFileGenerator.isSorted("Z:\\win7\\juno\\ADP2\\DataManagerTest3"));
        
        //Aufräumen
        //deleteFile(InputFilePath);
        //deleteFile(outputFilePath);
	}

	private static void deleteFile(String path){
		File file = new File(path);
		if (file.exists())
			file.delete();
	}
	
}
