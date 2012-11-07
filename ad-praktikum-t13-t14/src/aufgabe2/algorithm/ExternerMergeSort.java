package aufgabe2.algorithm;

import aufgabe2.data.DataManagerImpl;
import aufgabe2.interfaces.*;

public class ExternerMergeSort {

	
	
	public static void sort(String inputFile, String outputFile){
		DataManager tapes = new DataManagerImpl(inputFile); //InputFile übergeben, Konstruktor mit angebbarem Dateinamen bitte? 
		
		//Blockweise Sortierung
		DataWrapper data = tapes.readBlock(); //lese von "band" 1; initialisierung
		while (data.getSize() > 0){ //solange das "band" nicht leer ist
			blockSort(data);//Sortieren
			tapes.write(data); // zur�ckschreiben
			data = tapes.readBlock(); //lese wieder von "band" 1
		}
		
		//Mergen
		while(merge(tapes)){//Merge für jeden Block aufrufen, bis keine Blöcke mehr kommen
			 //der merge tut schon alles, also do nothing
		}
		
	}
	
	 static void blockSort(DataWrapper data){
		/* Das Verfahren zum internen sortieren der speicherblöcke
		 *  benutzt momentan einen Insertionsort zum Sortieren.
		 *  Arbeitet direkt auf dem dataWrapper  
		 */
		
		int[] unpacked = data.getData();
		
		
		for(int i = 1; i < data.getSize(); i++ ) {
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
	        	  
	      }

}


