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
    private int folgenLength;

    private boolean isLastFolgeReached;

    private long writeProgressOverSizedFolge;
    private boolean gotRest;

    // der byteBuffer hinter dem intBuffer,
    private ByteBuffer byteBuffer;
    // intBuffer ist nur die view auf den bytebuffer
    private IntBuffer intBuffer;

    public static FolgenWriter create(String fileName){
        return new FolgenWriter(fileName);
    }
    private  FolgenWriter(String fileName){
        fileWriter = Writer.create(fileName);
        isLastFolgeReached = false;
        initBuffer();
    }
    private void initBuffer(){
        // buffer der maximalen groeße erstellen
        byteBuffer = ByteBuffer.allocate((Integer.SIZE / Byte.SIZE) * Writer.INTEGER_COUNT_PER_WRITE);
        // bytebuffer als intbuffer interpretieren
        intBuffer = byteBuffer.asIntBuffer();
    }
    private void writeBuffer(){
        intBuffer.flip();
        // warum byteBuffer? weil der intbuffer nur ne view auf den bytebuffer ist
        fileWriter.writeByteBufferToFile(byteBuffer);

    }


    public void writeFolge(int[] folge){

        if(folgenLength > Writer.INTEGER_COUNT_PER_WRITE ){
            multipleWritesPerFolge(folge);
       }else {
            singleWritesPerFolge(folge);
       }
    }

    private void multipleWritesPerFolge(int[] folge) {
    }

    private void singleWritesPerFolge(int[] folge) {
        // wenn die folge.length von der folgenLength abweicht => folge ist die Letzte folge die geschrieben wird
        // da nur die letzte folge eine andere groeße haben kann.
        isLastFolgeReached = !(folge.length == folgenLength);
        if( !isLastFolgeReached ){
            // wenn die gegebene folge kleiner ist als unser
            // restlicher platz im intbuffer, dann schreib die folge
            // in den intbuffer. Andernfalls berechne den rest der folge,
            // der erst nach dem schreiben des intbuffers in den buffer geschrieben wird
            if(folgenLength < intBuffer.remaining()){
                intBuffer.put(folge,0,folgenLength);

            }else{
                // berechne den rest der folge der nicht in den buffer passt
                int derRestDerNichtPasst = folgenLength - intBuffer.remaining();
                // schreib den teil der "passt" in den buffer
                intBuffer.put(folge,0,intBuffer.remaining());
                writeBuffer();
                intBuffer.clear();
               // füll den rest der nicht passte nun in den intbuffer
                for (int i = folgenLength-derRestDerNichtPasst; i < folgenLength ; i++) {
                      intBuffer.put(folge[i]);
                }


            }
        }else{
            // für den fall das die letzte folge nicht die folgen länge hat muss die bytebuffer größe
            // angepasst werden
            if(folge.length < folgenLength){
                byteBuffer = ByteBuffer.allocate((Integer.SIZE / Byte.SIZE) * folge.length);
                intBuffer = byteBuffer.asIntBuffer();
                intBuffer.put(folge);
                writeBuffer();

            }else {
                System.out.println(fileWriter.getFileName()+": error! docht nicht die letzte folge? folge.length ("+folge.length+") > folgenLength ("+folgenLength+")");
            }


        }

    }

    public void setRunLevel(int runLevel){

        // runLevel+1, weil ich jetz mal davon ausgehe das die Folgenlangge im aktuellen run
        // durch das verschmelzen ja doppelt so lang wird, bsp: wenn aus beiden streams
        // immer 8 gelesen werden und runlevel 0 ist, dann ist die zu schreibene folge 16 lang

        folgenLength = (int)(Math.pow(2,runLevel)*FolgenReader.INITAL_FOLGEN_LENGTH);
    }

    public static void main(String[] args) {
        FolgenWriter folgenWriter = FolgenWriter.create("newFileFolgeWriter");
        folgenWriter.setRunLevel(0);
        int[] folge1 = {1,2,3,4};
        int[] folge2 = {5,6,7,8};
        int[] folge3 = {9,10};


        folgenWriter.writeFolge(folge1);
        folgenWriter.writeFolge(folge2);
        folgenWriter.writeFolge(folge3);

        FolgenReader folgenReader = FolgenReader.create("Test1","newFileFolgeWriter");
        DataWrapper  wrap;
        int[] array;
        while(folgenReader.HasNextFolge()){

            wrap = folgenReader.getFolge();
            array = wrap.getData();
            for (int i = 0; i < wrap.getSize(); i++) {
                System.out.println(array[i]);
            }

        }
    }
}
