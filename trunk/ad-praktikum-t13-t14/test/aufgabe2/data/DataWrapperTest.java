package aufgabe2.data;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * AD-Praktikum
 * Team: 13
 * Date: 30.10.12
 * Time: 23:56
 */
public class DataWrapperTest {

    public int[] data0, data1;
    public int size0, size1;
    public DataWrapper dataWrapper;

    @Before
    public void setUp() throws Exception {
        data0 = new int[]{6};
        size0 = 4;
        dataWrapper = DataWrapperImpl.create(data0, size0);
    }

    @Test
    public void testGetData() throws Exception {
        data1 = dataWrapper.getData();

        assertEquals(data0,data1);
    }

    @Test
    public void testGetSize() throws Exception {
        size1 = dataWrapper.getSize();

        assertEquals(size0,size1);

    }
}
