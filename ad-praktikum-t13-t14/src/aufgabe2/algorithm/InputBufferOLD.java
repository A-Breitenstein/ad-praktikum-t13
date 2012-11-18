package aufgabe2.algorithm;

import aufgabe2.interfaces.*;

/**
 * Ermöglicht das elementweise Lesen eines kompletten Blocks aus dem DataManager.
 * Die Klasse fordert bei Bedarf selbstständig das nächste "Datenhäpchen" (readLeftChannel/readRightChannel)
 * an. Wenn hasCurrent() false ist, dann ist der komplette (linke/Rechte) Input des Blockes gelesen.
 * @author Markus Bruhn
 *
 */
class InputBufferOLD {

	public DataManager tapes;
	private Channels currentChannel; //Soll vom linkem oder rechtem InputChannel gelesen werden?
	private DataWrapper input; //Das aktuelle "Datenhäppchen"
	private int pos = 0; //Der Index im Array, an welchem das aktuelle Element gelesen werden kann
	
	/**
	 * Erzeugt eine neue Instanz von InputBuffer
	 * @param data Die Quelle für die Daten
	 * @param channel Legt fest,ob vom linken oder rechtem Channel gelesen werden soll (data.readLeftChannel/readRightChannel)
	 */
	public	InputBufferOLD(DataManager data, Channels channel){
		tapes = data;
		currentChannel = channel;
		naechstesHaeppchenVomMergeRun(); //erste Zahlenfolge einlesen
	}
	
	/**
	 * Die möglichen Input-Channels
	 * @author Markus Bruhn
	 *
	 */
	public enum Channels{
		LEFTCHANNEL,
		RIGHTCHANNEL
	}
	
	/**
	 * Gibt das aktuelle Element zurück. Wenn hasCurrent() == false, dann Fehler.
	 * @return Int: das Element an der aktuellen Lesepositin
	 */
	public int getCurrent(){
		return input.getData()[pos];
	}
	/**
	 * Gibt zurück, ob es noch ein aktuelles Element gibt, welches zurückgegeben werden kann
	 * @return
	 */
	public boolean hasCurrent(){
		return pos < input.getSize();
	}
	/**
	 * Verschiebt das aktuelle Element um 1 nach rechts. Es wird kein Fehler ausgegeben, wenn hasCurrent==false ist.
	 */
	public void moveNext(){
		pos ++;
		if (!hasCurrent()){
			naechstesHaeppchenVomMergeRun();
		}	
	}
	
	/**
	 * Versucht, das nächste Datenhäppchen zu lesen. Es wird kein Fehler ausgegeben
	 */
	private void naechstesHaeppchenVomMergeRun(){
        if (input == null || !input.isFolgeKomplett()) { //Wenn signalisiert wurde, dass der Block komplett übergeben wurde, dann keine weiteren Daten anfordern. Bei der Initialisierung ist input natürlich aber noch null. 
            input = null;
            input = (currentChannel == Channels.LEFTCHANNEL ? tapes.readLeftChannel() : tapes.readRightChannel());
        } else {
            input.setSize(0); //Bewirkt, dass hasCurrent ab jetzt False zurückliefert.
        }
		pos = 0;
	}
}
