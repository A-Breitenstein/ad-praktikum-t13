package aufgabe2.experimentieren;

import java.util.Arrays;

import aufgabe2.algorithm.ExternerMergeSort;
import aufgabe2.data.TestFileGenerator;

public class Main {
	
	public static void main(String[] args) {
		String filePath = "C:\\Users\\abg690\\Desktop\\SourceFile";
		TestFileGenerator.createTestFile(filePath, 1024, 1);
		ExternerMergeSort.sort(filePath, filePath);
		TestFileGenerator.isSorted(filePath);
		
	}

}
