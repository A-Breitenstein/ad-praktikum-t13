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

    List<Group> groupList;
    Group currentGroup;
    Map<String,FolgenReader> folgenReaderMap;
    Map<String,FolgenWriter> folgenWriterMap;

    final String filePath1 = "file1", filePath2 = "file2", filePath3 = "file3", filePath4 = "file4";
    final String datei1 = "file1", datei2 = "file2", datei3 = "file3", datei4 = "file4";

    FolgenWriter folgenWriter1;
    FolgenWriter folgenWriter2;
    FolgenReader folgenReader1;
    FolgenReader folgenReader2;

    FileChannel file1, file2, file3, file4;

    //Linkes File, rechtes File
    boolean fileLeft = false, fileRight = false;
    //SourceFile
    private final String sourceFilePath = "zahlenfolge";
    FolgenReader initialReader;
    long sourceFileSize = 0;
    // ca 1.416.872 KB RAM  ( 357923000*4)
    private final int readerSize = 44740375;
    private final int writerSize = readerSize * 2;

    private final int FolgenReaderInitValue = 10;

    public DataManagerImpl() {

        Group group1 = Group.createGroup(1, datei1, datei2);
        Group group2 = Group.createGroup(2, datei3, datei4);
        groupList.addAll(Arrays.asList(group1,group2));
        currentGroup = groupList.get(0);

        folgenWriterMap.put(datei3,FolgenWriter.create(filePath3));
        folgenWriterMap.put(datei4,FolgenWriter.create(filePath4));

        folgenReaderMap.put(datei1, FolgenReader.create(datei1, filePath1, FolgenReaderInitValue));
        folgenReaderMap.put(datei2, FolgenReader.create(datei2, filePath2, FolgenReaderInitValue));

        initialReader = FolgenReader.create("IntitialReader", sourceFilePath);
        initialReader.setRunLevel(FolgenReaderInitValue);
        sourceFileSize = initialReader.getFileSize();
    }

    @Override
    public DataWrapper createDataWrapper(int[] data, int size, boolean folgeKomplett) {
        return DataWrapperImpl.create(data, size, folgeKomplett);
    }

    @Override
    public DataWrapper readBlock() {
        if(initialReader.HasNextFolge()){
            return initialReader.getFolge();
        }else{
            return createDataWrapper(new int[0], 0, true);
        }
    }

    @Override
    public DataWrapper[] read() {
        int groupId = currentGroup.getGroupID();
        DataWrapper[] dataWrappers = new DataWrapper[2];



        if( groupId == 1 || groupId == 2){

            fileLeft = folgenReaderMap.get(currentGroup.getCurrentFile()).HasNextFolge();

            if(fileLeft){
                dataWrappers[0] = folgenReaderMap.get(currentGroup.getCurrentFile()).getFolge();
            }else{
                dataWrappers[0] = createDataWrapper(new int[0],0,true);
            }

            currentGroup.switchFile();

            fileRight = folgenReaderMap.get(currentGroup.getCurrentFile()).HasNextFolge();

            if(fileRight){
                dataWrappers[1] = folgenReaderMap.get(currentGroup.getCurrentFile()).getFolge();
            }else{
                dataWrappers[1] = createDataWrapper(new int[0], 0, true);
            }

            currentGroup.switchFile();


        }else {
            System.out.println("This Group does not exists: " +currentGroup.getGroupID());
        }



        return dataWrappers;
    }

    @Override
    public void write(DataWrapper dataWrapper) {

        switchGroup();

        folgenWriterMap.get(currentGroup.getCurrentFile()).writeFolge(dataWrapper.getData());
        currentGroup.switchFile();

            if(dataWrapper.isFolgeKomplett())
                currentGroup.switchFile();

        switchGroup();

        if(!(fileLeft) && !(fileRight)){
            folgenWriterMap.clear();

            folgenReaderMap.get(currentGroup.getCurrentFile()).resetFile();
            folgenWriterMap.put(currentGroup.getCurrentFile(),FolgenWriter.create(currentGroup.getCurrentFile()));

            currentGroup.switchFile();

            folgenReaderMap.get(currentGroup.getCurrentFile()).resetFile();
            folgenWriterMap.put(currentGroup.getCurrentFile(),FolgenWriter.create(currentGroup.getCurrentFile()));

            currentGroup.switchFile();

            switchGroup();

            folgenReaderMap.clear();

            folgenReaderMap.put(currentGroup.getCurrentFile(),FolgenReader.create(currentGroup.getCurrentFile(),currentGroup.getCurrentFile()));
            currentGroup.switchFile();
            folgenReaderMap.put(currentGroup.getCurrentFile(),FolgenReader.create(currentGroup.getCurrentFile(),currentGroup.getCurrentFile()));
            currentGroup.switchFile();

            System.gc();
        }
    }

    //GROUP-SWITCH-@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    void switchGroup(){
        switchGroup(currentGroup, groupList);
    }

    void switchGroup(Group currentGroup, List<Group> groupList){
        final int currentGroupIndex = groupList.indexOf(currentGroup);

        if(currentGroupIndex+1 > groupList.size()-1){
            currentGroup = groupList.get(0);
        }else{
            currentGroup = groupList.get(currentGroupIndex+1);
        }
    }

    private void GB_Test() {
        int value = 1;
        int[] a;
        while(value < 268435456){
            System.gc();
            a = new int[value];
            value*=2;
            System.out.println(value);
            System.out.println("Sollte: "+ ((double)value*4/1024/1024/1024) + " GByte");
        }
    }
}
