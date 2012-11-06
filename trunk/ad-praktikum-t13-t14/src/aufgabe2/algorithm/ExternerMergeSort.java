package aufgabe2.algorithm;

import aufgabe2.algorithm.impl.*;
import aufgabe2.data.DataManagerImpl;
import aufgabe2.interfaces.*;

public class ExternerMergeSort {

	
	
	public static void sort(String inputFile, String outputFile){
		DataManager tapes = new DataManagerImpl(); //InputFile Ã¼bergeben, Konstruktor mit angebbarem Dateinamen bitte? 
		
		//Blockweise Sortierung
		DataWrapper data = tapes.readBlock(); //lese von "band" 1; initialisierung
		while (data.getSize() > 0){ //solange das "band" nicht leer ist
			blockSort(data);//Sortieren
			tapes.write(data); // zurückschreiben
			data = tapes.readBlock(); //lese wieder von "band" 1
		}
		
		//Mergen
		while(merge(tapes)){//Merge für jeden Block aufrufen, bis keine Blöcke mehr kommen
			 //der merge tut schon alles, also do nothing
		}
		
	}
	
	private static void blockSort(DataWrapper data){
		/* Das Verfahren zum internen sortieren der speicherblÃ¶cke
		 *  benutzt momentan einen Insertionsort zum Sortieren.
		 *  Arbeitet direkt auf dem dataWrapper  
		 */
		
		int[] unpacked = data.getData();
		
		
		for(int i = 1  ; i <= unpacked.length; i++ ) {
			int j = i; 
			int itemToSort = unpacked[i]; 
			while(j >0 && unpacked[j-1] > itemToSort) {
				//insert
				unpacked[j] = unpacked[j-1]; 
				j = j-1; 
			}
			unpacked[j] = itemToSort; 
		}
		
	}
	
	  private static boolean merge(DataManager ioTapes) {
		  
		  InputBuffer linksIn  = new InputBuffer(ioTapes, InputBuffer.Channels.LEFTCHANNEL);
		  InputBuffer rechtsIn = new InputBuffer(ioTapes, InputBuffer.Channels.RIGHTCHANNEL);
          
		  OutputBuffer output = new OutputBuffer(ioTapes);
		  
		  if ((!linksIn.hasNext()) && (!rechtsIn.hasNext()))
			  return false; //Keine weiteren Blöcke, die Sortiert werden könnten
		  
		  while (linksIn.hasNext() && rechtsIn.hasNext()){
			  int linksElem = linksIn.getNext();
	          int rechtsElem = rechtsIn.getNext();
	          if (linksElem <= rechtsElem) {
	        	  output.push(linksElem);
	          } else {
	        	  output.push(rechtsElem);
	          }
		  }
		  
		  while (linksIn.hasNext())
			  output.push(linksIn.getNext());
		  
		  while (rechtsIn.hasNext())
			  output.push(rechtsIn.getNext());
		  
		  output.storeInTape();
		  
		  return true;
		  
		  /*
		  int[] links = linksIn.getData();
          int[] rechts = rechtsIn.getData();

		  int linksLänge = linksIn.getSize();
		  int rechtsLänge = rechtsIn.getSize();
	      int linksPos=0;
	      int rechtsPos=0;
	      int outputPos=0;
		  DataWrapper mergedDW = inputTapes.createDataWrapper();
	      int[] merged = mergedDW.getData();
	      //if (mergedDW.getSize() == merged.length)
	      
		  while ( linksPos < linksLänge && rechtsPos < rechtsLänge) {
			  int linksElem = links[linksPos];
	          int rechtsElem = rechts[rechtsPos];
	            
	          if (linksElem <= rechtsElem) {
	        	  merged[outputPos] = linksElem;
	        	  linksPos ++;
	        	  outputPos ++;
	        	  if (linksPos == linksLänge){
	        		  linksIn = inputTapes.readLeftChannel();
	        		  linksPos=0;
	        		  links = linksIn.getData();
	        		  linksLänge = linksIn.getSize();	  
	        	  }
	          } else {
	        	  merged[outputPos] = rechtsElem;
	        	  rechtsPos ++;
	        	  outputPos ++;
	        	  if (rechtsPos == rechtsLänge){
	        		  rechtsIn = inputTapes.readRightChannel();
	        		  rechtsPos=0;
	        		  rechts = rechtsIn.getData();
	        		  rechtsLänge = rechtsIn.getSize();	        	  }
	          }
	          if(outputPos == merged.length){
	        	  mergedDW.setSize(merged.length);
	        	  inputTapes.write(mergedDW);
	        	  mergedDW = null;
	        	  mergedDW = inputTapes.createDataWrapper();
	        	  outputPos = 0;
	          }*/
	        	  
	      }

}


