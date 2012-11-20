package aufgabe2_old.data;

/**
 * Created with IntelliJ IDEA.
 * User: abg667
 * Date: 07.11.12
 * Time: 08:56
 */
import aufgabe2.algorithm.ExternerMergeSort;
import aufgabe2.data.DataManagerImpl;
import aufgabe2.data.FolgenReader;
import aufgabe2.data.FolgenWriter;
import aufgabe2.data.Reader;
import aufgabe2.data.TestFileGenerator;
import aufgabe2.interfaces.DataWrapper;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DataManagerTest {
    @Test
    public void DataManagerTest(){
        int[] array = {9,6,4,7,4,1,3,4,5,7,45,684,15,546,684,135,843,32,565,864,13,54,8,3,684,13,5464,1,654,1,6868,648,31};
        DataWrapper wrap = aufgabe2.data.DataWrapperImpl.create(array, array.length);
        FolgenWriter writer = FolgenWriter.create("DataManagerTest");
        writer.writeFolge(wrap);
        writer.close();
        ExternerMergeSort.sort("DataManagerTest");

//        FolgenReader reader = FolgenReader.create("DataManagerTest3","DataManagerTest3",10);
//        for(int elem : reader.getFolge().getData()){
//            System.out.println(elem);
//        }


    }
    @Test
    public void tenKIntegerTEst(){
        // also die FolgenReaderInitValue muss ein vielfaches der gesamt folge sein!!
        // sonst bugt es und er hört nicht auf
        int anzahlZahlenProSchreibVorgang = 1000000;
        int anzahlSchreibVorgaenge = 10;
//        TestFileGenerator.createTestFile("DataManagerTest",anzahlZahlenProSchreibVorgang,anzahlSchreibVorgaenge);
        Reader.setInegerCountPerRead(2500000);   // 2097152 =>  ca 8mb lese buffer dadurch ist der schreibbuffer 24mb groß ( 3*8)
        DataManagerImpl.setFolgenReaderInitValue(1000000); // hier bitte ein vielfaches von anzahlZahlenProSchreibVorgang * anzahlSchreibVorgaenge
        ExternerMergeSort.sort("DataManagerTest");


        //assertTrue(TestFileGenerator.isSorted("EnddateiSorted"));

    }
    @Test
    public void ReadTest(){
        FolgenReader reader;
        String filename = "DataManagerTest2";
//        reader = FolgenReader.create(filename,filename,10000);
//      while(reader.hasNextFolge()){
//            for(int elem : reader.getFolge().getData()){
//                System.out.println(elem);
//            }
//       }

        assertTrue(TestFileGenerator.isSorted(filename));
    }


}
