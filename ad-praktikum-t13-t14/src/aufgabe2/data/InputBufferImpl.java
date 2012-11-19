package aufgabe2.data;
import java.io.IOException;
import java.nio.IntBuffer;

import aufgabe2.interfaces.*;

public class InputBufferImpl implements InputBuffer{

	private final static IntBuffer ZEROBUFFER = IntBuffer.allocate(0);
    private IOScheduler scheduler;
	private Reader reader; //der einzige Leser auf diese Datei
	private IntBuffer currentBuffer = ZEROBUFFER; //Die aktuelle Quelle, aus der gerade die CurrentElemente geholt werden (wechselt, wenn die Quelle zuende gelesen wurde). Die Größe des currentBuffer ist maximal BUFFERSIZE_MERGEREAD 
	private ReaderJob backgroundReader; //Der aktuelle Leseauftrag 
	private int blockSize;
	private int blockPos = -1; //Der Index des aktuellen Elements (vom Blockanfang aus gesehen, nicht im currentBuffer)
	private int current = 0; //Das aktuelle Element
	
	
	/**
	 * Erzeugt einen neuen InputBuffer, welcher über die gesammte Datei liest und dabei die Blöcke simuliert
	 * @param filePath - Der Pfad der zu lesenden Datei
	 * @param BlockSize - Die Größe der sortierten Blöcke in der Datei
	 * @param scheduler - Die Instanz, die Lese/Schreibjobs im Hintergrund verarbeitet
	 */
	public InputBufferImpl(String filePath, int blockSize, IOScheduler scheduler){
		this.blockSize = blockSize;
		this.scheduler = scheduler;
		reader = Reader.create(filePath, filePath);
		reader.setInegerCountPerRead((int)(Constants.BUFFERSIZE_MERGEREAD / Constants.INTSIZE)); //Pro Integer werden 4 Bytes benötigt 
		
		if (reader.hasNextIntArrray()){ //Gibt es Elemente in der Datei?
			pushReaderJob(); //Lesejob erzeugen
			moveNext(); //Ergebnis des Lesejobs abrufen und erstes Element lesen
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
					currentBuffer = backgroundReader.getIntBuffer(); //hier wird ggf. gewartet, bis der Job erledigt ist.
					backgroundReader = null; //Reader hat seinen Job getan
					if (reader.hasNextIntArrray()){ //kann noch mehr gelesen werden? --> neuen asynchronen Leseauftrag erstellen!
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
		backgroundReader = new ReaderJob(reader);
		scheduler.pushJob(backgroundReader);
	}

}
