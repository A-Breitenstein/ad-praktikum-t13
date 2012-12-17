package aufgabe2.data;

public final class Constants {
	//Nach jeder Änderung in den Konstanten ist der Test in Tests.testValidBufferConstants aufzurufen!
	
	/**
	 * Die (maximale Größe) des Speichers in Bytes, die insgesammt vom Programm
	 * verwendet werden darf
	 */
	public static final long BUFFERSIZE_APPLICATION = 2048 * 1024 * (long)1024; // Integer.MAX_VALUE// vorderster Wert in MB

	/**
	 * Die Größe eines Integers in Bytes
	 */
	public static final int INTSIZE = 4; // ein Int benötigt 4 Bytes
	
	/**
	 * Die maximale Größe eines Lesevorgangs. Sind mehr Zahlen einzulesenen, so wird mehrmals hintereinander gelesen.
	 */
	public static final int MAXBYTESPERREADCALL = toValidIntSize(256 * 1024 * 1024); //optimal scheint 250-512 MB zu sein
	
	/**
	 * Die (maximale) Größe des Speichers in Bytes, der für das Arbeiten 
	 * auf dem Array genutzt werden darf.
	 */
	public static final int BUFFERSIZE_SORTARRAY = toValidIntSize(BUFFERSIZE_APPLICATION); //Gesamten Arbeitsspeicher verwenden!	
	/**
	 * Die Größe des Speichers in Bytes, der für einen Lese/Schreibvorgang pro Datei und Thead
	 * genutzt werden darf.
	 */
	public static final int BUFFERSIZE_MERGEPAGE = toValidIntSize(Math.min(128 * 1024 * 1024, Math.min(BUFFERSIZE_APPLICATION / 8, MAXBYTESPERREADCALL)));//Anmerkung: es gibt zwei schreibende Dateien (auch, wenn abwechselnd in diese geschrieben werden) und zwei Treads (Rechentread/ IO-Tread)
	/**
	 * Die Größe des Speichers in Bytes, der für das halten verarbeiteteter Daten im Abeitsspeichers (statt Festplatte) verwendet werden darf. 
	 */
	public static final long BUFFERSIZE_MERGEMEMPERSISTENCE = Math.max(0, BUFFERSIZE_APPLICATION - BUFFERSIZE_MERGEPAGE * 8);
	
	/**
	 * Berechnet aus dem gegebenen Maximalen Speicher den maximalen Anteil, in welchen sich
	 * Integer-Zahlen dargestellt werden können, maximal INTEGER.MAXVALUE
	 * @param maxBufferSize
	 * @return int - Postcondition: return % INTSIZE == 0 && returnvalue + INTSIZE > maxBufferSize
	 */
	public static int toValidIntSize(long maxBufferSize){
		return ((int)Math.min(Integer.MAX_VALUE,maxBufferSize) / INTSIZE) * INTSIZE;
	}
	
	
}
