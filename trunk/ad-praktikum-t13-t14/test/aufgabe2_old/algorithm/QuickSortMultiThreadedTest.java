package aufgabe2_old.algorithm;

import aufgabe2.algorithm.ExternerMergeSort;
import aufgabe2.algorithm.QuickSortMultiThreaded;
import aufgabe2.data.FolgenReader;
import aufgabe2.data.Reader;
import aufgabe2.data.TestFileGenerator;
import aufgabe2.interfaces.DataWrapper;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: Sven
 * Date: 17.11.12
 * Time: 21:45
 */
public class QuickSortMultiThreadedTest {
    @Test
    public void testSort() throws Exception {
        // vm parameter -Xmx1024m nicht vergessen^^ gerne auch mehr

        int anzahlZahlenProSchreibVorgang = 5000000;
        int anzahlSchreibVorgaenge = 20;
        aufgabe2.algorithm.QuickSortMultiThreaded.threadCountMax = 64; // bei kleinerern Folgen weniger threads 32 16 8, aber min 8!
        int int_count = anzahlSchreibVorgaenge*anzahlZahlenProSchreibVorgang;
        String filename = "testSort";
        if(!Files.exists(Paths.get(filename))){
            TestFileGenerator.createTestFile(filename,anzahlZahlenProSchreibVorgang,anzahlSchreibVorgaenge);
        }
        Reader.setInegerCountPerRead(int_count);
        FolgenReader reader = FolgenReader.create(filename,filename,int_count);

        int[] folge1,folge2,folge3;
        DataWrapper data = reader.getFolge();
        folge1 = data.getData();
        folge2 = new int[folge1.length];
        folge3 = new int[folge1.length];

        System.arraycopy(folge1,0,folge2,0,folge1.length);
        System.arraycopy(folge1,0,folge3,0,folge1.length);
        ExecutorService threadPool = Executors.newCachedThreadPool();
        // ein vorlauf, damit der threadpool seine worker erstellen kann die dann
        // im nächsten durchlauf keine zeit mehr zum erstellen verbrauchen.
        aufgabe2.algorithm.QuickSortMultiThreaded.sort(folge1, 0, folge1.length - 1, threadPool);
        System.out.println("---- vorlauf beendet ----");
        long startTime,endTime;
        startTime = System.currentTimeMillis();
        QuickSortMultiThreaded.sort(folge2, 0, folge2.length - 1, threadPool);
        endTime = System.currentTimeMillis();
        System.out.println("ElapsedTime multi-threaded:"+(endTime-startTime));

        assertTrue(isSorted(folge2));
        Thread.currentThread().setPriority(10);
        startTime = System.currentTimeMillis();
        ExternerMergeSort.blockSort_quick(folge3,0,folge3.length-1);
        endTime = System.currentTimeMillis();
        System.out.println("ElapsedTime single-threaded:"+(endTime-startTime));
        threadPool.shutdown();

    }
    private boolean isSorted(int[] data){
        long controll_counter = 1;
        for (int i = 0; i < data.length-1; i++) {
            if(data[i]>data[i+1]){
                System.out.println("nicht sortiert!");
                System.out.println("Index i = "+i);
                System.out.println(data[i] + ">" + data[i+1]);
                return false;
            }
            controll_counter++;
        }
        System.out.println("controll_counter: "+controll_counter);
        return  true;
    }
}
