package aufgabe2.data.jobs;

import java.nio.IntBuffer;

public interface IReaderJob {

	/**
	 * Gibt das Ergebnis des Lesevorgangs zurück. Sollte der Lesevorgang noch nicht
	 * vollständig ausgeführt worden sein, wird so lange gewartet, bis dieser abgeschlossen wurde.
	 * Nachdem der IntBuffer zurückgegeben wurde, ist dieser kein zweites Mal abrufbar.
	 * @return
	 */
	public IntBuffer getIntBuffer();
		
}
