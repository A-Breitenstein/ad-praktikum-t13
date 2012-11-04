package aufgabe2.data;

import aufgabe2.interfaces.DataWrapper;
import org.junit.Before;
import org.junit.Test;

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
        int size = 10000;
        // static final test params in Reader.INTEGER_COUNT_PER_READ   entspricht der buffer größe
        String fileName = "losslessReadTest.file";
        TestFileGenerator.createSortedTestFile(fileName,size);
        FolgenReader fr = FolgenReader.create("test1",fileName);
        fr.setRunLevel(10);

        int[] array;
        DataWrapper dataWrap;
        int controll_counter = 0;
        boolean ausstieg = false;
        while (fr.HasNextFolge() && !ausstieg){
            dataWrap = fr.getFolge();
            array = dataWrap.getData();
            for (int i = 0; i < array.length; i++) {
                if(controll_counter!=array[i]){
                    System.out.println(array[i] +" != "+controll_counter+ "     data loss ...");
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
        FolgenReader fr = FolgenReader.create(filename,filename);
        DataWrapper wrap;
        int[] array;
        long counter = 0;
        while (fr.HasNextFolge()){
            wrap = fr.getFolge();
            array = wrap.getData();
            for (int i = 0; i <array.length; i++) {
                System.out.println("counter: "+counter+"  array["+i+"] = "+array[i]);
                counter++;
            }
        }
        System.out.println("controll counter = "+counter);
    }

}
