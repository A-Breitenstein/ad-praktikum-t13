package aufgabe2_old.data;

import aufgabe2.data.FolgenReader;
import aufgabe2.data.FolgenWriter;
import aufgabe2.interfaces.DataWrapper;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Sven
 * Date: 04.11.12
 * Time: 20:34
 */
public class FolgeWriterTest {
    @Test
    public void testWrite10kInteger(){
        int size = 10000;
        aufgabe2.data.FolgenWriter fr = FolgenWriter.create("10kIntegerTest");
        fr.setFolgenLength(size);
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = i+1;
        }

        fr.writeFolge(aufgabe2.data.DataWrapperImpl.create(array, size, false));
        fr.close();
        FolgenReader reader = FolgenReader.create("10kIntegerTest","10kIntegerTest",10000);
        DataWrapper wrap;
        wrap = reader.getFolge();
        array = wrap.getData();
        for (int i = 0; i < array.length; i++) {
            System.out.println(array[i]);
        }

    }
}
