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
    private long currentByteBufferSize;
    private int[] intArray;
    private double folgenPerIntArray;
    private int currentIntegerCount;

    private static final int
    INTEGER_SIZE  = 4,
    INTEGER_COUNT = 500;


    private static int
            byteBufferSizeMax = INTEGER_SIZE*INTEGER_COUNT;


    public static FileReader create(String name,String fileName,long currentFolgeLength){
        return new FileReader(name, fileName,currentFolgeLength);
    }


    private FileReader(String name, String fileName,long currentFolgeLength){

        this.name = name;
        this.fileName = fileName;

        try {
            fileChan = new FileInputStream(fileName).getChannel();
            resetFileChan(currentFolgeLength);

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
    public void resetFileChan(long currentFolgeLength){

        double hundertProzent = 1.0;
        long currentFolgeSizeInByte = currentFolgeLength*INTEGER_SIZE;
        double bufferAuslastungsVerhaeltnis = ((double)currentFolgeSizeInByte/byteBufferSizeMax);

        if(bufferAuslastungsVerhaeltnis < hundertProzent){
            folgenPerIntArray = (hundertProzent/bufferAuslastungsVerhaeltnis);
            currentByteBufferSize = ((int)folgenPerIntArray) * currentFolgeSizeInByte;
            currentIntegerCount =(int)(currentByteBufferSize / INTEGER_SIZE);

        }else{
            // bufferAuslastungsVerhaeltnis > 100% bedeutet, dass die folge
            // nicht mit einmal laden in den buffer geladen werden kann
            // bsp bufferAuslastungsVerhaeltnis = 4,7 == 470%
            // => overflow 5,0 == 500% d.h. 5 mal laden um die folge
            // komplett geladen zu haben
            int overflow = ((int)bufferAuslastungsVerhaeltnis+1);
            if(currentFolgeSizeInByte % overflow == 0){

                currentByteBufferSize = currentFolgeSizeInByte/overflow;
            }else{
                // sollte sich die currentFolgeSizeInByte nicht durch overflow teilen lassen,
                // finde eine zahl die es tut...
                while(currentFolgeSizeInByte % overflow != 0){
                    overflow+=1;
                }
                currentByteBufferSize = currentFolgeSizeInByte/overflow;
            }
            folgenPerIntArray = hundertProzent/overflow;
            currentIntegerCount = (int)(currentByteBufferSize / INTEGER_SIZE);
        }


        try{
            fileChanSize = fileChan.size();
            hasNextCount = (int)(fileChanSize/currentByteBufferSize)+1;
        }catch(IOException e){
            e.printStackTrace();
        }
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
        long newCursorPosition = currentCursorPosition+currentByteBufferSize;
        hasNextCount--;
        if(newCursorPosition < fileChanSize){
            IntBuffer ib = fileChan.map(FileChannel.MapMode.READ_ONLY,currentCursorPosition,currentByteBufferSize).asIntBuffer();
            currentCursorPosition = newCursorPosition;

            intArray = new int[currentIntegerCount];
            ib.get(intArray);
            return DataWrapperImpl.create(intArray,currentIntegerCount);
        }else{

            // der lästige rest fall...
            int sizeLeft = (int) (fileChanSize- currentCursorPosition);
            if(sizeLeft%4 != 0) throw  new IOException();
            IntBuffer ib = fileChan.map(FileChannel.MapMode.READ_ONLY,currentCursorPosition,sizeLeft).asIntBuffer();
            currentCursorPosition+=sizeLeft;
            int int_count = (int)(sizeLeft/INTEGER_SIZE);
            intArray = new int[int_count];
            ib.get(intArray);
            return DataWrapperImpl.create(intArray,int_count);
        }
    }

    public static void main(String[] args) {
        FileReader reader = create("abc","sortiert.file",10);
        try {
            DataWrapper tmp = reader.getIntArray();
            int[] array = tmp.getData();
            for (int i = 0; i < array.length ; i++) {
                System.out.println(array[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
