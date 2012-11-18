package aufgabe2.data;

import aufgabe2.interfaces.DataWrapper;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: Sven
 * Date: 04.11.12
 * Time: 13:37
 */
public class FolgenWriter {
    private Writer fileWriter;
    private final int BUFFERSIZE;
    
    
    // der byteBuffer hinter dem intBuffer,
    private ByteBuffer byteBuffer;
    // intBuffer ist nur die view auf den bytebuffer
    private IntBuffer intBuffer;

    public static FolgenWriter create(String fileName, int bufferSize){
        return new FolgenWriter(fileName, bufferSize);
    }
    private  FolgenWriter(String fileName, int bufferSize){
        fileWriter = Writer.create(fileName);
        BUFFERSIZE = bufferSize;
        initBuffer();
    }
    private void initBuffer(){
        // buffer der maximalen groeße erstellen
        byteBuffer = ByteBuffer.allocate(BUFFERSIZE);
        // bytebuffer als intbuffer interpretieren
        intBuffer = byteBuffer.asIntBuffer();
    }
    public void close(){
        // hier muss noch geguckt werden welches besser funzt...
        if(intBuffer.position() != 0 || intBuffer.capacity() != intBuffer.remaining()){
            writeBuffer();
        }
        fileWriter.close();
    }
    private void writeBuffer(){
        // wenn der intbuffer komplett voll ist schreib ihn so weg,
        // sollte er nicht voll sein, dann MUSS der leere teil entfernt werden
        // damit keine nullen in die datei geschrieben werden Oder sogar müll
        // der sich noch im buffer befindet
        if(intBuffer.remaining()==0){
//            intBuffer.flip();

            // warum byteBuffer? weil der intbuffer nur ne view auf den bytebuffer ist
            fileWriter.writeByteBufferToFile(byteBuffer);
            intBuffer.clear();
        }else{
            // daten von dem zugroßen buffer (intBuffer) umschaufeln in den tmp_intbuffe um sie
            // korekt wegschreiben zukönnen.
            int positions = intBuffer.position();
            ByteBuffer tmp_byteBuff = ByteBuffer.allocate((Integer.SIZE / Byte.SIZE) * positions);
            IntBuffer tmp_intBuff = tmp_byteBuff.asIntBuffer();
            intBuffer.flip();
            for(int i= 0;i < positions;i++){
                tmp_intBuff.put(intBuffer.get());
            }
            fileWriter.writeByteBufferToFile(tmp_byteBuff);
            intBuffer.clear();

        }
    }


    public void writeFolge(DataWrapper wrap){
        // wenn die folgenLaenge gleich dem restlichem platz im buffer entspricht,
        // dann du die folge rein und schreib sie raus
        System.out.println(fileWriter.getFileName()+": Bufferauslastung: "+((double)intBuffer.position())/intBuffer.capacity());

        int[] folge = wrap.getData();
        int folgenLaenge = wrap.getSize();
        if(folgenLaenge>BUFFERSIZE / 4){
            System.out.println(fileWriter.getFileName()+": folgenLaenge zu lang, dass tut weeehh  meeehh");
            System.exit(1);
        }

        if(folgenLaenge == intBuffer.remaining()){
            intBuffer.put(wrap.getData(),0,folgenLaenge);
            writeBuffer();
            intBuffer.clear();
            // wenn die folgenLaenge kleiner ist dann einfach in den buffer rein
        }else if(folgenLaenge < intBuffer.remaining()){
            intBuffer.put(wrap.getData(),0,folgenLaenge);
        }else {
            // und wenn sie größer ist den rest auffüllen und dann den rest der folge
            // in den geleerten buffer schreiben
            // berechne den rest der folge der nicht in den buffer passt
            int derRestDerNichtPasst = folgenLaenge - intBuffer.remaining();
            // schreib den teil der "passt" in den buffer
            intBuffer.put(folge,0,intBuffer.remaining());
            writeBuffer();
            intBuffer.clear();
            // füll den rest der nicht passte nun in den intbuffer
            for (int i = folgenLaenge-derRestDerNichtPasst; i < folgenLaenge ; i++) {
                intBuffer.put(folge[i]);
            }
        }
    }
   

    @Override
    public String toString() {
        return fileWriter.getFileName();
    }
    public boolean isOpen(){
        return fileWriter.isOpen();
    }
}
