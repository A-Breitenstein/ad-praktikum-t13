package aufgabe2.data;

import java.io.*;
import java.nio.ByteBuffer;
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
    private FileOutputStream fOS;
    private long overAllWriteCount = 0;


    private Writer(String fileName){
        this.fileName = fileName;
        try{
            fOS = new FileOutputStream(fileName);
            fileChan = fOS.getChannel();
            fileChan.force(true);

        }catch (IOException e){

            System.out.println(fileName+": KONNTE WRITER NICHT ERSTELLEN");

        }
    }
    public static Writer create(String fileName){
        return  new Writer(fileName);
    }
    public void writeByteBufferToFile(ByteBuffer byteBuffer){
         try{
             int size = byteBuffer.capacity()/4;
             try{
                fileChan.write(byteBuffer);
                 byteBuffer.clear(); // <--------- WTFX? ich wusste nicht das man den byteBuffer auch clearen muss
                 // wenn man auf nen intbuffer hat ... und auf die clear called wtf..
                 overAllWriteCount+=size;
                 System.out.println(fileName+": ERFOLGREICH GESCHRIEBEN: "+size);
             }catch (NullPointerException e){
                 System.out.println(fileName+": NOTFALL WRITER WURDE ERSTELLT");
                 fOS = new FileOutputStream(fileName);
                 fileChan = fOS.getChannel();
                 fileChan.force(true);
                 fileChan.write(byteBuffer);
                 overAllWriteCount+=size;
                 System.out.println(fileName+": ERFOLGREICH GESCHRIEBEN: "+size);
             }
         }catch (IOException e){
             System.out.println(fileName+": NOTFALL WRITER NICHT ERSTELLT");
         }
    }
    public String getFileName(){
        return fileName;
    }
    public void close(){
        try {
            // wenn er zu dem zeitpunkt hier null ist, dann wurder er nicht benutzt
            if(fileChan != null){
                fileChan.close();
                fOS.close();
            }
            System.out.println(fileName+": was successfully closed! this writer has written "+overAllWriteCount+" integer");
        } catch (IOException e) {
            System.out.println(fileName+": WRITER KONNTE NICHT GECLOSET WERDEN ");
        }
    }

    public boolean isOpen(){
        return fileChan.isOpen();
    }

}
