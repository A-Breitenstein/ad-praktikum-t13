package aufgabe2.data;

import aufgabe2.interfaces.DataManager;
import aufgabe2.interfaces.DataWrapper;

/**
 * Created with IntelliJ IDEA.
 * User: abg667
 * Date: 31.10.12
 * Time: 16:41
 */
public class DataManagerImpl implements DataManager {
    // optimal folgenLength ist ne 2er potenz
    // start könnte bei 8 oder 16 sein
    private final int FolgenReaderInitValue = 10;
    private long FolgenReaderValue = FolgenReaderInitValue;

    private long initFileFolgenLength;

    //Linkes File, rechtes File

    private String datei1 = "file1", datei2 = "file2", datei3 = "file3", datei4 = "file4";
    FolgenWriter folgenWriter1;
    FolgenWriter folgenWriter2;
    FolgenReader folgenReader1;
    FolgenReader folgenReader2;

    boolean writeSwitch = true, bigSwitch = true;
    //SourceFile
    //private final String sourceFilePath = "zahlenfolge";
    FolgenReader initialReader;
    long sourceFileSize = 0;
    // ca 1.416.872 KB RAM  ( 357923000*4)

    public DataManagerImpl(String sourceFilePath) {
        datei1 = sourceFilePath+"1";
        datei2 = sourceFilePath+"2";
        datei3 = sourceFilePath+"3";
        datei4 = sourceFilePath+"4";

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
        if(leftChannelHasNext()){
            return folgenReader1.getFolge();
        }else {
            return createEmptyDataWrapper();
        }
    }

    @Override
    public DataWrapper readRightChannel() {
        if(rightChannelHasNext()){
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

            if(dataWrapper.isFolgeKomplett())
                writeSwitch = !writeSwitch;

        if(!initialReader.hasNextFolge()){
            if(!(leftChannelHasNext()) && !(rightChannelHasNext())){
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
//                if(FolgenReaderValue>2*initFileFolgenLength){
//                    System.exit(0);
//                }
                System.out.println("mal 2");
                FolgenReaderValue *= 2;
            }
        }
    }

    //Java-Garbage-Collection-@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    public static void main(String[] args)
     {
        int value = 1;
        int[] a;
        while(value < 268435456){
            //ohne garbage collector bombt er den ram voll
            //System.gc();
            a = new int[value];
            value*=2;
            System.out.println(value);
            System.out.println("Sollte: "+ ((double)value*4/1024/1024/1024) + " GByte");
        }
    }
}
