package aufgabe2.data;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertTrue;
import static aufgabe2.data.Constants.*;

import java.io.File;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import aufgabe2.algorithm.parallel.QuickSortMultiThreaded;
import aufgabe2.algorithm.parallel.stolen.Quicksort;
import org.junit.*;

import aufgabe2.algorithm.ExternerMergeSort;


public final class Tests {

    @Test
	public void testValidBufferConstants() {
		//Positiver Wert?
		assertTrue( BUFFERSIZE_APPLICATION > 0);
		assertTrue(BUFFERSIZE_SORTARRAY > 0);
		assertTrue(BUFFERSIZE_MERGEREAD > 0);
		assertTrue(BUFFERSIZE_MERGEWRITE > 0);
		assertTrue(MAXBYTESPERREADCALL > 0);
		//Gültiger Wert (lassen sich dort ganze Integers speichern ohne Rest?)
		assertTrue(BUFFERSIZE_SORTARRAY % INTSIZE == 0);
		assertTrue(BUFFERSIZE_MERGEREAD % INTSIZE == 0);
		assertTrue(BUFFERSIZE_MERGEWRITE % INTSIZE == 0);
		assertTrue(MAXBYTESPERREADCALL % INTSIZE == 0);
		//Wird die Gesammtkapazität des Speichers nicht überschritten? (Bedingungen können sich je nach Implementierungen ändern!)
		assertTrue(MAXBYTESPERREADCALL <= BUFFERSIZE_APPLICATION);
		assertTrue( BUFFERSIZE_SORTARRAY <= BUFFERSIZE_APPLICATION);
		assertTrue( BUFFERSIZE_SORTARRAY <= BUFFERSIZE_APPLICATION);
		assertTrue( BUFFERSIZE_MERGEREAD * 4 + BUFFERSIZE_MERGEWRITE * 4 <= BUFFERSIZE_APPLICATION); //Jeweils zwei Dateien und zwei Treads, also mal 4!
	}

    @Test @Ignore
	public void testTrend(){
    	String ausgabe ="zu Sortierende Zahlen;Anzahl Merge-Runs;Zugriffe insgesammt;";
    	
    	for(int anzahlElemente = 5; anzahlElemente<=3000; anzahlElemente+=30){
    		int anzahlZugriffe = 0;
    		int runlänge = 5;
    		int anzahlMergeRuns = 0;
    		anzahlZugriffe += anzahlElemente * 2; //init Run
    		
    		while (runlänge < anzahlElemente){
    			anzahlZugriffe += anzahlElemente * 2;
    			anzahlMergeRuns ++;
    			runlänge *= 2;
    		}
    		ausgabe += "\n" + anzahlElemente + ";" + anzahlMergeRuns + ";" + anzahlZugriffe + ";" ;
    	}
    	System.out.println(ausgabe);
    	
	}
    
    
	@Test
	public void testMergeSortAlgorithm() {
		//deleteFile("EnddateiSorted");
		String InputFilePath = "C:\\Users\\abg690\\Downloads\\DataManagerTest";
		String outputFilePath = null;

		
		if(!Files.exists(Paths.get(InputFilePath)))
            TestFileGenerator.createTestFile(InputFilePath,10000000,150);

		
		outputFilePath = ExternerMergeSort.sort(InputFilePath);
        System.out.println("Sortieren abgeschlossen. Prüfe Sortierung...");
        //outputFilePath ="EnddateiSorted";
        assertTrue(TestFileGenerator.isSorted("C:\\Users\\abg690\\Downloads\\EnddateiSorted")) ;//outputFilePath));

        /*
        mit 6 read calls
        *  Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 534.186.557
   Integers pro Merge-Read: 66.773.319
  Integers pro Merge-Write: 66.773.319
       Max Arbeitsspeicher: 2.037MB

04:21 - Diff 261.9s: QuickSort abgschlossen. Beginne ersten Merge-Schrit mit RunlÃ¤nge = 534.186.557
fertig
JobScheduler stopped.
06:39 - Diff 137.7s: Sortieren abgeschlossen! Die Ausgabedatei enthÃ¤lt 1.000.000.000 Integers.
Sortieren abgeschlossen. PrÃ¼fe Sortierung...
C:\Users\Sven\IdeaProjects\ad-praktikum-t13-t14\EnddateiSorted is sorted! congratulations
Anzahl der betrachteten Integer Zahlen: 1000000000
        *
        *
        *  mit nur einem readcall, hier kann man leider keinen unterschied in der quicksortphase sehen
          Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 534.186.557
   Integers pro Merge-Read: 66.773.319
  Integers pro Merge-Write: 66.773.319
       Max Arbeitsspeicher: 2.037MB

04:36 - Diff 276.4s: QuickSort abgschlossen. Beginne ersten Merge-Schrit mit RunlÃ¤nge = 534.186.557
fertig
JobScheduler stopped.
07:54 - Diff 197.4s: Sortieren abgeschlossen! Die Ausgabedatei enthÃ¤lt 1.000.000.000 Integers.
Sortieren abgeschlossen. PrÃ¼fe Sortierung...
C:\Users\Sven\IdeaProjects\ad-praktikum-t13-t14\EnddateiSorted is sorted! congratulations
Anzahl der betrachteten Integer Zahlen: 1000000000


                    32 read calls
              Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 268.435.456
   Integers pro Merge-Read: 33.554.432
  Integers pro Merge-Write: 33.554.432
       Max Arbeitsspeicher: 1.024MB

ElapsedTime quicksort: 26411 ms
ElapsedTime quicksort: 24538 ms
ElapsedTime quicksort: 28133 ms
ElapsedTime quicksort: 17417 ms
03:57 - Diff 237.5s: QuickSort abgschlossen. Beginne ersten Merge-Schrit mit RunlÃ¤nge = 268.435.456
06:14 - Diff 137.2s: Beginne Merge-Schritt mit RunlÃ¤nge = 536.870.912
fertig
JobScheduler stopped.
08:36 - Diff 141.6s: Sortieren abgeschlossen! Die Ausgabedatei enthÃ¤lt 1.000.000.000 Integers.

        *
        *        32readcalls für quicksort und 16 readcalls für merge
        *     Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 268.435.456
   Integers pro Merge-Read: 33.554.432
  Integers pro Merge-Write: 33.554.432
       Max Arbeitsspeicher: 1.024MB

readcall: 9265 ms
quicksort: 26581 ms
writecall: 17878 ms
readcall: 20120 ms
quicksort: 24900 ms
writecall: 17728 ms
readcall: 19294 ms
quicksort: 28973 ms
writecall: 17542 ms
readcall: 13862 ms
quicksort: 18569 ms
writecall: 12809 ms
readcall: 0 ms
03:48 - Diff 228.8s: QuickSort abgschlossen. Beginne ersten Merge-Schrit mit RunlÃ¤nge = 268.435.456
06:13 - Diff 144.2s: Beginne Merge-Schritt mit RunlÃ¤nge = 536.870.912
fertig
JobScheduler stopped.
08:44 - Diff 151.0s: Sortieren abgeschlossen! Die Ausgabedatei enthÃ¤lt 1.000.000.000 Integers.
Sortieren abgeschlossen. PrÃ¼fe Sortierung...
C:\Users\Sven\IdeaProjects\ad-praktikum-t13-t14\EnddateiSorted is sorted! congratulations
Anzahl der betrachteten Integer Zahlen: 1000000000
        *
        *
        * 32 readcalls quicksort 32 readcalls merge
        *     Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 268.435.456
   Integers pro Merge-Read: 33.554.432
  Integers pro Merge-Write: 33.554.432
       Max Arbeitsspeicher: 1.024MB

readcall: 20570 ms
quicksort: 26458 ms
writecall: 17491 ms
readcall: 19947 ms
quicksort: 31469 ms
writecall: 28599 ms
readcall: 19426 ms
quicksort: 24993 ms
writecall: 17623 ms
readcall: 13952 ms
quicksort: 22109 ms
writecall: 13802 ms
readcall: 0 ms
04:17 - Diff 257.3s: QuickSort abgschlossen. Beginne ersten Merge-Schrit mit RunlÃ¤nge = 268.435.456
06:42 - Diff 145.3s: Beginne Merge-Schritt mit RunlÃ¤nge = 536.870.912
fertig
JobScheduler stopped.
09:06 - Diff 144.1s: Sortieren abgeschlossen! Die Ausgabedatei enthÃ¤lt 1.000.000.000 Integers.
Sortieren abgeschlossen. PrÃ¼fe Sortierung...
C:\Users\Sven\IdeaProjects\ad-praktikum-t13-t14\En
        *
        *
        * quicksort readcalls 8 mergesort readcalls 8
        *
        *    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 134.217.728
   Integers pro Merge-Read: 16.777.216
  Integers pro Merge-Write: 16.777.216
       Max Arbeitsspeicher: 512MB

readcall: 10486 ms
quicksort: 21988 ms
writecall: 9429 ms
readcall: 10050 ms
quicksort: 11460 ms
writecall: 10349 ms
readcall: 10207 ms
quicksort: 11248 ms
writecall: 8981 ms
readcall: 9601 ms
quicksort: 12619 ms
writecall: 15302 ms
readcall: 9619 ms
quicksort: 11074 ms
writecall: 22918 ms
readcall: 9701 ms
quicksort: 12721 ms
writecall: 9376 ms
readcall: 9718 ms
quicksort: 16074 ms
writecall: 9576 ms
readcall: 4271 ms
quicksort: 5248 ms
writecall: 4071 ms
readcall: 0 ms
04:27 - Diff 267.5s: QuickSort abgschlossen. Beginne ersten Merge-Schrit mit RunlÃ¤nge = 134.217.728
06:55 - Diff 147.6s: Beginne Merge-Schritt mit RunlÃ¤nge = 268.435.456
09:21 - Diff 145.8s: Beginne Merge-Schritt mit RunlÃ¤nge = 536.870.912
fertig
JobScheduler stopped.
11:51 - Diff 150.9s: Sortieren abgeschlossen! Die Ausgabedatei enthÃ¤lt 1.000.000.000 Integers.
Sortieren abgeschlossen. PrÃ¼fe Sortierung...
C:\Users\Sven\IdeaProjects\ad-praktikum-t13-t14\EnddateiSorted is sorted! congratulations
Anzahl der betrachteten Integer Zahlen: 1000000000
        *
        * */
	}

	
	@SuppressWarnings("unused")
	private static void deleteFile(String path){
		File file = new File(path);
		if (file.exists())
			file.delete();
	}

    @Test @Ignore
    public void read2GBTest(){
        String InputFilePath = "DataManagerTest";
        long start,elapsed;

        if(!Files.exists(Paths.get(InputFilePath)))
            TestFileGenerator.createTestFile(InputFilePath,10000000,100);

        DataManagerImpl data = new DataManagerImpl(InputFilePath);
        start = System.currentTimeMillis();
        for (int i = 0; i <2 ; i++) {
            data.readBlock();
        }
        ;
        elapsed = System.currentTimeMillis() - start;
        System.out.println("ElapsedTime: " +elapsed +" ms");
    /* Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 526.133.493
   Integers pro Merge-Read: 65.766.686
  Integers pro Merge-Write: 65.766.686
       Max Arbeitsspeicher: 2.007MB     long BUFFERSIZE_APPLICATION = (long)(1.96 * 1024 * 1024 * 1024); // = 2(!) GB

ElapsedTime: 182475 ms verfälscht durch auslagerung

    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 534.186.557
   Integers pro Merge-Read: 66.773.319
  Integers pro Merge-Write: 66.773.319
       Max Arbeitsspeicher: 2.037MB

ElapsedTime: 143943 ms


JobScheduler started.
    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 534.186.557
   Integers pro Merge-Read: 66.773.319
  Integers pro Merge-Write: 66.773.319
       Max Arbeitsspeicher: 2.037MB

ElapsedTime: 100181 ms

    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 534.186.557
   Integers pro Merge-Read: 66.773.319
  Integers pro Merge-Write: 66.773.319
       Max Arbeitsspeicher: 2.037MB

ElapsedTime: 87991 ms


   Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 534.186.557
   Integers pro Merge-Read: 66.773.319
  Integers pro Merge-Write: 66.773.319
       Max Arbeitsspeicher: 2.037MB

ElapsedTime: 95482 ms

-------------------- neu -----------------
        mit 128 read calls
    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 534.186.557
   Integers pro Merge-Read: 66.773.319
  Integers pro Merge-Write: 66.773.319
       Max Arbeitsspeicher: 2.037MB

java.nio.DirectByteBuffer[pos=0 lim=2136746228 cap=2136746228]
java.nio.DirectByteBuffer[pos=0 lim=1863253772 cap=2136746228]

ElapsedTime: 75460 ms


            mit 8 read calls
    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 534.186.557
   Integers pro Merge-Read: 66.773.319
  Integers pro Merge-Write: 66.773.319
       Max Arbeitsspeicher: 2.037MB

java.nio.DirectByteBuffer[pos=0 lim=2136746228 cap=2136746228]
java.nio.DirectByteBuffer[pos=0 lim=1863253772 cap=2136746228]
ElapsedTime: 70056 ms


    mit 4 read calls
    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 534.186.557
   Integers pro Merge-Read: 66.773.319
  Integers pro Merge-Write: 66.773.319
       Max Arbeitsspeicher: 2.037MB

java.nio.DirectByteBuffer[pos=0 lim=2136746228 cap=2136746228]
java.nio.DirectByteBuffer[pos=0 lim=1863253772 cap=2136746228]
ElapsedTime: 70721 ms

       mit 6 read calls
    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 534.186.557
   Integers pro Merge-Read: 66.773.319
  Integers pro Merge-Write: 66.773.319
       Max Arbeitsspeicher: 2.037MB

java.nio.DirectByteBuffer[pos=0 lim=2136746228 cap=2136746228]
java.nio.DirectByteBuffer[pos=0 lim=1863253772 cap=2136746228]
ElapsedTime: 69967 ms


      mit einem read call
    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 534.186.557
   Integers pro Merge-Read: 66.773.319
  Integers pro Merge-Write: 66.773.319
       Max Arbeitsspeicher: 2.037MB

java.nio.DirectByteBuffer[pos=0 lim=2136746228 cap=2136746228]
java.nio.DirectByteBuffer[pos=0 lim=1863253772 cap=2136746228]
ElapsedTime: 100795 ms
*/
    }





    @Test @Ignore
    public void read1GBTest(){
        String InputFilePath = "DataManagerTest";
        long start,elapsed;

        if(!Files.exists(Paths.get(InputFilePath)))
            TestFileGenerator.createTestFile(InputFilePath,10000000,100);

        DataManagerImpl data = new DataManagerImpl(InputFilePath);
        start = System.currentTimeMillis();
        for (int i = 0; i <4 ; i++) {
            System.out.println(data.readBlock());

        }

        elapsed = System.currentTimeMillis() - start;
        System.out.println("ElapsedTime: " +elapsed +" ms");
        /*
    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 268.435.456
   Integers pro Merge-Read: 33.554.432
  Integers pro Merge-Write: 33.554.432
       Max Arbeitsspeicher: 1.024MB

ElapsedTime: 69697 ms

        *     Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 257.698.037
   Integers pro Merge-Read: 32.212.254
  Integers pro Merge-Write: 32.212.254
       Max Arbeitsspeicher: 983MB  long BUFFERSIZE_APPLICATION = (long)(0.96 * 1024 * 1024 * 1024); // = 2(!) GB

ElapsedTime: 69917 ms



    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 268.435.456
   Integers pro Merge-Read: 33.554.432
  Integers pro Merge-Write: 33.554.432
       Max Arbeitsspeicher: 1.024MB

ElapsedTime: 69488 ms

                mit 8 read calls
    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 268.435.456
   Integers pro Merge-Read: 33.554.432
  Integers pro Merge-Write: 33.554.432
       Max Arbeitsspeicher: 1.024MB

java.nio.DirectByteBuffer[pos=0 lim=1073741824 cap=1073741824]
java.nio.DirectByteBuffer[pos=0 lim=1073741824 cap=1073741824]
java.nio.DirectByteBuffer[pos=0 lim=1073741824 cap=1073741824]
java.nio.DirectByteBuffer[pos=0 lim=778774528 cap=1073741824]
ElapsedTime: 70178 ms

             16 read calls
    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 268.435.456
   Integers pro Merge-Read: 33.554.432
  Integers pro Merge-Write: 33.554.432
       Max Arbeitsspeicher: 1.024MB

java.nio.DirectByteBuffer[pos=0 lim=1073741824 cap=1073741824]
java.nio.DirectByteBuffer[pos=0 lim=1073741824 cap=1073741824]
java.nio.DirectByteBuffer[pos=0 lim=1073741824 cap=1073741824]
java.nio.DirectByteBuffer[pos=0 lim=778774528 cap=1073741824]
ElapsedTime: 72915 ms


 ------------>> interessant!! mit 32 read calls 20secs weniger? -------------
    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 268.435.456
   Integers pro Merge-Read: 33.554.432
  Integers pro Merge-Write: 33.554.432
       Max Arbeitsspeicher: 1.024MB

java.nio.DirectByteBuffer[pos=0 lim=1073741824 cap=1073741824]
java.nio.DirectByteBuffer[pos=0 lim=1073741824 cap=1073741824]
java.nio.DirectByteBuffer[pos=0 lim=1073741824 cap=1073741824]
java.nio.DirectByteBuffer[pos=0 lim=778774528 cap=1073741824]
ElapsedTime: 52419 ms

            nochmal mit 32 diesmal aber wieder schlechter....
            wahrscheinlich wurde irgendwas gecached
    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 268.435.456
   Integers pro Merge-Read: 33.554.432
  Integers pro Merge-Write: 33.554.432
       Max Arbeitsspeicher: 1.024MB

java.nio.DirectByteBuffer[pos=0 lim=1073741824 cap=1073741824]
java.nio.DirectByteBuffer[pos=0 lim=1073741824 cap=1073741824]
java.nio.DirectByteBuffer[pos=0 lim=1073741824 cap=1073741824]
java.nio.DirectByteBuffer[pos=0 lim=778774528 cap=1073741824]
ElapsedTime: 71761 ms

mit 128 read calls meine festplatte hat 8mb cache
    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 268.435.456
   Integers pro Merge-Read: 33.554.432
  Integers pro Merge-Write: 33.554.432
       Max Arbeitsspeicher: 1.024MB

java.nio.DirectByteBuffer[pos=0 lim=1073741824 cap=1073741824]
java.nio.DirectByteBuffer[pos=0 lim=1073741824 cap=1073741824]
java.nio.DirectByteBuffer[pos=0 lim=1073741824 cap=1073741824]
java.nio.DirectByteBuffer[pos=0 lim=778774528 cap=1073741824]
ElapsedTime: 68246 ms
                     mit 144 read calls was ca. 7,3mb entspricht weniger als mein festplatten cache
    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 268.435.456
   Integers pro Merge-Read: 33.554.432
  Integers pro Merge-Write: 33.554.432
       Max Arbeitsspeicher: 1.024MB

java.nio.DirectByteBuffer[pos=0 lim=1073741824 cap=1073741824]
java.nio.DirectByteBuffer[pos=0 lim=1073741824 cap=1073741824]
java.nio.DirectByteBuffer[pos=0 lim=1073741824 cap=1073741824]
java.nio.DirectByteBuffer[pos=0 lim=778774528 cap=1073741824]
ElapsedTime: 70982 ms


*/
    }
    @Test @Ignore
    public void read512MBTest(){
        String InputFilePath = "DataManagerTest";
        long start,elapsed;

        if(!Files.exists(Paths.get(InputFilePath)))
            TestFileGenerator.createTestFile(InputFilePath,10000000,100);

        DataManagerImpl data = new DataManagerImpl(InputFilePath);
        start = System.currentTimeMillis();
        for (int i = 0; i <8 ; i++) {
            data.readBlock();
        }
        elapsed = System.currentTimeMillis() - start;
        System.out.println("ElapsedTime: " +elapsed +" ms");
        /*
            Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 134.217.728
   Integers pro Merge-Read: 16.777.216
  Integers pro Merge-Write: 16.777.216
       Max Arbeitsspeicher: 512MB

ElapsedTime: 68932 ms

    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 134.217.728
   Integers pro Merge-Read: 16.777.216
  Integers pro Merge-Write: 16.777.216
       Max Arbeitsspeicher: 512MB

ElapsedTime: 69712 ms

        */
    }

    @Test @Ignore
    public void quicksortMultithreadTest(){
        String InputFilePath = "DataManagerTest";
        long start,elapsed;

        if(!Files.exists(Paths.get(InputFilePath)))
            TestFileGenerator.createTestFile(InputFilePath,10000000,100);

        DataManagerImpl data = new DataManagerImpl(InputFilePath);
        ExecutorService threadPool = Executors.newCachedThreadPool();
//        ExecutorService threadPool = new ForkJoinPool();

        //vorlauf
        IntBuffer intbuff = data.readBlock().asIntBuffer();
        QuickSortMultiThreaded.sort(intbuff, 0,intbuff.limit()-1,threadPool );
//        Quicksort.forkJoinQuicksort((ForkJoinPool) threadPool, intbuff);

        intbuff = data.readBlock().asIntBuffer();
        start = System.currentTimeMillis();
        QuickSortMultiThreaded.sort(intbuff, 0,intbuff.limit()-1,threadPool );
//        Quicksort.forkJoinQuicksort((ForkJoinPool) threadPool, intbuff);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("elapsedTime: "+elapsed+" ms");
         isSorted(intbuff);

        System.out.println(threadPool);
        /* Markus seine
         synchronized (threads) {
//						threads += 2;
//					}

        MultiThread
 Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 257.698.037
   Integers pro Merge-Read: 32.212.254
  Integers pro Merge-Write: 32.212.254
       Max Arbeitsspeicher: 983MB

elapsedTime: 36395 ms

JobScheduler started.
    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 257.698.037
   Integers pro Merge-Read: 32.212.254
  Integers pro Merge-Write: 32.212.254
       Max Arbeitsspeicher: 983MB

elapsedTime: 37252 ms

----------- cleaned


                  64threads
   Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 257.698.037
   Integers pro Merge-Read: 32.212.254
  Integers pro Merge-Write: 32.212.254
       Max Arbeitsspeicher: 983MB

elapsedTime: 26212 ms

    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 257.698.037
   Integers pro Merge-Read: 32.212.254
  Integers pro Merge-Write: 32.212.254
       Max Arbeitsspeicher: 983MB

elapsedTime: 24020 ms

         16threads
    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 257.698.037
   Integers pro Merge-Read: 32.212.254
  Integers pro Merge-Write: 32.212.254
       Max Arbeitsspeicher: 983MB

elapsedTime: 25731 ms
                  8  threads
    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 257.698.037
   Integers pro Merge-Read: 32.212.254
  Integers pro Merge-Write: 32.212.254
       Max Arbeitsspeicher: 983MB

elapsedTime: 26760 ms


    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 257.698.037
   Integers pro Merge-Read: 32.212.254
  Integers pro Merge-Write: 32.212.254
       Max Arbeitsspeicher: 983MB

elapsedTime: 24462 ms
java.util.concurrent.ThreadPoolExecutor@14b512cb[Running, pool size = 21, active threads = 0, queued tasks = 0, completed tasks = 58]








*/


        /*
            MulitThreaded alte Version
            Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 257.698.037
   Integers pro Merge-Read: 32.212.254
  Integers pro Merge-Write: 32.212.254
       Max Arbeitsspeicher: 983MB

elapsedTime: 23101 ms


    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 257.698.037
   Integers pro Merge-Read: 32.212.254
  Integers pro Merge-Write: 32.212.254
       Max Arbeitsspeicher: 983MB

elapsedTime: 23696 ms

    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 257.698.037
   Integers pro Merge-Read: 32.212.254
  Integers pro Merge-Write: 32.212.254
       Max Arbeitsspeicher: 983MB

elapsedTime: 24563 ms
java.util.concurrent.ThreadPoolExecutor@649096c6[Running, pool size = 241, active threads = 0, queued tasks = 0, completed tasks = 88850]


      insertionsort grenze 47
    Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 257.698.037
   Integers pro Merge-Read: 32.212.254
  Integers pro Merge-Write: 32.212.254
       Max Arbeitsspeicher: 983MB

elapsedTime: 23523 ms
java.util.concurrent.ThreadPoolExecutor@10094da[Running, pool size = 64, active threads = 0, queued tasks = 0, completed tasks = 1482]

insertionsort grenze 80
  Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 257.698.037
   Integers pro Merge-Read: 32.212.254
  Integers pro Merge-Write: 32.212.254
       Max Arbeitsspeicher: 983MB

elapsedTime: 24975 ms
java.util.concurrent.ThreadPoolExecutor@74dbe8cd[Running, pool size = 62, active threads = 0, queued tasks = 0, completed tasks = 2650]


*/




    }

    @Test @Ignore
    public void quicksortSingleThreadedTest(){
        String InputFilePath = "DataManagerTest";
        long start,elapsed;

        if(!Files.exists(Paths.get(InputFilePath)))
            TestFileGenerator.createTestFile(InputFilePath,10000000,100);

        DataManagerImpl data = new DataManagerImpl(InputFilePath);

        IntBuffer intbuff = data.readBlock().asIntBuffer();

        start = System.currentTimeMillis();
        // singleThread funktion
        QuickSortMultiThreaded.blockSort_quick_singleThreaded(intbuff,0,intbuff.limit()-1);
//        QuickSortMultiThreaded.dualPivotQuicksortSingleThreaded(intbuff,0,intbuff.limit()-1);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("elapsedTime: "+elapsed+" ms");
        isSorted(intbuff);

        /*
         SingleThreaded
         Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 257.698.037
   Integers pro Merge-Read: 32.212.254
  Integers pro Merge-Write: 32.212.254
       Max Arbeitsspeicher: 983MB

elapsedTime: 57243 ms

    Beginne Sortierung von: 100.000.000 Integers
Initial Integers pro Block: 51.002.736
   Integers pro Merge-Read: 6.375.342
  Integers pro Merge-Write: 6.375.342
       Max Arbeitsspeicher: 194MB

elapsedTime: 29922 ms
controll_counter: 51002736


    Beginne Sortierung von: 100.000.000 Integers
Initial Integers pro Block: 51.002.736
   Integers pro Merge-Read: 6.375.342
  Integers pro Merge-Write: 6.375.342
       Max Arbeitsspeicher: 194MB

elapsedTime: 29859 ms
controll_counter: 51002736



*/
    }

    private boolean isSorted(IntBuffer data){
        long controll_counter = 1;
        for (int i = 0; i < data.limit()-1; i++) {
            if(data.get(i)>data.get(i+1)){
                System.out.println("nicht sortiert!");
                System.out.println("Index i = "+i);
                System.out.println(data.get(i) + ">" + data.get(i+1));
                return false;
            }
            controll_counter++;
        }
        System.out.println("controll_counter: "+controll_counter);
        return  true;
    }

    @Test @Ignore
    public void dualPivotQuicksortSingleThreadedTest(){
        String InputFilePath = "DataManagerTest";
        long start,elapsed;

        if(!Files.exists(Paths.get(InputFilePath)))
            TestFileGenerator.createTestFile(InputFilePath,10000000,100);

        DataManagerImpl data = new DataManagerImpl(InputFilePath);

        IntBuffer intbuff = data.readBlock().asIntBuffer();

        start = System.currentTimeMillis();
        // singleThread funktion
        QuickSortMultiThreaded.dualPivotQuicksortSingleThreaded(intbuff,0,intbuff.limit() - 1);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("elapsedTime: "+elapsed+" ms");
        isSorted(intbuff);

        /*
        *
        *     Beginne Sortierung von: 100.000.000 Integers
Initial Integers pro Block: 51.002.736
   Integers pro Merge-Read: 6.375.342
  Integers pro Merge-Write: 6.375.342
       Max Arbeitsspeicher: 194MB

elapsedTime: 24672 ms
controll_counter: 51002736
        * */
    }
}
