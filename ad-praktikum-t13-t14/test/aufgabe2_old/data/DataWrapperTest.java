package aufgabe2_old.data;

import aufgabe2.interfaces.DataWrapper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
        dataWrapper = aufgabe2.data.DataWrapperImpl.create(data0, size0, false);
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
