package aufgabe2.data;
import java.io.IOException;
import java.nio.IntBuffer;

import aufgabe2.data.buffer.*;
import aufgabe2.data.io.Reader;
import aufgabe2.data.jobs.*;
import aufgabe2.interfaces.*;

public class InputBufferImpl implements InputBuffer{

	private final static IntBuffer ZEROBUFFER = IntBuffer.allocate(0);
    private IOScheduler scheduler;
	private Reader reader; //der einzige Leser auf diese Datei
	private IntBuffer currentBuffer = ZEROBUFFER; //Die aktuelle Quelle, aus der gerade die CurrentElemente geholt werden (wechselt, wenn die Quelle zuende gelesen wurde). Die Größe des currentBuffer ist maximal BUFFERSIZE_MERGEREAD 
	private IReaderJob backgroundReader; //Der aktuelle Leseauftrag 
	private long blockSize;
	private long blockPos = -1; //Der Index des aktuellen Elements (vom Blockanfang aus gesehen, nicht im currentBuffer)
	private long fileBytePos = 0; //Der Index in Bytes(!) von der gesammten Datei 
	private int current = 0; //Das aktuelle Element
	private IBufferManager bufferManager;
	private MemPersistence persistenceBuffer;
	private String  bufferKey1, bufferKey2, currentByteBufferKey;
	
	/**
	 * Erzeugt einen neuen InputBuffer, welcher über die gesammte Datei liest und dabei die Blöcke simuliert
	 * @param filePath - Der Pfad der zu lesenden Datei
	 * @param BlockSize - Die Größe der sortierten Blöcke in der Datei
	 * @param scheduler - Die Instanz, die Lese/Schreibjobs im Hintergrund verarbeitet
	 */
	public InputBufferImpl(String filePath, int blockSize, IOScheduler scheduler, IBufferManager bufferManager, String bufferKey1, String bufferKey2){
		this.blockSize = blockSize;
		this.scheduler = scheduler;
		this.bufferKey1 = bufferKey1;
		this.bufferKey2 = bufferKey2;
		this.currentByteBufferKey = bufferKey1;
		this.bufferManager = bufferManager;
		this.persistenceBuffer = bufferManager.getMemPersistence();
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
		
	@Override
	public void moveNext() {
		if (blockPos + 1 < blockSize){ //hat der Block noch ein nächstes Element?
			if (currentBuffer.hasRemaining()){
				current = currentBuffer.get();
				blockPos ++;
			} else { //Buffer komplett ausgelesen --> nächsten, hoffentlich schon fertig geladenen Buffer holen (ansonnsten dauert es etwas)
				
				if(backgroundReader != null) {
					currentBuffer = null; //Speicherplatz freigeben
					currentByteBufferKey = (currentByteBufferKey == bufferKey1 ? bufferKey2 : bufferKey1);
					currentBuffer = backgroundReader.getIntBuffer();//hier wird ggf. gewartet, bis der Job erledigt ist.
					fileBytePos += currentBuffer.limit() * 4;
					backgroundReader = null; //Reader hat seinen Job getan
					if (!reader.isFileFullyReaded() || memPersistenceHasData()){ //kann noch mehr gelesen werden? --> neuen asynchronen Leseauftrag erstellen!
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
	private long count = 0;
	private void pushReaderJob(){
		count ++;
		String fileID = reader.getFilePath();
		String readerBufferKey = (currentByteBufferKey == bufferKey1 ? bufferKey2 : bufferKey1);
		if(memPersistenceHasData()){
			System.out.println(" Lesen aus MemBuffer Job: " + count + " Pos: " + fileBytePos + " File: " + fileID);
			backgroundReader = new MemReader(persistenceBuffer, fileID, fileBytePos, bufferManager, readerBufferKey);
		} else {
			System.out.println(" Lesen aus Datei Job: " + count + " Pos: " + fileBytePos + " File: " + fileID);
			backgroundReader = new ReaderJob(reader, bufferManager.getBBuffer(readerBufferKey));
			scheduler.pushJob((ReaderJob)backgroundReader);
		}
	}
	/**
	 * Gibt zurück, ob die MemPersistence zur aktuellen Position Daten hat.
	 * @return
	 */
	private boolean memPersistenceHasData(){
		return persistenceBuffer.isInPage(reader.getFilePath(), fileBytePos);
	}

}
