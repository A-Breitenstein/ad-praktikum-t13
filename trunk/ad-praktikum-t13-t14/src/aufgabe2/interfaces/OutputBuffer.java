package aufgabe2.interfaces;

/**
 * Ermöglicht das Speichern eines ganzen Merge-Blocks. Die Klasse gibt die Daten (push) 
 * selbstständig an den DataManager weiter, und holt sich selber neuen Speicher. 
 * Ist der Block fertig abgearbeitet, finishBlock() aufrufen!  
 * @author Markus Bruhn
 *
 */
public interface OutputBuffer {

	/**
	 * Fügt eine Zahl in den Output am Ende hinzu
	 * @param val die hinzuzufügende Zahl
	 */
	public void push(int val);
	
	/**
	 * Signalisiert, dass der aktuelle Block über push nun vollständig übermittelt wurde
	 */
	public void finishBlock();
	
}
