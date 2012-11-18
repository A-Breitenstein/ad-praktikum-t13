package aufgabe2.data;

import java.nio.IntBuffer;
import java.util.concurrent.locks.ReentrantLock;
import sun.awt.Mutex;

/**
 * Ein Job zum Einlesen aus einem Stream. Das Ergebnis ist ein IntBuffer.
 * @author Markus Bruhn
 *
 */
public class ReaderJob implements IOJob{

	private Reader reader;
	private IntBuffer result;
	private ReentrantLock memoryLock;
	private Mutex jobFinished;
	
	/**
	 * Konstruktor
	 * @param reader - die Quelle, von welcher getIntBuffer() aufgerufen werden soll
	 */
	public ReaderJob(Reader reader){
		jobFinished = new Mutex();
		jobFinished.lock();//Sperren, bis Run beendet ist
		this.reader = reader;
	}
	
	/**
	 * Gibt das Ergebnis des Lesevorgangs zurück. Sollte der Lesevorgang noch nicht
	 * vollständig ausgeführt worden sein, wird so lange gewartet, bis dieser abgeschlossen wurde.
	 * Nachdem der IntBuffer zurückgegeben wurde, ist dieser kein zweites Mal abrufbar.
	 * @return
	 */
	public IntBuffer getIntBuffer(){
		jobFinished.lock(); //Warten, bis der Job erledigt ist
		IntBuffer returnValue = result;
		result = null; //Speicher freigeben (innerhalb dieser Klasse)
		memoryLock.unlock(); //Signalisieren, dass der Speicher anderseitig verwendet werden darf
		return returnValue;
	}
	
	@Override
	public String getWorkingFile() {
		return reader.getFileName();
	}

	@Override
	public void setMemoryLock(ReentrantLock lock) {
		this.memoryLock = lock;
		
	}

	@Override
	public boolean prepareRun() {
		return memoryLock.tryLock(); //Speicher im Abeitsspeicher anfordern
	}

	@Override
	public void runJob() {
		result = reader.getIntBuffer(); //das ist alles...
	}

}
