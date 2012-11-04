package aufgabe2.data;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

/**
 * Created with IntelliJ IDEA.
 * User: Sven
 * Date: 04.11.12
 * Time: 13:06
 */
public class Writer {
    private FileChannel fileChan;
    private String fileName;
    public static int INTEGER_COUNT_PER_WRITE = 2 * Reader.INTEGER_COUNT_PER_READ ;
    private Writer(String fileName){
        this.fileName = fileName;
        try{
            fileChan = new FileOutputStream(fileName).getChannel();
        }catch (FileNotFoundException e){
            System.out.println(fileName+" not Found!" );
            e.printStackTrace();

        }
    }
    public static Writer create(String fileName){
        return  new Writer(fileName);
    }
    private void writeIntArray(int[] array) {
        try {
            ByteBuffer byteBuff = ByteBuffer.allocate((Integer.SIZE / Byte.SIZE) * array.length);
            IntBuffer intBuff = byteBuff.asIntBuffer();
            intBuff.put(array);
            intBuff.flip();
            fileChan.write(byteBuff);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeByteBufferToFile(ByteBuffer byteBuffer){
         try{
             fileChan.write(byteBuffer);
         }catch (IOException e){

         }
    }
    public String getFileName(){
        return fileName;
    }
}
