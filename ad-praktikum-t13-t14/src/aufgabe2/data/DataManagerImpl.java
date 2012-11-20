package aufgabe2.data;

import aufgabe2.interfaces.DataManager;
import aufgabe2.interfaces.DataWrapper;
import aufgabe2.interfaces.InputBuffer;
import aufgabe2.interfaces.OutputBuffer;
import static aufgabe2.data.Constants.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;

/**
 * Created with IntelliJ IDEA.
 * User: abg667
 * Date: 31.10.12
 * Time: 16:41
 */
public class DataManagerImpl implements DataManager {
	
	private String sourceFilePath, file1Path, file2Path, file3Path, file4Path;
	private FolgenReader initReader;
	private FolgenWriter initWriter1, initWriter2, activeInitWriter;
	private InputBufferImpl mergeInput1, mergeInput2;
	private OutputBufferImpl mergeOutput1, mergeOutput2, activeMergeOutput;
	private final DataWrapper ZERODATAWRAPPER = DataWrapperImpl.create(new int[0], 0, true);
	private int readerBlockSize; //Die aktuelle Größe der beim Mergen zu lesenden Blöcke (writerBlockSize ist doppelt so groß)
	private IOScheduler scheduler = new IOScheduler();
	private switchStates switchState = switchStates.undef; 
	private modie modus = modie.QuickSort;
	private long integersToSort;
	private long storedIntegers;
	
	
	public DataManagerImpl(String sourceFilePath) {
		this.sourceFilePath = sourceFilePath;
		file1Path = sourceFilePath+"1";
		file2Path = sourceFilePath+"2";
		file3Path = sourceFilePath+"3";
		file4Path = sourceFilePath+"4";
		
		initReader = FolgenReader.create("InitialReader", sourceFilePath, INITBLOCKINTEGERS);
		initWriter1 = FolgenWriter.create(file1Path, (int)BUFFERSIZE_SORTWRITE);
        initWriter2 = FolgenWriter.create(file2Path, (int)BUFFERSIZE_SORTWRITE);
        activeInitWriter = initWriter1;
        readerBlockSize = INITBLOCKINTEGERS;
        integersToSort = initReader.getFileSize() / INTSIZE;
        
        System.out.println("    Beginne Sortierung von: " + valToS(integersToSort) + " Integers");
        System.out.println("Initial Integers pro Block: " + valToS(INITBLOCKINTEGERS));
        System.out.println("       Max Arbeitsspeicher: " + valToS(BUFFERSIZE_APPLICATION / 1024/1024) + "MB");
    }
	
	@Override
	public DataWrapper readBlock() {
		if(initReader.hasNextFolge()){
            return initReader.getFolge();
        } else {
            return ZERODATAWRAPPER;
        }
	}

	@Override
	public void writeBlock(DataWrapper dataWrapper) {
		activeInitWriter.writeFolge(dataWrapper);
		activeInitWriter = (activeInitWriter == initWriter1 ? initWriter2 : initWriter1); //Runs abwechselnd schreiben		
	}

	private void closeInitIO(){
		if(initReader != null){//Wurde der Kram bereits geschlossen (und freigegeben)?
			initReader.close();
			initWriter1.close();
			initWriter2.close();
			initReader = null;
			initWriter1 = null;
			initWriter2 = null;
		}
	}


	/**
	 * Muss von OutputBuffer aufgerufen werden, wenn dort das Ende des Blocks signalisiert wurde
	 */
	void finishBlock(){		
		storedIntegers += readerBlockSize * 2; //wenn ein Rest gespeichert wurde, ist der tatsächliche Wert natürlich etwas kleiner, aber es wird so oder So ein Switch gemacht.
		if (storedIntegers >= integersToSort) {
			if ( readerBlockSize * 2 < integersToSort){ //kann hier terminiert werden?
				switchChannels();
			} else {
				//Else-Part TEMPORÄR!
				//closeBuffers();
				//System.out.println("Test-Sortierung abgeschlossen.");
				//System.out.println("Virtuell geschriebene Elemente: " + mergeOutput1.diagnosticWriteCount + ", " + mergeOutput2.diagnosticWriteCount);
				//System.exit(0);
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
			System.out.println("QuickSort abgschlossen. Beginne ersten Merge-Schrit mit Runlänge = " + readerBlockSize);
		} else {
			closeBuffers();
			readerBlockSize *= 2; //BlockSize verdoppeln
			System.out.println("Beginne Merge-Schrit mit Runlänge = " + readerBlockSize);
		}
		switchState = (switchState == switchStates.read1_2_write3_4 ? switchStates.read3_4_write1_2 : switchStates.read1_2_write3_4);
		
		if (switchState == switchStates.read1_2_write3_4){
			mergeInput1 = new InputBufferImpl(file1Path, readerBlockSize, scheduler);
			mergeInput2 = new InputBufferImpl(file2Path, readerBlockSize, scheduler);
			mergeOutput1 = new OutputBufferImpl(file3Path, this, scheduler);
			mergeOutput2 = new OutputBufferImpl(file4Path, this, scheduler);
		} else {
			mergeInput1 = new InputBufferImpl(file3Path, readerBlockSize, scheduler);
			mergeInput2 = new InputBufferImpl(file4Path, readerBlockSize, scheduler);
			mergeOutput1 = new OutputBufferImpl(file1Path, this, scheduler);
			mergeOutput2 = new OutputBufferImpl(file2Path, this, scheduler);
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
		closeBuffers();
		
		File endFile = new File(activeMergeOutput.getFilePath());
        File resultFile = new File(Paths.get(sourceFilePath).getParent().resolve("EnddateiSorted").toString());

        if(!endFile.renameTo(resultFile)){
        	System.err.println("Die Ausgabedatei konnte nicht umbenannt werden.");
        	resultFile = endFile;
        } else {
        	/*
        	deleteIfExits(file1Path);
        	deleteIfExits(file2Path);
        	deleteIfExits(file3Path);
        	deleteIfExits(file4Path);*/
        }
        return resultFile.getAbsolutePath();
	}
	
	
	/**
	 * Löscht eine Datei, wenn diese existiert.
	 * @param stringPath
	 */
	private void deleteIfExits(String stringPath){
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
    
	enum modie{
		QuickSort,
		Merge;
	}
	enum switchStates{
		undef,
		read1_2_write3_4,
		read3_4_write1_2;
	}
	
	/*
    private static int FolgenReaderInitValue = 500;
    private long FolgenReaderValue = FolgenReaderInitValue;

    private long initFileFolgenLength;

    //Linkes File, rechtes File

    private String datei1 = "file1", datei2 = "file2", datei3 = "file3", datei4 = "file4";
    String[] dateiNamen;
    FolgenWriter folgenWriter1;
    FolgenWriter folgenWriter2;
    FolgenReader folgenReader1;
    FolgenReader folgenReader2;

    boolean writeSwitch = true, bigSwitch = true;
    boolean finish = false;
    //SourceFile
    //private final String sourceFilePath = "zahlenfolge";
    FolgenReader initialReader;
    long sourceFileSize = 0;
    // ca 1.416.872 KB RAM  ( 357923000*4)
    public static void setFolgenReaderInitValue(int count){
         FolgenReaderInitValue = count;
    }
    public DataManagerImpl(String sourceFilePath) {
        datei1 = sourceFilePath+"1";
        datei2 = sourceFilePath+"2";
        datei3 = sourceFilePath+"3";
        datei4 = sourceFilePath+"4";

        dateiNamen = new String[]{datei1, datei2, datei3, datei4};

        folgenWriter1 = FolgenWriter.create(datei3);
        folgenWriter2 = FolgenWriter.create(datei4);
        folgenReader1 = FolgenReader.create(datei1,datei1,FolgenReaderInitValue);
        folgenReader2 = FolgenReader.create(datei2,datei2,FolgenReaderInitValue);

        initialReader = FolgenReader.create("InitialReader", sourceFilePath,FolgenReaderInitValue);
        initialReader.setRunLevel(FolgenReaderInitValue);

        sourceFileSize = initialReader.getFileSize();
        initFileFolgenLength = sourceFileSize/4;
        
    }


    @Override
    public DataWrapper readBlock() {
        if(initialReader.hasNextFolge()){
            return initialReader.getFolge();
        }else{
            return DataWrapperImpl.create(new int[0], 0, true);
        }
    }

    @Override
    public DataWrapper createOuputChannel() {
        return DataWrapperImpl.create(new int[(int)Writer.INTEGER_COUNT_PER_WRITE], 0, false);
    }

    private DataWrapper createEmptyDataWrapper() {
        return DataWrapperImpl.create(new int[0],0,false);
    }

    @Override
    public DataWrapper readLeftChannel() {
        if(leftChannelHasNext() && !finish){
            return folgenReader1.getFolge();
        }else {
            return createEmptyDataWrapper();
        }
    }

    @Override
    public DataWrapper readRightChannel() {
        if(rightChannelHasNext() && !finish){
            return folgenReader2.getFolge();
        }else {
            return createEmptyDataWrapper();
        }
    }

    @Override
    public boolean leftChannelHasNext() {
        return folgenReader1.hasNextFolge();
    }

    @Override
    public boolean rightChannelHasNext() {
        return folgenReader2.hasNextFolge();
    }

//    @Deprecated
//    private DataWrapper[] read() {
//        DataWrapper[] dataWrappers = new DataWrapper[2];
//
//            fileLeft = folgenReader1.hasNextFolge();
//
//            if(fileLeft){
//                dataWrappers[0] = folgenReader1.getFolge();
//            }else{
//                dataWrappers[0] = DataWrapperImpl.create(new int[0], 0, true);
//            }
//
//            fileRight = folgenReader2.hasNextFolge();
//
//            if(fileRight){
//                dataWrappers[1] = folgenReader2.getFolge();
//            }else{
//                dataWrappers[1] = DataWrapperImpl.create(new int[0], 0, true);
//            }
//        return dataWrappers;
//    }

    @Override
    public void write(DataWrapper dataWrapper) {

        if(writeSwitch){
            folgenWriter1.writeFolge(dataWrapper);
        }else{
            folgenWriter2.writeFolge(dataWrapper);
        }
        //folgenWriter1.
            if(dataWrapper.isFolgeKomplett()){
                writeSwitch = !writeSwitch;
            }
              
        if(!initialReader.hasNextFolge()){
            if(!(leftChannelHasNext()) && !(rightChannelHasNext())){ //&& dataWrapper.isFolgeKomplett()
             System.gc();

                if(bigSwitch){
                    if(FolgenReaderValue>initFileFolgenLength){
                        System.out.println((!writeSwitch?folgenWriter1:folgenWriter2));
                    }
                    folgenWriter1.close();
                    folgenWriter2.close();
                    folgenReader1.resetFile();
                    folgenReader2.resetFile();
                    folgenReader1 = FolgenReader.create(datei3,datei3,FolgenReaderValue);
                    folgenReader2 = FolgenReader.create(datei4,datei4,FolgenReaderValue);
                    folgenWriter1 = FolgenWriter.create(datei1);
                    folgenWriter2 = FolgenWriter.create(datei2);

                    bigSwitch = false;

                } else {
                    if(FolgenReaderValue>initFileFolgenLength){
                        System.out.println((!writeSwitch?folgenWriter1:folgenWriter2));
                    }
                    folgenWriter1.close();
                    folgenWriter2.close();
                    folgenReader1.resetFile();
                    folgenReader2.resetFile();
                    folgenReader1 = FolgenReader.create(datei1,datei1,FolgenReaderValue);
                    folgenReader2 = FolgenReader.create(datei2,datei2,FolgenReaderValue);
                    folgenWriter1 = FolgenWriter.create(datei3);
                    folgenWriter2 = FolgenWriter.create(datei4);

                    bigSwitch = true;

                }
                finish = folgenReader1.getFileSize() == sourceFileSize || folgenReader2.getFileSize() == sourceFileSize;
                if(FolgenReaderValue>2*initFileFolgenLength){
                    //System.exit(0);
                }
                System.out.println("mal 2");
                FolgenReaderValue *= 2;
            }
        }
    }
    public void closeAllChannelsIfOpen(){
        if(folgenReader1.isOpen())
            folgenReader1.close();
        if(folgenReader2.isOpen())
            folgenReader2.close();
        if(folgenWriter1.isOpen())
            folgenWriter1.close();
        if(folgenWriter2.isOpen())
            folgenWriter2.close();
        //falls es noetig sein sollte
        System.gc();
    }
    //Create TargetFile
    public String signSortedFile(){
        long dSize1 = 0, dSize2 = 0, dSize3 = 0, dSize4 = 0;
        boolean found = false;
        File usedFiles;
        Path path;

        final String endDateiSorted = "EnddateiSorted";
        String enddateiName = "";


        try {
            dSize1 = Files.size(Paths.get(datei1));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            dSize2 = Files.size(Paths.get(datei2));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            dSize3 = Files.size(Paths.get(datei3));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            dSize4 = Files.size(Paths.get(datei4));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if(sourceFileSize == dSize1){
            enddateiName = datei1;
            found = !found;
        }else if (sourceFileSize == dSize2){
            enddateiName = datei2;
            found = !found;
        }else if(sourceFileSize == dSize3){
            enddateiName = datei3;
            found = !found;
        }else if (sourceFileSize == dSize4){
            enddateiName = datei4;
            found = !found;
        }

        if(found){
            File endFile = new File(enddateiName);
            File endFileRenamed = new File(endDateiSorted);

            if(endFile.renameTo(endFileRenamed)){
                System.out.println("Erfolgreiche umbenennung: "+enddateiName+" -> "+endDateiSorted);
            }else{
                System.out.println("Unerfolgreiche umbenennung: "+enddateiName+" -> "+endDateiSorted);
            }

            for (String dateiName : dateiNamen){
//                endFile = new File(dateiName);
                if(endFile.exists())
                    System.gc();
                    try{
                        path = Paths.get(dateiName);

                        if(Files.exists(path))
                            Files.delete(path);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
            }

            return endFileRenamed.getAbsolutePath();
        }else {
            System.out.println(String.valueOf("Keine Enddatei gefunden!"));
            return null;
        }
    }

    */
}
