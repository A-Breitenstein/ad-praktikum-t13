package aufgabe2_old.data;

import aufgabe2.data.TestFileGenerator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Sven
 * Date: 31.10.12
 * Time: 23:46
 * To change this template use File | Settings | File Templates.
 */
public class TestFileGeneratorTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testChecker() throws Exception {
       aufgabe2.data.TestFileGenerator.createSortedTestFile("sortiert.file", 20000);
//        assertEquals(true, TestFileGenerator.isSorted_OLD("sortiert.file"));
        assertEquals(true, aufgabe2.data.TestFileGenerator.isSorted("sortiert.file"));
    }
    @Test
    public void testcreateTestFile() throws Exception {
        TestFileGenerator.createTestFile("C:\\Users\\abg667\\Desktop\\testfile.file", 160000000, 8);
//        assertEquals(false, TestFileGenerator.isSorted("testfile.file"));
    }



}
