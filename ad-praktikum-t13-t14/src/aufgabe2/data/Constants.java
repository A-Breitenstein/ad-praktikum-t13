package aufgabe2.data;

final class Constants {
	//Nach jeder Änderung in den Konstanten ist der Test in Tests.testValidBufferConstants aufzurufen!
	
	/**
	 * Die (maximale Größe) des Speichers in Bytes, die insgesammt vom Programm
	 * verwendet werden darf
	 */
	public static final long BUFFERSIZE_APPLICATION = 3 * (long)(1024 * 1024 * 1024); // = 3 GB
	/**
	 * Die Größe eines Integers in Bytes
	 */
	public static final long INTSIZE = 4; // ein Int benötigt 4 Bytes
	
	
	/**
	 * Die (maximale) Größe des Speichers in Bytes, der für das Arbeiten 
	 * auf dem Array genutzt werden darf.
	 */
	public static final long BUFFERSIZE_SORTARRAY = toValidIntSize((long)(BUFFERSIZE_APPLICATION * 0.75)); //75% des Speichers für das arbeiten auf dem Array (um möglichst Große RunGröße zu ermöglichen)
	/**
	 * Die Größe der Runs beim ersten Durchlauf (=Sortieren)
	 */
	public static final int INITBLOCKSIZE = (int)(BUFFERSIZE_SORTARRAY / 4);
	
	/**
	 * Die (maximale) Größe des Speichers in Bytes, der für das Lesen
	 *(ByteBuffer) aus der Datei genutzt werden darf
	 */
	public static final long BUFFERSIZE_SORTREAD = toValidIntSize(BUFFERSIZE_APPLICATION - BUFFERSIZE_SORTARRAY); 
	/**
	 * Die (maximale) Größe des Speichers in Bytes, der für das Schreiben
	 * (ByteBuffer) in die Datei genutzt werden darf
	 */
	public static final long BUFFERSIZE_SORTWRITE = toValidIntSize(BUFFERSIZE_APPLICATION - BUFFERSIZE_SORTARRAY);
		
	
	/**
	 * Die (maximale) Größe des Speichers in Bytes, der für einen LeseVorgang pro Datei und Tread
	 * genutzt werden darf
	 */
	public static final long BUFFERSIZE_MERGEREAD = toValidIntSize(BUFFERSIZE_APPLICATION / 8); //Anmerkung: es gibt zwei lesende Dateien und zwei Treads (Rechentread/ IO-Tread)
	/**
	 * Die (maximale) Größe des Speichers in Bytes, der für einen Schreibvorgang pro Datei und
	 * Thread genutzt werden darf
	 */
	public static final long BUFFERSIZE_MERGEWRITE = toValidIntSize(BUFFERSIZE_APPLICATION / 8); //Anmerkung: es gibt zwei schreibende Dateien (auch, wenn abwechselnd in diese geschrieben werden) und zwei Treads (Rechentread/ IO-Tread)
	
	
	
	/**
	 * Berechnet aus dem gegebenen Maximalen Speicher den maximalen Anteil, in welchen sich
	 * Integer-Zahlen dargestellt werden können 
	 * @param maxBufferSize
	 * @return int - Postcondition: return % INTSIZE == 0 && returnvalue + INTSIZE > maxBufferSize
	 */
	private static long toValidIntSize(long maxBufferSize){
		return (long)(maxBufferSize / INTSIZE) * INTSIZE;
	}
	
	
}
