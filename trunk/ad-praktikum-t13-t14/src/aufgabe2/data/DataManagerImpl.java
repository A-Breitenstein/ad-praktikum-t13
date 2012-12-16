package aufgabe2.data;

import aufgabe2.data.buffer.IBufferManager;
import aufgabe2.data.buffer.MemPersistence;
import aufgabe2.data.io.Reader;
import aufgabe2.data.io.Writer;
import aufgabe2.data.jobs.IOScheduler;
import aufgabe2.interfaces.DataManager;
import aufgabe2.interfaces.InputBuffer;
import aufgabe2.interfaces.OutputBuffer;
import static aufgabe2.data.Constants.*;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry.Entry;


/**
 * Created with IntelliJ IDEA.
 * User: abg667
 * Date: 31.10.12
 * Time: 16:41
 */
public class DataManagerImpl implements DataManager, IBufferManager {
	
	//SourceDateien
	private String sourceFilePath, file1Path, file2Path, file3Path, file4Path;
	
	//Readers, Writers
	private Reader initReader;
	private Writer initWriter1, initWriter2, activeInitWriter;
	private InputBufferImpl mergeInput1, mergeInput2;
	private OutputBufferImpl mergeOutput1, mergeOutput2, activeMergeOutput;
	private IOScheduler scheduler = new IOScheduler();
	
	//ByteBuffer-Pools
	private final ByteBuffer ZEROBYTEBUFFER = ByteBuffer.allocateDirect(0);
	private ByteBuffer sortBBuffer;
	private Hashtable<String, ByteBuffer> mergeBBufferPool = new Hashtable<String, ByteBuffer>();//ByteBuffers für die Readers/Writers im Merge-Schritt
	private MemPersistence memPersistence = MemPersistence.ZEROMEMPERSISTENCE;
	
	//Größen, Zustände
	private final int initBufferSize; //Die (optimale) größe der Blöcke bei der QuickSort-Sortierung in Bytes 
	private int readerBlockSize; //Die aktuelle Größe der beim Mergen zu lesenden Blöcke (writerBlockSize ist doppelt so groß)
	public final long integersToSort; //Die Länge der Datei
	private long storedIntegers; //Die bereits gespeicherten Integers innerhalb eines MergeRuns
	private switchStates switchState = switchStates.undef; 
	private modie modus = modie.QuickSort;
	
	//Zeitmessung
	private long startTimestamp;
	private long lastMessageTimestamp;
	
	/**
	 * Konstruktor
	 * @param sourceFilePath
	 */
	public DataManagerImpl(String sourceFilePath) {
		//SourceFiles
		this.sourceFilePath = sourceFilePath;
		file1Path = sourceFilePath+"1";
		file2Path = sourceFilePath+"2";
		file3Path = sourceFilePath+"3";
		file4Path = sourceFilePath+"4";
		
		//Readers, Writers
		initBufferSize = calculateOptimalInitReadBuffer(new File(sourceFilePath).length());
		initReader = Reader.create(sourceFilePath, initBufferSize);
		initWriter1 = Writer.create(file1Path);
        initWriter2 = Writer.create(file2Path);
        activeInitWriter = initWriter1;
               
        readerBlockSize = initBufferSize / INTSIZE;
        integersToSort = initReader.getFileChanSize() / INTSIZE;
        sortBBuffer = ByteBuffer.allocateDirect(initBufferSize);
        scheduler.start();
        
        //Ausgabe
        System.out.println("                        PC: " + getPcName());
        System.out.println("    Beginne Sortierung von: " + valToS(integersToSort) + " Integers (" + Math.round((integersToSort * INTSIZE)/1024/1024) + "MB)"  );
        System.out.println("Initial Integers pro Block: " + valToS(readerBlockSize));
        System.out.println("   Integers pro Merge-Page: " + valToS(BUFFERSIZE_MERGEPAGE / INTSIZE));
        System.out.println("       Max Arbeitsspeicher: " + valToS(BUFFERSIZE_APPLICATION / 1024/1024) + "MB");
        System.out.println(" Max Größe eines Readcalls: " + valToS(MAXBYTESPERREADCALL / 1024/1024) + "MB");
        System.out.println("           Merge PageGröße: " + valToS(BUFFERSIZE_MERGEPAGE / 1024/1024) + "MB");
        System.out.println("Größe Merge-MemPersistence: " + valToS(BUFFERSIZE_MERGEMEMPERSISTENCE / 1024/1024) + "MB (" + (int)Math.floor(BUFFERSIZE_MERGEMEMPERSISTENCE / BUFFERSIZE_MERGEPAGE) + " Pages)");
        System.out.println();
        
        //Zeitmessung
        startTimestamp = System.currentTimeMillis();
        lastMessageTimestamp = System.currentTimeMillis();
    }
	
	/**
	 * Berechnet die optimale Initiallänge der Arrays, ohne einen zusätzlichen Merge-Run machen zu müssen.
	 * @return
	 */
	private int calculateOptimalInitReadBuffer(long fileSize){
		long optimalInitBuffer = fileSize;
		while(optimalInitBuffer > BUFFERSIZE_SORTARRAY){
			optimalInitBuffer = optimalInitBuffer / 2 + 4; //+4 wegen möglichen Rundungsfehlern (und 4 Bits für Integers...)
		}
		return toValidIntSize(optimalInitBuffer);
	}
	
	private String getPcName() {
		try {
			return java.net.InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "Unknown";
		}
	}
	
	
	//---------------Interface-Methoden --------------------------------------
	
	
	@Override
	public ByteBuffer readBlock() {
        if(!initReader.isFileFullyReaded()){
        	 try {
				initReader.readToByteBuffer(sortBBuffer);
				return sortBBuffer;
			} catch (IOException e) {
				System.err.println("Fehler beim Lesen aus der Initialdatei");
				return ZEROBYTEBUFFER;
			}
        } else {
            return ZEROBYTEBUFFER;
        }
	}
	@Override
	public void writeBlock(ByteBuffer buffer) {
		activeInitWriter.writeByteBufferToFile(buffer);
		activeInitWriter = (activeInitWriter == initWriter1 ? initWriter2 : initWriter1); //Runs abwechselnd schreiben
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
		closeBuffers();
		closeBBufferPool();
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
	
	
	//----------- Buffer-Pool --------------------------------------------------
	
	
	private void initBBufferPool(){
		for (int i = 1; i<=2; i++){
			mergeBBufferPool.put("r1" + i, ByteBuffer.allocateDirect((int)BUFFERSIZE_MERGEPAGE));
			mergeBBufferPool.put("r2" + i, ByteBuffer.allocateDirect((int)BUFFERSIZE_MERGEPAGE));
		}
		for (int i = 1; i<=2; i++){
			mergeBBufferPool.put("w1" + i, ByteBuffer.allocateDirect((int)BUFFERSIZE_MERGEPAGE));
			mergeBBufferPool.put("w2" + i, ByteBuffer.allocateDirect((int)BUFFERSIZE_MERGEPAGE));
		}
		memPersistence = new MemPersistence(BUFFERSIZE_MERGEMEMPERSISTENCE);
	}
	private void closeBBufferPool(){
		mergeBBufferPool.clear();
		memPersistence.ReleasePersistence();
	}
		
	/**
	 * Gibt den Buffer mit dem angegebenen BufferKey zurück
	 * @param bufferKey
	 * @return
	 */
	@Override
	public ByteBuffer getBBuffer(String bufferKey){
		return mergeBBufferPool.get(bufferKey);
	}
	/**
	 * Tauscht einen ByteBuffer gegen einen anderen, gleichwertigen aus
	 * @param bufferKey
	 * @return
	 */
	@Override
	public ByteBuffer exchangeBBuffer(String bufferKey, ByteBuffer newBuffer){
		ByteBuffer b = getBBuffer(bufferKey);
		mergeBBufferPool.put(bufferKey, newBuffer);
		System.out.println("Exchange Buffer " + bufferKey + " von " + System.identityHashCode(b) + " zu " + System.identityHashCode(newBuffer));
		return b;
	}
	@Override
	public MemPersistence getMemPersistence() {
		return memPersistence;
	}
	
	//----------SwitchChannels, Close IO Channels--------------------------------
	
	
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
		boolean lastRun = readerBlockSize *2 > integersToSort;
		if (switchState == switchStates.read1_2_write3_4){
			mergeInput1 = new InputBufferImpl(file1Path, readerBlockSize, scheduler, this, "r11", "r12");
			mergeInput2 = new InputBufferImpl(file2Path, readerBlockSize, scheduler, this, "r21", "r22");
			mergeOutput1 = new OutputBufferImpl(file3Path, this, scheduler, "w11", "w12", lastRun);
			mergeOutput2 = new OutputBufferImpl(file4Path, this, scheduler, "w21", "w22", lastRun);
		} else {
			mergeInput1 = new InputBufferImpl(file3Path, readerBlockSize, scheduler, this, "r11", "r12");
			mergeInput2 = new InputBufferImpl(file4Path, readerBlockSize, scheduler, this, "r21", "r22");
			mergeOutput1 = new OutputBufferImpl(file1Path, this, scheduler, "w11", "w12", lastRun);
			mergeOutput2 = new OutputBufferImpl(file2Path, this, scheduler, "w21", "w22", lastRun);
		}
		storedIntegers = 0;
		activeMergeOutput = mergeOutput1;
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
	
	
	//---------- Ausgabe auf Konsole ----------------
	
    private void printMessage(String message){
    	DateFormat df = new SimpleDateFormat("mm:ss");
    	System.out.println(df.format((System.currentTimeMillis() - startTimestamp)) + " - Diff " + Math.round((System.currentTimeMillis() - lastMessageTimestamp) / 100.0) / 10.0 + "s: " + message);
    	lastMessageTimestamp = System.currentTimeMillis();
    }
    /**
     * Formatiert eine Zahl mit Tausender-Trennzeichen
     * @param val
     * @return
     */
	private String valToS(long val){
		return NumberFormat.getInstance().format(val);
	}


	//------------ Eumns ----------------------------

	
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
