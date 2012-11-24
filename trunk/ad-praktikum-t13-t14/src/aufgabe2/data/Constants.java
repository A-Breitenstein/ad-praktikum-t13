package aufgabe2.data;

final class Constants {
	//Nach jeder Änderung in den Konstanten ist der Test in Tests.testValidBufferConstants aufzurufen!
	
	/**
	 * Die (maximale Größe) des Speichers in Bytes, die insgesammt vom Programm
	 * verwendet werden darf
	 */
	public static final long BUFFERSIZE_APPLICATION = (long)(0.5 * 1024 * 1024 * 1024); // = 2(!) GB
	//ACHTUNG: Die Reader scheinen den Doppelten Speicher noch einmal zu benötigen. Wenn wir 
	// das untersucht haben, kann die BufferSize korrekt eingestellt werden. Zur Zeit muss
	// so ziemlich der doppelte Arbeitsspeicherverbrauch einkalkuliert werden!!!
	
	/**
	 * Die Größe eines Integers in Bytes
	 */
	public static final int INTSIZE = 4; // ein Int benötigt 4 Bytes
	
	
	/**
	 * Die (maximale) Größe des Speichers in Bytes, der für das Arbeiten 
	 * auf dem Array genutzt werden darf.
	 */
	public static final int BUFFERSIZE_SORTARRAY = toValidIntSize(BUFFERSIZE_APPLICATION); //Gesamten Arbeitsspeicher verwenden!
	/**
	 * Die Größe der Runs beim ersten Durchlauf (=Sortieren)
	 */
	//public static final int INITBLOCKINTEGERS = (int)(BUFFERSIZE_SORTARRAY / 4);
			
	
	/**
	 * Die (maximale) Größe des Speichers in Bytes, der für einen LeseVorgang pro Datei und Tread
	 * genutzt werden darf
	 */
	public static final int BUFFERSIZE_MERGEREAD = toValidIntSize(BUFFERSIZE_APPLICATION / 8); //Anmerkung: es gibt zwei lesende Dateien und zwei Treads (Rechentread/ IO-Tread)
	/**
	 * Die (maximale) Größe des Speichers in Bytes, der für einen Schreibvorgang pro Datei und
	 * Thread genutzt werden darf
	 */
	public static final int BUFFERSIZE_MERGEWRITE = toValidIntSize(BUFFERSIZE_APPLICATION / 8); //Anmerkung: es gibt zwei schreibende Dateien (auch, wenn abwechselnd in diese geschrieben werden) und zwei Treads (Rechentread/ IO-Tread)
	
	
	
	/**
	 * Berechnet aus dem gegebenen Maximalen Speicher den maximalen Anteil, in welchen sich
	 * Integer-Zahlen dargestellt werden können, maximal INTEGER.MAXVALUE
	 * @param maxBufferSize
	 * @return int - Postcondition: return % INTSIZE == 0 && returnvalue + INTSIZE > maxBufferSize
	 */
	private static int toValidIntSize(long maxBufferSize){
		return ((int)Math.min(Integer.MAX_VALUE,maxBufferSize) / INTSIZE) * INTSIZE;
	}
	
	
}
