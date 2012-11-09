package aufgabe2.data;

import aufgabe2.interfaces.DataWrapper;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * AD-Praktikum
 * Team: 13
 * Date: 30.10.12
 * Time: 23:56
 */
public class FolgenReaderTest {


    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void losslessReadTest() throws Exception {
        int size = 5000;
        // static final test params in Reader.INTEGER_COUNT_PER_READ   entspricht der buffer größe
        String fileName = "losslessReadTest.file";
        TestFileGenerator.createSortedTestFile(fileName,size);
        FolgenReader fr = FolgenReader.create("test1",fileName,2400);
//        fr.setRunLevel(10);

        int[] array;
        DataWrapper dataWrap;
        int controll_counter = 0;
        boolean ausstieg = false;
        while (fr.hasNextFolge() && !ausstieg){
            dataWrap = fr.getFolge();
            array = dataWrap.getData();
            for (int anArray : array) {
                if (controll_counter != anArray) {
                    System.out.println(anArray + " != " + controll_counter + "     data loss ...");
                    ausstieg = true;
                    break;
                }
                controll_counter++;
            }
        }

        if(ausstieg){
            assertEquals(ausstieg,false);
        }else{
            System.out.println("no data loss");
            System.out.println("controll_counter: "+controll_counter + " size: "+size);
            assertEquals(true, true);
        }


    }
    @Test
    public void testReadRandomFile(){
        String filename = "testReadRandomFile";
       TestFileGenerator.createTestFile(filename,1000,25);
        Reader.INTEGER_COUNT_PER_READ = 1222;
        FolgenReader fr = FolgenReader.create(filename,filename,4);
        DataWrapper wrap;
        int[] array;
        long counter = 0;
        while (fr.hasNextFolge()){
            wrap = fr.getFolge();
            array = wrap.getData();
            for (int i = 0; i <array.length; i++) {
                System.out.println("counter: "+counter+"  array["+i+"] = "+array[i]);
                counter++;
            }
        }
        System.out.println("controll counter = "+counter);
    }
    @Test
    public void testFolgenEndeErreicht(){
        String filename = "testReadRandomFile";
        TestFileGenerator.createTestFile(filename,1000,1);

        FolgenReader fr = FolgenReader.create(filename,filename,16);
        DataWrapper wrap;
        wrap = fr.getFolge();
        System.out.println("folgeende erreicht? :"+wrap.isFolgeKomplett());
        wrap = fr.getFolge();
        System.out.println("folgeende erreicht? :"+wrap.isFolgeKomplett());
//        fr.resetFile();

        File file = new File("abc");
    }

    @Test
    public void test_readBigFile(){
        FolgenReader reader = FolgenReader.create("ding","C:\\Users\\abg667\\Desktop\\testfile.file",2500000000L);
        System.out.println(reader.getFileSize());
        int folgenzaehler = 0;
        DataWrapper wrapp;
        while (reader.hasNextFolge()){
            wrapp = reader.getFolge();
            if(!(wrapp.isFolgeKomplett())){
                folgenzaehler++;
                System.out.println("Folgenteil: " + folgenzaehler);
            }else{
                System.out.println("Folge Komplett ("+wrapp.getSize()+")");
                folgenzaehler = 0;
            }
        }
    }
}
