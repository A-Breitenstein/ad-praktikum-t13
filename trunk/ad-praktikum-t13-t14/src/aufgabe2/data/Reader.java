package aufgabe2.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.security.InvalidAlgorithmParameterException;

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
    private int hasNextCount;
    private FileInputStream fIS;
    private final long byteBufferSize;
    public final long INTEGER_COUNT_PER_READ;

    public static Reader create(String fileName, int bufferSize){
        return new Reader(fileName, bufferSize);
    }


    private Reader(String fileName, int bufferSize){
        if (bufferSize % Constants.INTSIZE != 0)
        	throw new IllegalArgumentException("BuferSize muss ein Vielfaches von 4 sein.");
        this.fileName = fileName;
        this.byteBufferSize = bufferSize;
        INTEGER_COUNT_PER_READ = bufferSize / Constants.INTSIZE;
        
        try {
            try{
                fIS = new FileInputStream(fileName);
                fileChan = fIS.getChannel();

            }catch(FileNotFoundException e){
                File file = new File(fileName);
                file.createNewFile();
                fIS = new FileInputStream(fileName);

                fileChan = fIS.getChannel();
            }

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
    public boolean isOpen(){
        return fileChan.isOpen();
    }
    public void close() throws IOException{

        fileChan.close();
        fIS.close();
    }
    public boolean hasNextIntArrray(){
//        return (fileChanSize - currentCursorPosition) > 0;//hasNextCount > 0;
        boolean result = false;
        try {
            result = (fileChan.size() - fileChan.position()) > 0;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return result;
    }
    public String toString(){
        String tmp = "Reader has IOException ....";
        try{
         tmp = "hasNextCount = "+hasNextCount+", fileChanSize = "+fileChanSize+", cursorPosition = "+fileChan.position();
        }catch (IOException e){
            e.printStackTrace();
        }
        return  tmp;
    }

    public void read(ByteBuffer target){
        try {
            fileChan.read(target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean isFileFullyReaded(){
        try {
//            System.out.println("filechan.position = " +fileChan.position());
            return fileChan.position()>= fileChan.size();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return true;
    }
    public long getFileChanPosition(){
        try {
            return fileChan.position();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return 0;
    }
    public IntBuffer getIntBuffer(ByteBuffer target) {
        return splitRead(target,8).asIntBuffer();
    }


    /*public IntBuffer getIntBuffer(ByteBuffer target) {
        target.clear();
    	try{
            long newCursorPosition = currentCursorPosition+byteBufferSize;
            hasNextCount--;
            if(newCursorPosition <= fileChanSize){
            	if (target.capacity() < byteBufferSize)
            		throw new IllegalArgumentException("übergebener Buffer zu klein");
                ByteBuffer b = target; //ByteBuffer.allocate((int)byteBufferSize);
                fileChan.read(b,currentCursorPosition);
                b.rewind();
                IntBuffer ib =  b.asIntBuffer();

//                IntBuffer ib = fileChan.map(FileChannel.MapMode.READ_ONLY,currentCursorPosition,byteBufferSize).asIntBuffer();
                currentCursorPosition = newCursorPosition;
                return ib;
            }else{

                // der lästige rest fall...
                int sizeLeft = (int) (fileChanSize- currentCursorPosition);
                if(sizeLeft % 4 != 0) throw  new IOException();
                
                if (target.capacity() < sizeLeft)
            		throw new IllegalArgumentException();
                ByteBuffer b = target;//ByteBuffer.allocate(sizeLeft);
                b.position(sizeLeft);
                b.flip();
                
                fileChan.read(b,currentCursorPosition);

                b.rewind();
                IntBuffer ib =  b.asIntBuffer();

//                IntBuffer ib = fileChan.map(FileChannel.MapMode.READ_ONLY,currentCursorPosition,sizeLeft).asIntBuffer();
                currentCursorPosition+=sizeLeft;
                return  ib;
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return  null;
    }*/

    public ByteBuffer splitRead(ByteBuffer bytebuffer,int read_calls){
            int bytesPerRead = bytebuffer.capacity() / read_calls;
            int rest = bytebuffer.capacity() % read_calls;
                bytebuffer.clear();
                for (int i = 1; i < read_calls ; i++) {
                    if(!isFileFullyReaded()){
                        bytebuffer.limit(bytesPerRead * i);
                        read(bytebuffer);
                    }else break;
                }
                if(!isFileFullyReaded()){
                    bytebuffer.limit((bytesPerRead*read_calls)+rest);
                    read(bytebuffer);
                }


                bytebuffer.flip();
                return bytebuffer;
    }
}
