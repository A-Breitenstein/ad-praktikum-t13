package aufgabe2.algorithm;

import aufgabe2.data.DataManagerImpl;
import aufgabe2.interfaces.*;

public class ExternerMergeSort {

	public static void sort(String inputFile, String outputFile) {
		DataManager tapes = new DataManagerImpl(inputFile); // InputFile
															// übergeben,
															// Konstruktor mit
															// angebbarem
															// Dateinamen bitte?

		// Blockweise Sortierung
		DataWrapper data = tapes.readBlock(); // lese von "band" 1;
												// initialisierung
		while (data.getSize() > 0) { // solange das "band" nicht leer ist
			blockSort_insertion(data.getData(), 0, data.getSize()-1);// Sortieren
			tapes.write(data); // zur?ckschreiben
			data = tapes.readBlock(); //lese wieder von "band" 1
		}
		
		//Mergen
		while(merge(tapes)){//Merge für jeden Block aufrufen, bis keine Blöcke mehr kommen
			 //der merge tut schon alles, also do nothing
		}
		
	}

	// Auswählen, welcher sortieralgo für die blöcke verwendet wird:
	// IF blockgröße > 30 then blockSort_quick

	static void blockSort_insertion(int[] data, int links, int rechts) {
		for (int i = links + 1; i <= rechts; i++) {
			int j = i;
			int itemToSort = data[i];
			while (j > 0 && data[j - 1] > itemToSort) {
				// insert
				data[j] = data[j - 1];
				j = j - 1;
			}
			data[j] = itemToSort;
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
                  linksIn.pos++;
	          } else {
	        	  output.push(rechtsElem);
                  rechtsIn.pos++;
	          }
		  }
		  
		  while (linksIn.hasNext()){
			  output.push(linksIn.getNext());
              linksIn.pos++;
          }
		  
		  while (rechtsIn.hasNext()){
			  output.push(rechtsIn.getNext());
              rechtsIn.pos++;
          }
		  
		  output.storeInTape();
		  
		  return true;
	        	  
	      }

	static void blockSort_quick(int[] data, int links, int rechts) {
		if (rechts - links < 9) {
			blockSort_insertion(data, links, rechts);
		} else {
			int positionPivot = quickSwap(data, links, rechts);
			blockSort_quick(data, links, positionPivot - 1);
			blockSort_quick(data, positionPivot + 1, rechts);
		}
	}

	private static int quickSwap(int[] data, int links, int rechts) {

		int pivot = data[links];
		while (links < rechts) {
			if (data[links] < pivot) {
				links++;
				continue;
			}
			if (data[rechts] > pivot) {
				rechts--;
				continue;
			}
			// swap;
			int tmp = data[links];
			data[links] = data[rechts];
			data[rechts] = tmp;
			links++;
		}

		return links;

	}


}
