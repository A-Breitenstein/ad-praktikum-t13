package aufgabe2.data;

/**
 * Created with IntelliJ IDEA.
 * User: abg667
 * Date: 07.11.12
 * Time: 08:56
 */
import aufgabe2.algorithm.ExternerMergeSort;
import aufgabe2.interfaces.DataWrapper;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DataManagerTest {
    @Test
    public void DataManagerTest(){
        int[] array = {9,6,4,7,4,1,3,4,5,7,45,684,15,546,684,135,843,32,565,864,13,54,8,3,684,13,5464,1,654,1,6868,648,31};
        DataWrapper wrap = DataWrapperImpl.create(array,array.length);
        FolgenWriter writer = FolgenWriter.create("DataManagerTest");
        writer.writeFolge(wrap);
        writer.close();
        ExternerMergeSort.sort("DataManagerTest","");

//        FolgenReader reader = FolgenReader.create("DataManagerTest3","DataManagerTest3",10);
//        for(int elem : reader.getFolge().getData()){
//            System.out.println(elem);
//        }


    }
    @Test
    public void tenKIntegerTEst(){

        TestFileGenerator.createTestFile("DataManagerTest",1000,1);
        Reader.setInegerCountPerRead(2048);
        DataManagerImpl.setFolgenReaderInitValue(64);
        ExternerMergeSort.sort("DataManagerTest","");
        // zur kontrolle im ordner gucken welches file das größte ist und dann beim ReadTest
        // den filename angeben und kontrollieren lassen

    }
    @Test
    public void ReadTest(){
        FolgenReader reader = FolgenReader.create("DataManagerTest1","DataManagerTest1",1000);
        String filename = "DataManagerTest3";
        reader = FolgenReader.create(filename,filename,10000);
       while(reader.hasNextFolge()){
            for(int elem : reader.getFolge().getData()){
                System.out.println(elem);
            }
       }

        assertTrue(TestFileGenerator.isSorted(filename));
    }


}
