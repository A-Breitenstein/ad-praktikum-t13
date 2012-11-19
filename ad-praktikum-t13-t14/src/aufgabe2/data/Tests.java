package aufgabe2.data;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertTrue;
import static aufgabe2.data.Constants.*;
import org.junit.*;


public final class Tests {

	@Test 
	public void testValidBufferConstants() {
		//Positiver Wert?
		assertTrue(BUFFERSIZE_APPLICATION > 0);
		assertTrue(BUFFERSIZE_SORTARRAY > 0);
		assertTrue(BUFFERSIZE_SORTREAD > 0);
		assertTrue(BUFFERSIZE_SORTWRITE > 0);
		assertTrue(BUFFERSIZE_MERGEREAD > 0);
		assertTrue(BUFFERSIZE_MERGEWRITE > 0);
		assertTrue(INITBLOCKSIZE > 0);
		//Gültiger Wert (lassen sich dort ganze Integers speichern ohne Rest?)
		assertTrue(BUFFERSIZE_SORTARRAY % INTSIZE == 0);
		assertTrue(BUFFERSIZE_SORTREAD % INTSIZE == 0);
		assertTrue(BUFFERSIZE_SORTWRITE % INTSIZE == 0);
		assertTrue(BUFFERSIZE_MERGEREAD % INTSIZE == 0);
		assertTrue(BUFFERSIZE_MERGEWRITE % INTSIZE == 0);
		//Werden implementierungstechnische Limits nicht überschritten?
		assertTrue(INITBLOCKSIZE <= Integer.MAX_VALUE - 8); //Lässt sich ein Array mit dieser BlockSize überhaupt darstellen? Der genaue Wert ist von der Java-Maschine abhängig. Hier wird in Kommentar 9 zu MAXINTEGER-8 geraten: http://stackoverflow.com/questions/3038392/do-java-arrays-have-a-maximum-size
		assertTrue(INITBLOCKSIZE * INTSIZE == BUFFERSIZE_SORTARRAY);//Stimmt die Länge des Arrays mit dem dafür reserviertem Speicher überein?
		assertTrue(BUFFERSIZE_SORTREAD <= Integer.MAX_VALUE); //ByteBuffer.allocate lässt nur Integer zu!
		assertTrue(BUFFERSIZE_SORTWRITE <= Integer.MAX_VALUE);
		assertTrue(BUFFERSIZE_MERGEREAD <= Integer.MAX_VALUE);
		assertTrue(BUFFERSIZE_MERGEWRITE <= Integer.MAX_VALUE);
		//Wird die Gesammtkapazität des Speichers nicht überschritten? (Bedingungen können sich je nach Implementierungen ändern!)
		assertTrue( BUFFERSIZE_SORTARRAY + BUFFERSIZE_SORTREAD <= BUFFERSIZE_APPLICATION);
		assertTrue( BUFFERSIZE_SORTARRAY + BUFFERSIZE_SORTWRITE <= BUFFERSIZE_APPLICATION);
		assertTrue( BUFFERSIZE_MERGEREAD * 4 + BUFFERSIZE_MERGEWRITE * 4 <= BUFFERSIZE_APPLICATION); //Jeweils zwei Dateien und zwei Treads, also mal 4!
	}
	
}
