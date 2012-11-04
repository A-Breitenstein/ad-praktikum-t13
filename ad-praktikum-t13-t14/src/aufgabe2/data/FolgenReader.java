package aufgabe2.data;

import aufgabe2.interfaces.DataWrapper;

import java.nio.IntBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: Sven
 * Date: 03.11.12
 * Time: 16:14
 */

// Speicheraufwand von FolgenReader kommmt durch Reader zustande, beträgt 2 * byteBufferSize aus Reader
// setzt sich aus einem Intbuffer und einem nativ int array zusammen die beide die gleiche groeße haben
public class FolgenReader {
    public static final int
            INTEGER_SIZE = 4;
    private int folgenLength;
    private int folgenCursorPosition;
    private int remainigIntegerInBuffer;

    private int previousBufferCapacity;

    private long loadProgressOverSizedFolge;

    private boolean gotRest;
    private Reader reader;
    private IntBuffer intBuffer;
    public static final int INITAL_FOLGEN_LENGTH = 4;


    private FolgenReader(String name,String fileName){
        reader = Reader.create(name,fileName);
        folgenLength = INITAL_FOLGEN_LENGTH;
        previousBufferCapacity =0;

        initBuffer();

    }
    public static FolgenReader create(String name,String fileName){
        return new FolgenReader(name, fileName);
    }
    private void initBuffer(){
        intBuffer = reader.getIntBuffer();
        previousBufferCapacity = intBuffer.capacity();
        remainigIntegerInBuffer = intBuffer.capacity();
        folgenCursorPosition = 0;
        System.out.println(reader.getFileName()+": Loaded "+intBuffer.capacity()+" Integer in intBuffer");
    }
    private void fillBuffer(){
        previousBufferCapacity = intBuffer.capacity();
        intBuffer = reader.getIntBuffer();
        remainigIntegerInBuffer = intBuffer.capacity();
        folgenCursorPosition = 0;
        System.out.println(reader.getFileName()+": Loaded "+intBuffer.capacity()+" Integer in intBuffer");


    }
    private DataWrapper multipleReadsPerFolge(){
        int[] folge;
        // wenn der buffer nicht leer gemacht wurde weil sich ein
        // folgen ende in dem buffer befunden hat, dann nimm den neuen
        // folgen anfang gebe ihn zurück andernfalls ...
        if(gotRest){

            int restImBufferBzwAnfangDerNeuenFolge = intBuffer.capacity()-intBuffer.position();
            folge = new int[restImBufferBzwAnfangDerNeuenFolge];
            intBuffer.get(folge,0,restImBufferBzwAnfangDerNeuenFolge);
            gotRest = false;
            System.out.println(reader.getFileName()+": FolgenReader::multipleReadsPerFolge() Case: gotRest restImBufferBzwAnfangDerNeuenFolge: "+restImBufferBzwAnfangDerNeuenFolge);
            loadProgressOverSizedFolge+=restImBufferBzwAnfangDerNeuenFolge;
            remainigIntegerInBuffer-=restImBufferBzwAnfangDerNeuenFolge;
            intBuffer.clear();
            fillBuffer();
            return DataWrapperImpl.create(folge,folge.length,1,false);

        }else{
            // andernfalls berechne die restliche Anzahl an Integern um die folge zubeenden,
            // wenn der rest der folge in den buffer passt, dann gebe den rest der folge
            // zurück und makiere die folge als abgeschlossen
            // anderfalls nimm den kompletten buffer inhalt (ist ein teil der folge der sich weder
            // am anfang noch am ende befindet) und gebe ihn zurück
            long rest = folgenLength-loadProgressOverSizedFolge;
            if( rest <= Reader.INTEGER_COUNT_PER_READ){
                folge = new  int[(int)rest];
                intBuffer.get(folge, 0, (int) rest);
                gotRest = true;
                loadProgressOverSizedFolge +=rest;
                remainigIntegerInBuffer-=rest;
                System.out.println(reader.getFileName()+": FolgenReader::multipleReadsPerFolge() Case: rest ("+rest+") <= Reader.INTEGER_COUNT_PER_READ ("+Reader.INTEGER_COUNT_PER_READ+") ");
                System.out.println(reader.getFileName()+": loadProgress: "+loadProgressOverSizedFolge+" von "+folgenLength+" Zahlen der Folge");
                System.out.println(reader.getFileName()+": folgen ende erreicht! returned array laenge: "+folge.length);
                loadProgressOverSizedFolge = 0;

                if(rest == Reader.INTEGER_COUNT_PER_READ){
                    intBuffer.clear();
                    fillBuffer();
                }

                return DataWrapperImpl.create(folge,folge.length,1,true);
            }else{
                int size = intBuffer.capacity();
                folge = new int[size];
                intBuffer.get(folge);
                System.out.println(reader.getFileName() + ": FolgenReader::multipleReadsPerFolge() Case: rest (" + rest + ") > Reader.INTEGER_COUNT_PER_READ (" + Reader.INTEGER_COUNT_PER_READ + ")");
                loadProgressOverSizedFolge+=size;
                remainigIntegerInBuffer-=size;
                System.out.println(reader.getFileName()+": loadProgress: "+loadProgressOverSizedFolge+" von "+folgenLength+" Zahlen der Folge");
                System.out.println(reader.getFileName()+": returned array laenge: "+folge.length);
                intBuffer.clear();
                fillBuffer();
                return DataWrapperImpl.create(folge,folge.length,1,false);
            }

        }
    }

    private DataWrapper singleReadPerFolge(){


        // solange der reader daten hat lesen
        if(remainigIntegerInBuffer == 0 && reader.hasNextIntArrray()){
            fillBuffer();
        }

        int[] folge = null;
        // wenn die länge der Folge länger ist als die restlichen integerzahlen im buffer
        // und die folgenlänge nicht groeßer als unser buffer,
        // kopiere die restlichen zahlen aus den buffer in den bufferRestArray und hole eine
        // neue ladung integer aus dem reader und fülle die restlichen integer von der geteilten
        // folge in den bufferRestArray ein.
        if(folgenLength > remainigIntegerInBuffer &&  folgenLength < intBuffer.capacity()){
            System.out.println(reader.getFileName()+": FolgenReader::singleReadPerFolge() Case: folgenLength ("+folgenLength+") > remainigIntegerInBuffer ("+remainigIntegerInBuffer+") &&  folgenLength ("+folgenLength+") < intBuffer.capacity ("+intBuffer.capacity()+")");
            int[] bufferRestArray = new int[folgenLength];
            int lengthRestTilBufferEnd = intBuffer.capacity()- folgenCursorPosition;
            intBuffer.get(bufferRestArray,0,lengthRestTilBufferEnd);
            // buffer leeren damit die GC nur noch das objekt weg räumen muss
            intBuffer.clear();

            // füllt den buffer mit den nächsten zahlen
            fillBuffer();
            // die länger der folge minus die anzahl der sich bereits befindenen zahlen
            // im bufferRestArray
            int lengthRestFolgeLength = folgenLength-lengthRestTilBufferEnd;
            int[] tmp_array;
            if(intBuffer.capacity()>0){
                // die restlichen zahlen einfüllen, der IntBuffer verwaltet einen
                // eigenen cursor ~~
                for (int i = lengthRestTilBufferEnd; i < folgenLength; i++) {
                    bufferRestArray[i] = intBuffer.get();
                }
            }else if(intBuffer.capacity()==0){
                tmp_array = new int[lengthRestTilBufferEnd];
                System.arraycopy(bufferRestArray,0,tmp_array,0,lengthRestTilBufferEnd);
                bufferRestArray = tmp_array;
            }
            moveFolgenCursor(lengthRestFolgeLength);
            folge = bufferRestArray;
        }else if(folgenLength<=remainigIntegerInBuffer){
            System.out.println(reader.getFileName()+": FolgenReader::singleReadPerFolge() Case: folgenLength ("+folgenLength+") <= remainigIntegerInBuffer ("+remainigIntegerInBuffer+")");

            folge = new int[folgenLength];
            intBuffer.get(folge,0,folgenLength);
            moveFolgenCursor(folgenLength);


        }else {
            System.out.println(reader.getFileName()+": FolgenReader::singleReadPerFolge() Case: until now unknown !!");
            System.out.println(reader.getFileName()+": FolgenReader::singleReadPerFolge() folgenLength = "+folgenLength+", remainingIntegerInBuffer = "+remainigIntegerInBuffer+" , folgenCursorPosition = "+folgenCursorPosition+" , intBuffer.capacity = "+intBuffer.capacity());
        }
        return DataWrapperImpl.create(folge,folge.length,1,true);
    }
    public DataWrapper getFolge(){
        DataWrapper tmp = null;
        if(folgenLength > Reader.INTEGER_COUNT_PER_READ){
            //oversize folgen fetch mode
           tmp =  multipleReadsPerFolge();
        }else{
            //normal folgen fetch mode
            tmp = singleReadPerFolge();
        }
        return  tmp;
    }

//    @Deprecated
//    public DataWrapper getBufferRest(){
//        int[] folge;
//        DataWrapper tmp = null;
//        if(folgenLength > Reader.INTEGER_COUNT_PER_READ){
//            long length = loadProgressOverSizedFolge+remainigIntegerInBuffer;
//            folge = new int[remainigIntegerInBuffer];
//            intBuffer.get(folge);
//            // ist es nicht egal ob am ende der datei die folge komplett ist oder nicht ??
//            // den es wird eh nicht mehr auf das andere band gewechselt
//            if(length == folgenLength){
//                tmp = DataWrapperImpl.create(folge,folge.length,1,true);
//            }else{
//                tmp = DataWrapperImpl.create(folge,folge.length,1,false);
//            }
//
//        }else{
//
//            if(remainigIntegerInBuffer == folgenLength){
//                folge = new int[folgenLength];
//                intBuffer.get(folge);
//                tmp = DataWrapperImpl.create(folge,folge.length,1,true);
//            }else if(remainigIntegerInBuffer<folgenLength){
//                folge = new int[remainigIntegerInBuffer];
//                intBuffer.get(folge);
//                tmp = DataWrapperImpl.create(folge,folge.length,1,false);
//            }else {
//                System.out.println(reader.getFileName()+": getBufferRest() more then one folgen in rest");
//            }
//        }
//        return tmp;
//    }

    public boolean HasNextFolge(){
        // fehlt das (previousBufferCapacity > intBuffer.capacity()) kommt es dazu das die
        // letzte folge nicht beendet wird

        return intBuffer.capacity()!=0; //reader.hasNextIntArrray();// || (previousBufferCapacity> intBuffer.capacity());//|| loadProgressOverSizedFolge !=0;
    }
    private void moveFolgenCursor(int length){
        folgenCursorPosition+=length;
        remainigIntegerInBuffer-=length;
    }

    public void setRunLevel(int runLevel){
        folgenLength = (int)(Math.pow(2,runLevel)*INITAL_FOLGEN_LENGTH);
    }
    public static void main(String[] args) {
        FolgenReader folgenReader = FolgenReader.create("fread1","sortiert.file");
        int[] array;
        int x = 0;
        DataWrapper tmp;
        while(folgenReader.HasNextFolge()){
            tmp = folgenReader.getFolge();
            array = tmp.getData();
//            for (int i = 0; i < array.length; i++) {
                System.out.println(array[array.length-1] +"array length:"+array.length);
//            }
        }
    }


}
