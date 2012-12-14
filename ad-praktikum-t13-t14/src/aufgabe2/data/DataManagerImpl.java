package aufgabe2.data;

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


/**
 * Created with IntelliJ IDEA.
 * User: abg667
 * Date: 31.10.12
 * Time: 16:41
 */
public class DataManagerImpl implements DataManager {
	
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
	private List<ByteBuffer> readBBufferPool = new ArrayList<ByteBuffer>();//ByteBuffers für die Readers im Merge-Schritt
	private List<ByteBuffer> writeBBufferPool = new ArrayList<ByteBuffer>(); //ByteBuffers für die Writers im Merge-Schritt
	
	//Größen, Zustände
	private final int initBufferSize; //Die (optimale) größe der Blöcke bei der QuickSort-Sortierung in Bytes 
	private int readerBlockSize; //Die aktuelle Größe der beim Mergen zu lesenden Blöcke (writerBlockSize ist doppelt so groß)
	private final long integersToSort; //Die Länge der Datei
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
        System.out.println("   Integers pro Merge-Read: " + valToS(BUFFERSIZE_MERGEREAD / INTSIZE));
        System.out.println("  Integers pro Merge-Write: " + valToS(BUFFERSIZE_MERGEWRITE / INTSIZE));
        System.out.println("       Max Arbeitsspeicher: " + valToS(BUFFERSIZE_APPLICATION / 1024/1024) + "MB");
        System.out.println(" Max Größe eines Readcalls: " + valToS(MAXBYTESPERREADCALL / 1024/1024) + "MB");
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
//        int write_calls = 6;
//        int bytesPerWrite = BUFFERSIZE_SORTARRAY / write_calls;
//        int rest = BUFFERSIZE_SORTARRAY % write_calls;
//
//        for (int i = 1; i < write_calls; i++) {
//            if(buffer.remaining() > bytesPerWrite){
//                buffer.limit(bytesPerWrite*i);
//                activeInitWriter.write(buffer);
//            }
//            else{
//                buffer.limit(bytesPerWrite*(i-1)+buffer.remaining())
//                activeInitWriter.write(buffer);
//            }
//        }
//        if(buffer.hasRemaining()){
//
//        }
//        buffer.clear();
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
	
	
	//----------- Buffer-Pool --------------------------------------------------
	
	
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
