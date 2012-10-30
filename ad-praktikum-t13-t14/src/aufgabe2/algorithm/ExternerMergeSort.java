package aufgabe2.algorithm;

import aufgabe2.data.DataWrapper;
import aufgabe2.data.DataWrapperImpl;

public class ExternerMergeSort {
	
	
	
	// kopiert von /spielerei/Mergesort.java
	
	  private static DataWrapper merge(DataWrapper links, DataWrapper rechts) {
	        int[] newList = new int[links.getSize()+rechts.getSize()];

	        int linksPos=0;
	        int rechtsPos=0;
	        
	        while ( linksPos < links.getSize() && rechtsPos < rechts.getSize()) 
	        {
	            int linksElem = links.getData()[linksPos];
	            int rechtsElem = rechts.getData()[rechtsPos];
	            
	            if (linksElem <= rechtsElem) {
	            	newList[linksPos + rechtsPos] = linksElem;
	            	linksPos ++;
	                
	            } else {
	            	newList[linksPos + rechtsPos] = rechtsElem;
	            	rechtsPos ++;
	            }
	        }

	        while (linksPos < links.getSize()) {
	        	newList[linksPos + rechtsPos] = links.getData()[linksPos];
            	linksPos ++;
	        }

	        while (rechtsPos < rechts.getSize()) {
	        	newList[linksPos + rechtsPos] =  rechts.getData()[rechtsPos];
            	rechtsPos ++;
	        }

	        return DataWrapperImpl.create(newList, links.getSize() + rechts.getSize());

	    }

}
