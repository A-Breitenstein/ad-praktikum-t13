package aufgabe2.data;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Sven
 * Date: 31.10.12
 * Time: 21:37
 * To change this template use File | Settings | File Templates.
 */
public class TestFileGenerator {

    public static boolean isSorted(String fileName){
        FileChannel fc = null;
        IntBuffer ib = null;
       // buffer size needs to be divied by 4
        int byteBufferSize =512;
        long cursorPosition = 0;
        int[] intArray;
        long CONTROLL_COUNTER = 0;
        long integerCount = byteBufferSize/4;

        try {
            fc = new FileInputStream(new File(fileName)).getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            fc = new FileInputStream(new File(fileName)).getChannel();
            // ausrechnen wie viele iterationen mit der byteBufferSize möglich sind
            int iterations = (int) (fc.size()/byteBufferSize);
            for (int i = 0; i < iterations; i++) {

                intArray = getIntArray(fc,cursorPosition,byteBufferSize);
                for (int x = 1; x < integerCount; x++) {
//                    System.out.println(intArray[x]);
                    CONTROLL_COUNTER++;
                    //aufsteigend sortieret
                    if(intArray[x-1]>intArray[x]){
                        System.out.println(intArray[x-1] +" ist großer als "+intArray[x]);
                        System.out.println(fileName+ " nicht richtig sortiert!");
                        fc.close();
                        return false;
                    }
                }
                cursorPosition+=byteBufferSize;
            }

            // da iterations abgerundet wird fehlt am ende noch ein kleiner rest, der wird
            // hier als byteBufferSize gesetzt und damit wird nun ein letztes mal
            // der filechannel gelesen.
            byteBufferSize = (int) (fc.size()-(iterations*byteBufferSize));
            intArray = getIntArray(fc,cursorPosition,byteBufferSize);
            integerCount = byteBufferSize/4;
            for (int x = 1; x < integerCount ; x++) {
//                System.out.println(intArray[x]);
                CONTROLL_COUNTER++;
                if(intArray[x-1]>intArray[x]){
                    System.out.println(intArray[x-1] +" ist großer als "+intArray[x]);
                    System.out.println(fileName+ " nicht richtig sortiert!");
                    fc.close();
                   return false;
                }
            }
            cursorPosition+=byteBufferSize;
            System.out.println(fileName+ " is sorted! congratulations ");
            System.out.println("fileChannel Size: "+fc.size()+" b");
            System.out.println("Anzahl der betrachteten Integer Zahlen: "+(CONTROLL_COUNTER+iterations));
            System.out.println("Cursor_position: "+cursorPosition);



            fc.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return true;
    }
    private static int[] getIntArray(FileChannel fc,long cursorStart,long sizeInByte) throws IOException{
        IntBuffer ib = fc.map(FileChannel.MapMode.READ_ONLY,cursorStart,sizeInByte).asIntBuffer();
        int [] array = new int[(int) (sizeInByte/4)];
        ib.get(array);
        return array;
    }

    static void writeIntArray(FileChannel fc,int[] array) {
        try {
            ByteBuffer byteBuff = ByteBuffer.allocate((Integer.SIZE / Byte.SIZE) * array.length);
            IntBuffer intBuff = byteBuff.asIntBuffer();
            intBuff.put(array);
            intBuff.flip();
            fc.write(byteBuff);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void createTestFile(String fileName,int arraySize,int iterations){

        try{
            FileChannel fc = new FileOutputStream(fileName).getChannel();
            for (int i = 0; i < iterations ; i++) {

                writeIntArray(fc,initRandomArray(arraySize,1000000000,-1000000000));
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void createTestFile(String fileName,int[] givenIntArray){
        try{
            FileChannel fc = new FileOutputStream(fileName).getChannel();
                writeIntArray(fc,givenIntArray);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static int[] initRandomArray(int arraySize, int upperBound, int lowerBound) {
        int array[] = new int[arraySize];
        Random random = new Random();

        upperBound += (1 + Math.abs(lowerBound));

        for(int i = 0; i < array.length; i++){
            array[i] = random.nextInt(upperBound)+lowerBound;
        }

        return array;
    }

    public static void createSortedTestFile(String fileName,int size){
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = i;
        }
        createTestFile(fileName,arr);
    }
}
