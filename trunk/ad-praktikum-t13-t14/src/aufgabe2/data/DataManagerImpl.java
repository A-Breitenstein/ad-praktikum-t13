package aufgabe2.data;

import aufgabe2.interfaces.DataManager;
import aufgabe2.interfaces.InputBuffer;
import aufgabe2.interfaces.OutputBuffer;
import static aufgabe2.data.Constants.*;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: abg667
 * Date: 31.10.12
 * Time: 16:41
 */
public class DataManagerImpl implements DataManager {
	
	private String sourceFilePath, file1Path, file2Path, file3Path, file4Path;
	private Reader initReader;
	private Writer initWriter1, initWriter2, activeInitWriter;
	private InputBufferImpl mergeInput1, mergeInput2;
	private OutputBufferImpl mergeOutput1, mergeOutput2, activeMergeOutput;
	private final ByteBuffer ZEROBYTEBUFFER = ByteBuffer.allocateDirect(0);
	private int readerBlockSize; //Die aktuelle Größe der beim Mergen zu lesenden Blöcke (writerBlockSize ist doppelt so groß)
	private IOScheduler scheduler = new IOScheduler();
	
	private switchStates switchState = switchStates.undef; 
	private modie modus = modie.QuickSort;
	private long integersToSort;
	private long storedIntegers;
	//Für Buffer-Pool
	List<ByteBuffer> readBBufferPool = new ArrayList<ByteBuffer>();//ByteBuffers für die Readers im Merge-Schritt
	List<ByteBuffer> writeBBufferPool = new ArrayList<ByteBuffer>(); //ByteBuffers für die Writers im Merge-Schritt
	ByteBuffer sortBBuffer;
	
	public DataManagerImpl(String sourceFilePath) {
		this.sourceFilePath = sourceFilePath;
		file1Path = sourceFilePath+"1";
		file2Path = sourceFilePath+"2";
		file3Path = sourceFilePath+"3";
		file4Path = sourceFilePath+"4";
		
		initReader = Reader.create(sourceFilePath, (int)BUFFERSIZE_SORTARRAY);
		
		initWriter1 = Writer.create(file1Path);
        initWriter2 = Writer.create(file2Path);
        activeInitWriter = initWriter1;
        readerBlockSize = INITBLOCKINTEGERS;
        integersToSort = initReader.getFileChanSize() / INTSIZE;
        sortBBuffer = ByteBuffer.allocateDirect( (int)BUFFERSIZE_SORTARRAY);
        scheduler.start();
        
        System.out.println("    Beginne Sortierung von: " + valToS(integersToSort) + " Integers");
        System.out.println("Initial Integers pro Block: " + valToS(INITBLOCKINTEGERS));
        System.out.println("   Integers pro Merge-Read: " + valToS(BUFFERSIZE_MERGEREAD / INTSIZE));
        System.out.println("  Integers pro Merge-Write: " + valToS(BUFFERSIZE_MERGEWRITE / INTSIZE));
        System.out.println("       Max Arbeitsspeicher: " + valToS(BUFFERSIZE_APPLICATION / 1024/1024) + "MB");
        System.out.println();
        
        startTimestamp = System.currentTimeMillis();
        lastMessageTimestamp = System.currentTimeMillis();
    }
	
	@Override
	public ByteBuffer readBlock() {
		if(initReader.hasNextIntArrray()){		
            initReader.getIntBuffer(sortBBuffer);
            return sortBBuffer;
        } else {
            return ZEROBYTEBUFFER;
        }
	}
		
	@Override
	public void writeBlock(ByteBuffer buffer) {
		activeInitWriter.writeByteBufferToFile(buffer);
		activeInitWriter = (activeInitWriter == initWriter1 ? initWriter2 : initWriter1); //Runs abwechselnd schreiben		
	}

	
	private void closeInitIO(){
		if(initReader != null){//Wurde der Kram bereits geschlossen (und freigegeben)?
			try {
				initReader.close();
			} catch (IOException e) {
				System.err.println("InitReader konnte nicht geschlossen werden");
			}
			initWriter1.close();
			initWriter2.close();
			initReader = null;
			initWriter1 = null;
			initWriter2 = null;
			sortBBuffer = null;
			System.gc();
		}
	}


	/**
	 * Muss von OutputBuffer aufgerufen werden, wenn dort das Ende des Blocks signalisiert wurde
	 */
	void finishBlock(){		
		storedIntegers += readerBlockSize * 2; //wenn ein Rest gespeichert wurde, ist der tatsächliche Wert natürlich etwas kleiner, aber es wird so oder So ein Switch gemacht.
		if (storedIntegers >= integersToSort) {
			if ( readerBlockSize * 2 < integersToSort){ //wenn false, dann terminiere
				switchChannels();
			}
		} else {
			mergeInput1.simulateNextBlock();
			mergeInput2.simulateNextBlock();
			activeMergeOutput = (activeMergeOutput == mergeOutput1 ? mergeOutput2 : mergeOutput1); //Den nächsten Run auf die andere der beiden Dateien schreiben
		}	
	}
	
	private void switchChannels(){
		if(modus == modie.QuickSort){
			closeInitIO();
			modus = modie.Merge;
			initBBufferPool();
			printMessage("QuickSort abgschlossen. Beginne ersten Merge-Schrit mit Runlänge = " + valToS(readerBlockSize));
		} else {
			closeBuffers();
			readerBlockSize *= 2; //BlockSize verdoppeln
			printMessage("Beginne Merge-Schritt mit Runlänge = " + valToS(readerBlockSize));
		}
		switchState = (switchState == switchStates.read1_2_write3_4 ? switchStates.read3_4_write1_2 : switchStates.read1_2_write3_4);
		
		if (switchState == switchStates.read1_2_write3_4){
			mergeInput1 = new InputBufferImpl(file1Path, readerBlockSize, scheduler, readBBufferPool.get(0), readBBufferPool.get(1));
			mergeInput2 = new InputBufferImpl(file2Path, readerBlockSize, scheduler, readBBufferPool.get(2), readBBufferPool.get(3));
			mergeOutput1 = new OutputBufferImpl(file3Path, this, scheduler, writeBBufferPool.get(0), writeBBufferPool.get(1));
			mergeOutput2 = new OutputBufferImpl(file4Path, this, scheduler, writeBBufferPool.get(2), writeBBufferPool.get(3));
		} else {
			mergeInput1 = new InputBufferImpl(file3Path, readerBlockSize, scheduler, readBBufferPool.get(0), readBBufferPool.get(1));
			mergeInput2 = new InputBufferImpl(file4Path, readerBlockSize, scheduler, readBBufferPool.get(2), readBBufferPool.get(3));
			mergeOutput1 = new OutputBufferImpl(file1Path, this, scheduler, writeBBufferPool.get(0), writeBBufferPool.get(1));
			mergeOutput2 = new OutputBufferImpl(file2Path, this, scheduler, writeBBufferPool.get(2), writeBBufferPool.get(3));
		}
		storedIntegers = 0;
		activeMergeOutput = mergeOutput1;
	}
	private void closeBuffers(){
		try {
			mergeInput1.close();
			mergeInput2.close();
			mergeOutput1.close();
			mergeOutput2.close();
		} catch (IOException e) {
			System.err.println("Fehler beim Schließen der Dateien. Sortiervorgang wird abgebrochen.");
			System.exit(0);
		}
	}
	
	@Override
	public InputBuffer readLeftChannel() {
		if (modus == modie.QuickSort)
			switchChannels();
		return mergeInput1;
	}

	@Override
	public InputBuffer readRightChannel() {
		if (modus == modie.QuickSort)
			switchChannels();
		return mergeInput2;
	}
	@Override
	public OutputBuffer createOuputBuffer() {
		if (modus == modie.QuickSort)
			switchChannels();
		return activeMergeOutput;
	}

	@Override
	public String completeSort() {
		closeBBufferPool();
		closeBuffers();
		scheduler.interrupt();
		try {
			scheduler.join();
		} catch (InterruptedException e) {}
		File endFile = new File(activeMergeOutput.getFilePath());
        File resultFile =  new File((endFile.getParent() == null ? "EnddateiSorted" : Paths.get(sourceFilePath).getParent().resolve("EnddateiSorted").toString()));
 
        if(!endFile.renameTo(resultFile)){
        	System.err.println("Die Ausgabedatei (" + endFile.getAbsolutePath() +") konnte nicht umbenannt werden.");
        	resultFile = endFile;
        } else {
        	deleteIfExits(file1Path);
        	deleteIfExits(file2Path);
        	deleteIfExits(file3Path);
        	deleteIfExits(file4Path);
        }
        printMessage("Sortieren abgeschlossen! Die Ausgabedatei enthält " + valToS(resultFile.length() / INTSIZE) + " Integers.");
        
        return resultFile.getAbsolutePath();
	}
	
	
	/**
	 * Löscht eine Datei, wenn diese existiert.
	 * @param stringPath
	 */
	private static void deleteIfExits(String stringPath){
		 Path path = Paths.get(stringPath);

         if(Files.exists(path)){
			try {
				Files.delete(path);
			} catch (IOException e) {
				System.out.println("Datei " + stringPath + " konnte nicht gelöscht werden.");
			}
         }
	}
	
	private String valToS(long val){
		return NumberFormat.getInstance().format(val);
	}
	private long startTimestamp;
	private long lastMessageTimestamp;
    private void printMessage(String message){
    	DateFormat df = new SimpleDateFormat("mm:ss");
    	System.out.println(df.format((System.currentTimeMillis() - startTimestamp)) + " - Diff " + Math.round((System.currentTimeMillis() - lastMessageTimestamp) / 100.0) / 10.0 + "s: " + message);
    	lastMessageTimestamp = System.currentTimeMillis();
    }
	
	private void initBBufferPool(){
		for (int i = 0; i<4; i++){
			readBBufferPool.add(ByteBuffer.allocateDirect((int)BUFFERSIZE_MERGEREAD));
		}
		for (int i = 0; i<4; i++){
			writeBBufferPool.add(ByteBuffer.allocateDirect((int)BUFFERSIZE_MERGEWRITE));
		}
	}
	private void closeBBufferPool(){
		readBBufferPool.clear();
		writeBBufferPool.clear();
	}
	
	enum modie{
		QuickSort,
		Merge;
	}
	enum switchStates{
		undef,
		read1_2_write3_4,
		read3_4_write1_2;
	}

}
