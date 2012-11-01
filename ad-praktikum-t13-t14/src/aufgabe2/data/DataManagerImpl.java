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


    FileChannel file1, file2, file3, file4;
    // ca 1.416.872 KB RAM  ( 357923000*4)
    private final int readerSize = 44740375;
    private final int writerSize = readerSize * 2;

    private final String sourceFile = "zahlenfolge";

    public static void main(String[] args) {
        DataManager d = new DataManagerImpl();
    }

    public DataManagerImpl() {
//        read1 = createDataWrapper(new int[readerSize],0);
//        read2 = createDataWrapper(new int[readerSize],0);
//        read3 = createDataWrapper(new int[readerSize],0);
//        read4 = createDataWrapper(new int[readerSize],0);
//        write1 = createDataWrapper(new int[writerSize],0);
//        write2 = createDataWrapper(new int[writerSize],0);

        byte[] byteArray = new byte[4];
        byteArray[0] = 0;
        byteArray[1] = 0;
        byteArray[2] = 0;
        byteArray[3] = 1;

        int[] intArray = new int[4];
        intArray[0] = 0;
        intArray[1] = 0;
        intArray[2] = 0;
        intArray[3] = 1;

        writeIntArray(intArray);

        for (int i = 0; i < intArray.length; i++) {
            System.out.println(intArray[i]);
        }

        int[] intArray2 = new int[4];

        intArray2 = readIntArray();

        for (int i = 0; i < intArray2.length; i++) {
            System.out.println(intArray2[i]);
        }

//        IntBuffer intBuf = ByteBuffer.wrap(byteArray).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
//        int[] array = new int[intBuf.remaining()];
//        intBuf.get(array);
//        System.out.println(array[0]);


//        try {
//            DataInputStream  instr = new DataInputStream(new BufferedInputStream(new FileInputStream( sourceFile ) ) );
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }


    }

    static int[] readIntArray() {
        FileChannel fc = null;
        try {
            fc = new FileInputStream(new File("out.file")).getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        IntBuffer ib = null;
        try {
            ib = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size()).asIntBuffer();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        int[] array = new int[4];
        ib.get(array);
        return array;
    }

    static void writeIntArray(int[] array) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("out.file");
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            ByteBuffer byteBuff = ByteBuffer.allocate((Integer.SIZE / Byte.SIZE) * array.length);
            IntBuffer intBuff = byteBuff.asIntBuffer();
            intBuff.put(array);
            intBuff.flip();
            FileChannel fc = fos.getChannel();
            fc.write(byteBuff);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public DataWrapper createDataWrapper(int[] data, int size) {
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

    private void fetchDataFromFile(DataWrapper whichReader, String filePath) {

    }
}
