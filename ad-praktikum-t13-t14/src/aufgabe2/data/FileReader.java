package aufgabe2.data;

import aufgabe2.interfaces.DataWrapper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

/**
 * Created with IntelliJ IDEA.
 * User: Sven
 * Date: 01.11.12
 * Time: 21:48
 */
public class FileReader {
    private FileChannel fileChan;
    private long currentCursorPosition;
    private long fileChanSize;
    private String fileName;
    private String name;
    private int hasNextCount;

    private int[] intArray;

    private static final int
    INTEGER_SIZE  = 4,
    INTEGER_COUNT = 50;

    private static int
            byteBufferSize = INTEGER_SIZE*INTEGER_COUNT;


    public static FileReader create(String name,String fileName){
        return new FileReader(name, fileName);
    }


    private FileReader(String name, String fileName){
        this.name = name;
        this.fileName = fileName;

        try {
            fileChan = new FileInputStream(fileName).getChannel();

            hasNextCount = (int) (fileChan.size()/byteBufferSize) + 1; // +1 damit der "rest" noch oben drauf kommt

            fileChanSize = fileChan.size();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public long getFileChanSize(){
        return fileChanSize;
    }
    public void close() throws IOException{
        fileChan.close();
    }
    public boolean hasNextIntArrray(){
        return hasNextCount > 0;
    }


    /**
     * Holt einen IntArray der groeße FileReader.INTEGER_COUNT aus dem gesetzten fileChannel
     * <p>UsageTemplate:</p>
     * <p>while(filereader.hasNextIntArray())<br>
     *     {
     *     <br>DataWrapper dwrap = filereader.getIntArray()<br>
     *     }
     * </p>
     *
     * @return DataWrapper
     * @throws java.io.IOException
     */
    public DataWrapper getIntArray() throws IOException {
        long newCursorPosition = currentCursorPosition+byteBufferSize;
        hasNextCount--;
        if(newCursorPosition < fileChanSize){
            IntBuffer ib = fileChan.map(FileChannel.MapMode.READ_ONLY,currentCursorPosition,byteBufferSize).asIntBuffer();
            currentCursorPosition = newCursorPosition;

            intArray = new int[INTEGER_COUNT];
            ib.get(intArray);
            return DataWrapperImpl.create(intArray,INTEGER_COUNT);
        }else{

            // der lästige rest fall...
            int sizeLeft = (int) (fileChanSize- currentCursorPosition);
            if(sizeLeft%4 != 0) throw  new IOException();
            IntBuffer ib = fileChan.map(FileChannel.MapMode.READ_ONLY,currentCursorPosition,sizeLeft).asIntBuffer();
            currentCursorPosition+=sizeLeft;
            int int_count = (int)(sizeLeft/4);
            intArray = new int[int_count];
            return DataWrapperImpl.create(intArray,int_count);
        }
    }
}
