package aufgabe2.data;

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
        Reader.INTEGER_COUNT_PER_READ = 5000;
        FolgenWriter fr = FolgenWriter.create("10kIntegerTest");
        fr.setFolgenLength(size);
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = i+1;
        }
        fr.writeFolge(array);


    }
}
