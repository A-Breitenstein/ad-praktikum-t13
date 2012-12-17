package aufgabe2.data.jobs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.Semaphore;

import aufgabe2.data.io.Reader;

/**
 * Ein Job zum Einlesen aus einem Stream. Das Ergebnis ist ein IntBuffer.
 * @author Markus Bruhn
 *
 */
public class ReaderJob implements IOJob, IReaderJob{

	private Reader reader;
	private IntBuffer result;
	private Semaphore memoryLock;
	private Semaphore jobFinished = new Semaphore(0);
	private ByteBuffer buffer;
	
	/**
	 * Konstruktor
	 * @param reader - die Quelle, von welcher getIntBuffer() aufgerufen werden soll
	 * @param buffer - der Buffer, in welches das Ergebnis geschrieben werden soll
	 */
	public ReaderJob(Reader reader, ByteBuffer buffer){
		this.reader = reader;
		this.buffer = buffer;
		buffer.clear();
	}
	
	/**
	 * Gibt das Ergebnis des Lesevorgangs zurück. Sollte der Lesevorgang noch nicht
	 * vollständig ausgeführt worden sein, wird so lange gewartet, bis dieser abgeschlossen wurde.
	 * Nachdem der IntBuffer zurückgegeben wurde, ist dieser kein zweites Mal abrufbar.
	 * @return
	 */
	@Override
	public IntBuffer getIntBuffer(){
		
		try {
			jobFinished.acquire();//Warten, bis der Job erledigt ist
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		} 
		IntBuffer returnValue = result;
		result = null; //Speicher freigeben (innerhalb dieser Klasse)
		memoryLock.release(); //Signalisieren, dass der Speicher anderseitig verwendet werden darf
		return returnValue;
	}
	
	@Override
	public String getWorkingFile() {
		return reader.getFilePath();
	}

	@Override
	public void setMemoryLock(Semaphore lock) {
		this.memoryLock = lock;
		
	}

	@Override
	public boolean prepareRun() {
		return memoryLock.tryAcquire(); //Speicher im Abeitsspeicher anfordern
	}

	@Override
	public void run() {
		 try {
			 reader.readToByteBuffer(buffer);//das ist alles...
		} catch (IOException e) {
			System.err.println("Fehler beim Lesen aus " + getWorkingFile());
			buffer.clear();
		} 
		 result = buffer.asIntBuffer();
		 jobFinished.release();
	}

}
