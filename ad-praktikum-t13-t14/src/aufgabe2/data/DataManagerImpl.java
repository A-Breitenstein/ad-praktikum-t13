package aufgabe2.data;

import aufgabe2.interfaces.DataManager;
import aufgabe2.interfaces.DataWrapper;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
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

    DataWrapper read1;
    DataWrapper read2;
    DataWrapper read3;
    DataWrapper read4;
    DataWrapper write1;
    DataWrapper write2;

    Queue<DataWrapper> dataWrappers = new ArrayDeque<DataWrapper>();

    List<Group> groupList;
    Group currentGroup;
    Map<String,FileReader> fileReaderMap;

    final String filePath1 = "file1", filePath2 = "file2", filePath3 = "file3", filePath4 = "file4";
    final String fileName1 = "file1", fileName2 = "file2", fileName3 = "file3", fileName4 = "file4";

    FileChannel file1, file2, file3, file4;
    // ca 1.416.872 KB RAM  ( 357923000*4)
    private final int readerSize = 44740375;
    private final int writerSize = readerSize * 2;

    private final String sourceFile = "zahlenfolge";

    public static void main(String[] args) {
//        int[] i = new int[357000000];
//        int[] o = new int[357000000];
//        int[] p = new int[357000000];
//        int[] q = new int[357000000];
        while(true);

    }

    public DataManagerImpl() {
//        read1 = createDataWrapper(new int[readerSize],0);
//        read2 = createDataWrapper(new int[readerSize],0);
//        read3 = createDataWrapper(new int[readerSize],0);
//        read4 = createDataWrapper(new int[readerSize],0);
//        write1 = createDataWrapper(new int[writerSize],0);
//        write2 = createDataWrapper(new int[writerSize],0);

        Group group1 = Group.createGroup(1,fileName1,fileName2);
        Group group2 = Group.createGroup(2,fileName3,fileName4);
        groupList.addAll(Arrays.asList(group1,group2));
        currentGroup = groupList.get(0);

        fileReaderMap.put(fileName1,FileReader.create(fileName1,filePath1,50));
        fileReaderMap.put(fileName2, FileReader.create(fileName2, filePath2,50));
        fileReaderMap.put(fileName3, FileReader.create(fileName3, filePath3,50));
        fileReaderMap.put(fileName4, FileReader.create(fileName4, filePath4,50));

    }

    @Override
    public DataWrapper createDataWrapper(int[] data, int size, int folgen, boolean folgeKomplett) {
        return DataWrapperImpl.create(data, size, folgen, folgeKomplett);
    }

    @Override
    public DataWrapper readBlock(int blockSize) {
        return null;
    }

    @Override
    public DataWrapper readBlock() {

        return null;
    }

    @Override
    public DataWrapper[] read() {

        DataWrapper[] dWrap1 = {read1,read2,write1};
        DataWrapper[] dWrap2 = {read3,read4,write2};

        //Group1
        if(currentGroup.getCurrentFile().equals(fileName1)){
//            read1 = fileReaderMap.get(currentGroup.getCurrentFile()).getIntArray();
            currentGroup.switchFile();
        }

        if(currentGroup.getCurrentFile().equals(fileName2)){
//            read2 = fileReaderMap.get(currentGroup.getCurrentFile()).getIntArray();
            currentGroup.switchFile();
        }

        if(currentGroup.getGroupID() == 1)
            return dWrap1;

        //Group2
        if(currentGroup.getCurrentFile().equals(fileName3)){
//            read1 = fileReaderMap.get(currentGroup.getCurrentFile()).getIntArray();
            currentGroup.switchFile();
        }

        if(currentGroup.getCurrentFile().equals(fileName4)){
//            read2 = fileReaderMap.get(currentGroup.getCurrentFile()).getIntArray();
            currentGroup.switchFile();
        }

        if(currentGroup.getGroupID() == 2)
            return dWrap2;

        return null;
    }

    @Override
    public void write(DataWrapper dataWrapper) {

    }

    @Override
    public Queue<DataWrapper> exportFolgenToQueue(DataWrapper dataWrapper) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
}
