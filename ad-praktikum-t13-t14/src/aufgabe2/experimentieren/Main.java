package aufgabe2.experimentieren;

import java.util.Arrays;

import aufgabe2.data.TestFileGenerator;

public class Main {
	
	public static void main(String[] args) {
		String filePath = "aFile";
		TestFileGenerator.createTestFile(filePath, 1024, 1);
		System.out.println(TestFileGenerator.isSorted(filePath));
		
		TestFileGenerator.createSortedTestFile("bFile", 1024);
		TestFileGenerator.isSorted("bFile");
  
		
	}

}
