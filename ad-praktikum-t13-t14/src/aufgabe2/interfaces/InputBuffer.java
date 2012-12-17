package aufgabe2.interfaces;

/**
 * Ermöglicht das elementweise Lesen eines kompletten Blocks aus dem DataManager.
 * Die Klasse fordert bei Bedarf selbstständig das nächste "Datenhäpchen" (readLeftChannel/readRightChannel)
 * an. Wenn hasCurrent() false ist, dann ist der komplette (linke/Rechte) Input des Blockes gelesen.
 * @author Markus Bruhn
 *
 */
public interface InputBuffer {

	/**
	 * Gibt das aktuelle Element zurück. Wenn hasCurrent() == false, dann Fehler.
	 * @return Int: das Element an der aktuellen Lesepositin
	 */
	public int getCurrent();
	/**
	 * Gibt zurück, ob es noch ein aktuelles Element gibt, welches zurückgegeben werden kann
	 * @return
	 */
	public boolean hasCurrent();
	/**
	 * Verschiebt das aktuelle Element um 1 nach rechts. Es wird kein Fehler ausgegeben, wenn hasCurrent==false ist.
	 * @return ob es nun noch ein aktuelles Element gibt (äquvalent mit hasCurrent)
	 */
	public boolean moveNext();
	
}
