package aufgabe2.data;

import aufgabe2.interfaces.DataWrapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

/**
 * Created with IntelliJ IDEA.
 * User: Sven
 * Date: 03.11.12
 * Time: 16:33
 */
public class Reader {
    private FileChannel fileChan;
    private long currentCursorPosition;
    private long fileChanSize;
    private String fileName;
    private String name;
    private int hasNextCount;



    public static int
            INTEGER_SIZE  = 4,
            INTEGER_COUNT_PER_READ = 18;

    private static int
            byteBufferSize = INTEGER_SIZE* INTEGER_COUNT_PER_READ;

    public static Reader create(String name,String fileName){
        return new Reader(name, fileName);
    }


    private Reader(String name, String fileName){
        this.name = name;
        this.fileName = fileName;

        try {
            fileChan = new FileInputStream(fileName).getChannel();
            if(fileChan.size() % byteBufferSize == 0){
                hasNextCount =(int) (fileChan.size()/byteBufferSize);
            }else{
                hasNextCount = (int) (fileChan.size()/byteBufferSize) + 1; // +1 damit der "rest" noch oben drauf kommt
            }

            fileChanSize = fileChan.size();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public String getFileName(){
        return fileName;
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


    public IntBuffer getIntBuffer() {
        try{
            long newCursorPosition = currentCursorPosition+byteBufferSize;
            hasNextCount--;
            if(newCursorPosition <= fileChanSize){
                IntBuffer ib = fileChan.map(FileChannel.MapMode.READ_ONLY,currentCursorPosition,byteBufferSize).asIntBuffer();
                currentCursorPosition = newCursorPosition;
                return ib;
            }else{

                // der lästige rest fall...
                int sizeLeft = (int) (fileChanSize- currentCursorPosition);
                if(sizeLeft%4 != 0) throw  new IOException();
                IntBuffer ib = fileChan.map(FileChannel.MapMode.READ_ONLY,currentCursorPosition,sizeLeft).asIntBuffer();
                currentCursorPosition+=sizeLeft;
                return  ib;
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return  null;
    }

}
