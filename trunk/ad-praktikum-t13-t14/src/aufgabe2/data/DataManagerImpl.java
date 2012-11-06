package aufgabe2.data;

import aufgabe2.interfaces.DataManager;
import aufgabe2.interfaces.DataWrapper;

import java.nio.channels.FileChannel;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: abg667
 * Date: 31.10.12
 * Time: 16:41
 */
public class DataManagerImpl implements DataManager {
    // optimal folgenLength ist ne 2er potenz
    // start könnte bei 8 oder 16 sein
    public final int folgenLength = 8;
    private final int FolgenReaderInitValue = 10;
    private int FolgenReaderValue = FolgenReaderInitValue;

    //Linkes File, rechtes File

    final String datei1 = "file1", datei2 = "file2", datei3 = "file3", datei4 = "file4";
    FolgenWriter folgenWriter1;
    FolgenWriter folgenWriter2;
    FolgenReader folgenReader1;
    FolgenReader folgenReader2;

    boolean fileLeft = false, fileRight = false, writeSwitch = true, bigSwitch = true;
    //SourceFile
    private final String sourceFilePath = "zahlenfolge";
    FolgenReader initialReader;
    long sourceFileSize = 0;
    // ca 1.416.872 KB RAM  ( 357923000*4)
    private final int readerSize = 44740375;

    private final int writerSize = readerSize * 2;

    public DataManagerImpl() {

        folgenWriter1 = FolgenWriter.create(datei3);
        folgenWriter2 = FolgenWriter.create(datei4);
        folgenReader1 = FolgenReader.create(datei1,datei1,FolgenReaderInitValue);
        folgenReader2 = FolgenReader.create(datei2,datei2,FolgenReaderInitValue);

        initialReader = FolgenReader.create("InitialReader", sourceFilePath,FolgenReaderInitValue);
        initialReader.setRunLevel(FolgenReaderInitValue);
        sourceFileSize = initialReader.getFileSize();
    }
    @Deprecated
    private DataWrapper createDataWrapper(int[] data, int size, boolean folgeKomplett) {
        return DataWrapperImpl.create(data, size, folgeKomplett);
    }

    @Override
    public DataWrapper readBlock() {
        if(initialReader.hasNextFolge()){
            return initialReader.getFolge();
        }else{
            return createDataWrapper(new int[0], 0, true);
        }
    }

    @Override
    public DataWrapper createDataWrapper() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DataWrapper readLeftChannel() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DataWrapper readRightChannel() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Deprecated
    private DataWrapper[] read() {
        DataWrapper[] dataWrappers = new DataWrapper[2];

            fileLeft = folgenReader1.hasNextFolge();

            if(fileLeft){
                dataWrappers[0] = folgenReader1.getFolge();
            }else{
                dataWrappers[0] = createDataWrapper(new int[0],0,true);
            }

            fileRight = folgenReader2.hasNextFolge();

            if(fileRight){
                dataWrappers[1] = folgenReader2.getFolge();
            }else{
                dataWrappers[1] = createDataWrapper(new int[0], 0, true);
            }
        return dataWrappers;
    }

    @Override
    public void write(DataWrapper dataWrapper) {

        if(writeSwitch){
            folgenWriter1.writeFolge(dataWrapper.getData());
        }else{
            folgenWriter2.writeFolge(dataWrapper.getData());
        }

            if(dataWrapper.isFolgeKomplett())
                        if(writeSwitch){
                            writeSwitch = false;
                        }else{
                            writeSwitch = true;
                        }

        if(!(fileLeft) && !(fileRight)){
            FolgenReaderValue *= 2;
            if(bigSwitch){
                folgenReader1.resetFile();
                folgenReader2.resetFile();
                folgenReader1 = FolgenReader.create(datei3,datei3,FolgenReaderValue);
                folgenReader2 = FolgenReader.create(datei4,datei4,FolgenReaderValue);
                folgenWriter1 = FolgenWriter.create(datei1);
                folgenWriter2 = FolgenWriter.create(datei2);

                bigSwitch = false;

            } else {
                folgenReader1.resetFile();
                folgenReader2.resetFile();
                folgenReader1 = FolgenReader.create(datei1,datei1,FolgenReaderValue);
                folgenReader2 = FolgenReader.create(datei2,datei2,FolgenReaderValue);
                folgenWriter1 = FolgenWriter.create(datei3);
                folgenWriter2 = FolgenWriter.create(datei4);

                bigSwitch = true;

            }
            System.gc();
        }
    }

    //Java-Garbage-Collection-@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void GB_Test() {
        int value = 1;
        int[] a;
        while(value < 268435456){
            //ohne garbage collector bombt er den ram voll
            System.gc();
            a = new int[value];
            value*=2;
            System.out.println(value);
            System.out.println("Sollte: "+ ((double)value*4/1024/1024/1024) + " GByte");
        }
    }
}
