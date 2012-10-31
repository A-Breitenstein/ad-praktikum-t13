package aufgabe2.data;

import aufgabe2.interfaces.DataManager;
import aufgabe2.interfaces.DataWrapper;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created with IntelliJ IDEA.
 * User: abg667
 * Date: 31.10.12
 * Time: 16:41
 */
public class DataManagerImpl implements DataManager {

    DataWrapper read1;
    DataWrapper read2;
    DataWrapper read3;
    DataWrapper read4;
    DataWrapper write1;
    DataWrapper write2;



    FileChannel file1,file2,file3,file4;
    // ca 1.416.872 KB RAM  ( 357923000*4)
    private final int readerSize = 44740375;
    private final int writerSize = readerSize*2;

    private final String sourceFile = "zahlenfolge";

    public static void main(String[] args) {
        DataManager d = new DataManagerImpl();
        while (true){

        }
    }
    public DataManagerImpl() {
        read1 = createDataWrapper(new int[readerSize],0);
        read2 = createDataWrapper(new int[readerSize],0);
        read3 = createDataWrapper(new int[readerSize],0);
        read4 = createDataWrapper(new int[readerSize],0);
        write1 = createDataWrapper(new int[writerSize],0);
        write2 = createDataWrapper(new int[writerSize],0);




         byte[] byteArray = new byte[4];
        byteArray[0] = 0;
        byteArray[1] = 0;
        byteArray[2] = 0;
        byteArray[3] = 1;



        IntBuffer intBuf = ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
        int[] array = new int[intBuf.remaining()];
        intBuf.get(array);
        System.out.println(array[0]);

//        try {
//            DataInputStream  instr = new DataInputStream(new BufferedInputStream(new FileInputStream( sourceFile ) ) );
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }

    }

    public DataWrapper createDataWrapper(int[] data, int size){
        return DataWrapperImpl.create(data, size);
    }


    @Override
    public DataWrapper readBlock(int blockSize) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DataWrapper readBlock() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DataWrapper[] read() {
        return new DataWrapper[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void write(DataWrapper dataWrapper) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
    private void fetchDataFromFile(DataWrapper whichReader,String filePath){

    }
}
