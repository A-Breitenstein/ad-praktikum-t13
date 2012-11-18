package aufgabe2.data;

final class Constants {

	/**
	 * Die (maximale Größe) des Speichers in Bytes, die insgesammt vom Programm
	 * verwendet werden darf
	 */
	public static final int BUFFERSIZE_APPLICATION = 3 * 1024 * 1024 * 1024; // = 3 GB
	
	/**
	 * Die (maximale) Größe des Speichers in Bytes, der für einen LeseVorgang pro Datei und Tread
	 * genutzt werden darf
	 */
	public static final int BUFFERSIZE_SORTREAD = BUFFERSIZE_APPLICATION / 2; //Anmerkung: es gibt zwei lesende Dateien und zwei Treads (Rechentread/ IO-Tread)
	/**
	 * Die (maximale) Größe des Speichers in Bytes, der für einen Schreibvorgang pro Datei und
	 * Thread genutzt werden darf
	 */
	public static final int BUFFERSIZE_SORTWRITE = BUFFERSIZE_APPLICATION / 8; //Anmerkung: es gibt zwei schreibende Dateien (auch, wenn abwechselnd in diese geschrieben werden) und zwei Treads (Rechentread/ IO-Tread)

	
	/**
	 * Die (maximale) Größe des Speichers in Bytes, der für einen LeseVorgang pro Datei und Tread
	 * genutzt werden darf
	 */
	public static final int BUFFERSIZE_MERGEREAD = BUFFERSIZE_APPLICATION / 8; //Anmerkung: es gibt zwei lesende Dateien und zwei Treads (Rechentread/ IO-Tread)
	/**
	 * Die (maximale) Größe des Speichers in Bytes, der für einen Schreibvorgang pro Datei und
	 * Thread genutzt werden darf
	 */
	public static final int BUFFERSIZ_MERGEWRITE = BUFFERSIZE_APPLICATION / 8; //Anmerkung: es gibt zwei schreibende Dateien (auch, wenn abwechselnd in diese geschrieben werden) und zwei Treads (Rechentread/ IO-Tread)
	
}
