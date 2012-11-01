package aufgabe2.data;

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
        TestFileGenerator.createSortedTestFile("sortiert.file", 100000);
        assertEquals(true, TestFileGenerator.isSorted("sortiert.file"));
    }
    @Test
    public void testcreateTestFile() throws Exception {
        TestFileGenerator.createTestFile("testfile.file", 10000, 10);
        assertEquals(false, TestFileGenerator.isSorted("testfile.file"));
    }



}
