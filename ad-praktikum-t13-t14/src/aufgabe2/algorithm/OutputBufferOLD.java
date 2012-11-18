package aufgabe2.algorithm;

import aufgabe2.interfaces.DataManager;
import aufgabe2.interfaces.DataWrapper;

/**
 * Ermöglicht das Speichern eines ganzen Merge-Blocks. Die Klasse gibt die Daten (push) selbstständig
 * an den DataManager weiter, wenn der erworbene Speicher voll ist und holt sich in diesem Fall
 * neuen Speicher (createOuputChannel). Ist der Block fertig abgearbeitet, Close() aufrufen!  
 * @author Markus Bruhn
 *
 */
class OutputBufferOLD {
	
	private DataManager tapes; 
	private DataWrapper output; //Der Speicher, in welchem die Elemente gespeichert werden können
	private int pos = 0; //Der Index im Array, an welcher das nächste Element (bei Push) im output gespeichert werden werden kann
	int counter = 0; //temporär, für Debug...
	/**
	 * Erzeugt eine neue Instanz von OutputBuffer
	 * @param data Die Schnittstelle, an welche die Daten weitergeleitet werden sollen
	 */
	public OutputBufferOLD(DataManager data){
		tapes = data;
		createNextOutput();
	}
	
	/**
	 * Fügt eine Zahl in den Output am Ende hinzu
	 * @param val die hinzuzufügende Zahl
	 */
	public void push(int val){
		output.getData()[pos] = val;

        counter ++;
        pos++;
        if (pos == output.getData().length){ //ist der erhaltene Speicher voll?
            storeInTape(); //da Pos schon um 1 erhöht wurde, ist es damit die anzahl der Elemente
            createNextOutput();
        }
		
	}
	
	/**
	 * Schließt den Buffer. Dem DataMager wird damit auch signalisiert, dass der Block nun
	 * komplett übergeben wurde
	 */
	public void closeBuffer(){
		output.setFolgeKomplett(true);
		storeInTape();
		output = null; //nach dem Schließen des Buffers soll es nicht möglich sein, noch weitere Elemente über diese Instanz von OutputBuffer zu pushen.
	}
	
	/**
	 * Speichert die Daten im Output im DataManager
	 */
	private void storeInTape(){
		output.setSize(pos);//Da Pos den Index des NÄCHSTEN zu schreibendes Elementes enthält, ist Pos = Size  
		tapes.write(output);
	}
	
	/**
	 * Fordert einen neuen Speicher für die Werte an
	 */
	private void createNextOutput(){
		output = null; //Speicher zunächst freigeben, bevor das nächste z.B. 3 Gigabyte große Element angefordert wird
		output = tapes.createOuputChannel(); 
		pos = 0;
	}

}
