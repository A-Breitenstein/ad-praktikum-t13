package aufgabe2.data;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import aufgabe2.data.io.Reader;
import aufgabe2.data.jobs.IOScheduler;
import aufgabe2.data.jobs.ReaderJob;
import aufgabe2.interfaces.*;

public class InputBufferImpl implements InputBuffer{

	private final static IntBuffer ZEROBUFFER = IntBuffer.allocate(0);
    private IOScheduler scheduler;
	private Reader reader; //der einzige Leser auf diese Datei
	private IntBuffer currentBuffer = ZEROBUFFER; //Die aktuelle Quelle, aus der gerade die CurrentElemente geholt werden (wechselt, wenn die Quelle zuende gelesen wurde). Die Größe des currentBuffer ist maximal BUFFERSIZE_MERGEREAD 
	private ReaderJob backgroundReader; //Der aktuelle Leseauftrag 
	private long blockSize;
	private long blockPos = -1; //Der Index des aktuellen Elements (vom Blockanfang aus gesehen, nicht im currentBuffer)
	private int current = 0; //Das aktuelle Element
	private ByteBuffer currentByteBuffer, buffer1, buffer2;
	
	/**
	 * Erzeugt einen neuen InputBuffer, welcher über die gesammte Datei liest und dabei die Blöcke simuliert
	 * @param filePath - Der Pfad der zu lesenden Datei
	 * @param BlockSize - Die Größe der sortierten Blöcke in der Datei
	 * @param scheduler - Die Instanz, die Lese/Schreibjobs im Hintergrund verarbeitet
	 */
	public InputBufferImpl(String filePath, int blockSize, IOScheduler scheduler, ByteBuffer buffer1, ByteBuffer buffer2){
		this.blockSize = blockSize;
		this.scheduler = scheduler;
		this.buffer1 = buffer1;
		this.buffer2 = buffer2;
		reader = Reader.create(filePath, (int)Constants.BUFFERSIZE_MERGEPAGE);
				
		if (!reader.isFileFullyReaded()){ //Gibt es Elemente in der Datei?
			pushReaderJob(); //Lesejob erzeugen
			moveNext(); //Ergebnis des Lesejobs abrufen und erstes Element lesen
		} else {
			blockPos = blockSize;//Bewirkt, dass hasCurrent nun False zurückliefert
		}
			
	}
	
	/**
	 * Schließt den Buffer und leert den Speicher
	 * @throws IOException 
	 */
	void close() throws IOException{
		currentBuffer = null;
		if(backgroundReader != null) {	//Gibt es noch einen ReaderJob, den sonnst keiner mehr abholen würde?		
			backgroundReader.getIntBuffer(); 
			backgroundReader = null;
		}
		reader.close( );
		buffer1 = null;
		buffer2 = null;
	}
	
	/**
	 * Beginnt mit der Simulation des nächsten Blocks. Es wird kein Fehler zurückgegeben, wenn
	 * es keinen Block mehr zu simulieren gibt.
	 */
	void simulateNextBlock(){
		blockPos = -1;
		moveNext();//Erstes Element des neuen Blocks einlesen
	}
	
	@Override
	public int getCurrent() {
		return current;
	}

	@Override
	public boolean hasCurrent() {
		return blockPos < blockSize;
	}
long readCount = 0;
	@Override
	public void moveNext() {
		if (blockPos + 1 < blockSize){ //hat der Block noch ein nächstes Element?
			if (currentBuffer.hasRemaining()){
				current = currentBuffer.get();
				readCount ++;
				blockPos ++;
			} else { //Buffer komplett ausgelesen --> nächsten, hoffentlich schon fertig geladenen Buffer holen (ansonnsten dauert es etwas)
				
				if(backgroundReader != null) {
					currentBuffer = null; //Speicherplatz freigeben
					currentByteBuffer = (currentByteBuffer == buffer1 ? buffer2 : buffer1);//Nur als markierung
					currentBuffer = backgroundReader.getIntBuffer();//hier wird ggf. gewartet, bis der Job erledigt ist.
					backgroundReader = null; //Reader hat seinen Job getan
					if (!reader.isFileFullyReaded()){ //kann noch mehr gelesen werden? --> neuen asynchronen Leseauftrag erstellen!
						pushReaderJob(); 
					}
					moveNext(); //currentBuffer wurde erneuert, daher current neu einlsenen
				} else { //Dann ist die Datei entgültig zuende
					currentBuffer = ZEROBUFFER;
					blockPos = blockSize;//Bewirkt, dass hasCurrent nun False zurückliefert
				}
			}
		} else {
			blockPos = blockSize;//Bewirkt, dass hasCurrent nun False zurückliefert (CurrentBuffer aber nicht zurücksetzen, da dort noch Elemente für den nächsten Block stehen könnten)
		}
			
	}
	
	private void pushReaderJob(){
		backgroundReader = new ReaderJob(reader, (currentByteBuffer == buffer1 ? buffer2 : buffer1));
		scheduler.pushJob(backgroundReader);
	}

}
