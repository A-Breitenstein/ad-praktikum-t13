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
		assertTrue(BUFFERSIZE_APPLICATION > 0);
		assertTrue(BUFFERSIZE_SORTARRAY > 0);
		assertTrue(BUFFERSIZE_MERGEREAD > 0);
		assertTrue(BUFFERSIZE_MERGEWRITE > 0);
		//Gültiger Wert (lassen sich dort ganze Integers speichern ohne Rest?)
		assertTrue(BUFFERSIZE_SORTARRAY % INTSIZE == 0);
		assertTrue(BUFFERSIZE_MERGEREAD % INTSIZE == 0);
		assertTrue(BUFFERSIZE_MERGEWRITE % INTSIZE == 0);
		//Werden implementierungstechnische Limits nicht überschritten?
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

        if(!Files.exists(Paths.get(InputFilePath)))
            TestFileGenerator.createTestFile(InputFilePath,10000000,100);

		outputFilePath = ExternerMergeSort.sort(InputFilePath);
        System.out.println("Sortieren abgeschlossen. Prüfe Sortierung...");
        assertTrue(TestFileGenerator.isSorted(outputFilePath));

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

        *
        *
        **/
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

    @Test
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





    @Test
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



*/
    }
    @Test
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

    @Test
    public void quicksortMultithreadTest(){
        String InputFilePath = "DataManagerTest";
        long start,elapsed;

        if(!Files.exists(Paths.get(InputFilePath)))
            TestFileGenerator.createTestFile(InputFilePath,10000000,100);

        DataManagerImpl data = new DataManagerImpl(InputFilePath);
        ExecutorService threadPool = Executors.newCachedThreadPool();

        //vorlauf
        IntBuffer intbuff = data.readBlock().asIntBuffer();
        QuickSortMultiThreaded.sort(intbuff, 0,intbuff.limit()-1,threadPool );


        intbuff = data.readBlock().asIntBuffer();
        start = System.currentTimeMillis();
        QuickSortMultiThreaded.sort(intbuff, 0,intbuff.limit()-1,threadPool );
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

    @Test
    public void quicksortSingleThreadedTest(){
        String InputFilePath = "DataManagerTest";
        long start,elapsed;

        if(!Files.exists(Paths.get(InputFilePath)))
            TestFileGenerator.createTestFile(InputFilePath,10000000,100);

        DataManagerImpl data = new DataManagerImpl(InputFilePath);

        IntBuffer intbuff =data.readBlock().asIntBuffer();


        intbuff = data.readBlock().asIntBuffer();
        start = System.currentTimeMillis();
        // singleThread funktion
        QuickSortMultiThreaded.blockSort_quick(intbuff,0,intbuff.limit()-1);
        elapsed = System.currentTimeMillis() - start;
        System.out.println("elapsedTime: "+elapsed+" ms");

        /*
         SingleThreaded
         Beginne Sortierung von: 1.000.000.000 Integers
Initial Integers pro Block: 257.698.037
   Integers pro Merge-Read: 32.212.254
  Integers pro Merge-Write: 32.212.254
       Max Arbeitsspeicher: 983MB

elapsedTime: 57243 ms  */
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


}
