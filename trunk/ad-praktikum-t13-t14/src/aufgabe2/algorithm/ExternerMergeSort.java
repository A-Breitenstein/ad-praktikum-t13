package aufgabe2.algorithm;

import aufgabe2.data.DataManagerImpl;
import aufgabe2.interfaces.DataManager;
import aufgabe2.interfaces.DataWrapper;

public class ExternerMergeSort {

	
	
	public static void sort(String inputFile, String outputFile){
		DataManager tapes = new DataManagerImpl(); //InputFile übergeben, Konstruktor mit angebbarem Dateinamen bitte? 
		DataWrapper data = tapes.readBlock(); //lese von "band" 1; initialisierung
		while (data.getSize() > 0){ //solange das "band" nicht leer ist
			tapes.write(blockSort(data)); // kleine blöcke sortieren
			data = tapes.readBlock(); //lese wieder von "band" 1
		}
			
		
		//Sortieren
		//mergen
			
			
	}
	
	private static DataWrapper blockSort(DataWrapper input){ // später wieder zurück zu private
		/* Das Verfahren zum internen sortieren der speicherblöcke
		 *  benutzt momentan einen Insertionsort zum Sortieren.
		 */
		
		int[] unpacked = input.getData();
		
		
		for(int i = 1  ; i <= unpacked.length; i++ ) {
			int j = i; 
			int itemToSort = unpacked[i]; 
			while(unpacked[j-1] > itemToSort) {
				//insert
				unpacked[j] = unpacked[j-1]; 
				j = j-1; 
			}
			unpacked[j] = itemToSort; 
		}
		
		return input;
	}
	
	public static int[] blockSort(int[] input){ // zum testen
		/* Das Verfahren zum internen sortieren der speicherblöcke
		 *  benutzt momentan einen Insertionsort zum Sortieren.
		 *  
		 *  F�r die zu sortierenden Int-Arrays muss eines gelten: 
		 *  arr[0] ist ein Stopper-Element, d.h am besten die kleinste zahl �berhaupt.
		 */
		
		int[] unpacked = input;
		
		
		for(int i = 1 ; i < unpacked.length; i++ ) {
			int j = i; 
			int itemToSort = unpacked[i]; 
			while(unpacked[j-1] > itemToSort) { /* ohne stopper: while((unpacked[j-1] > itemToSort) && j > 1) */
				
				//insert
				unpacked[j] = unpacked[j-1]; 
				j = j-1; 
			}
			unpacked[j] = itemToSort; 
		}
		
		return input;
	}
	
	
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

	        return new DataManagerImpl().createDataWrapper(newList, links.getSize() + rechts.getSize());

	    }

}
