package aufgabe2.data;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

import aufgabe2.data.io.Reader;

/**
 * Created with IntelliJ IDEA.
 * User: Sven
 * Date: 31.10.12
 * Time: 21:37
 * To change this template use File | Settings | File Templates.
 */
public class TestFileGenerator {

	private static void writeIntArray(FileChannel fc,int[] array) {
        try {
            System.gc();
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
        	FileOutputStream fos = new FileOutputStream(fileName);
            FileChannel fc = fos.getChannel();
            for (int i = 0; i < iterations ; i++) {
                writeIntArray(fc,initRandomArray(arraySize,1000000000,-1000000000));
            }
            fc.close();
            fos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void createTestFile(String fileName,int[] givenIntArray){
    	
    	try{
    		FileOutputStream fos = new FileOutputStream(fileName);
            FileChannel fc = fos.getChannel();
            writeIntArray(fc,givenIntArray);
            fc.close();
            fos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static int[] initRandomArray(int arraySize, int upperBound, int lowerBound) {
        System.gc();
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

    public static boolean isSorted(String fileName){
        Reader reader = Reader.create(fileName, Constants.BUFFERSIZE_MERGEREAD);
        long CONTROLL_COUNTER = 0;
        ByteBuffer bbuffer = ByteBuffer.allocate(Constants.BUFFERSIZE_MERGEREAD);
       
        int overhangElem = Integer.MIN_VALUE;
        
        try {
            while(!reader.isFileFullyReaded()){
                reader.readToByteBuffer(bbuffer);
                IntBuffer buffer = bbuffer.asIntBuffer();
                
                if (overhangElem > buffer.get(0)){
                	System.out.println(overhangElem +" ist großer als " + buffer.get(0));
                    System.out.println(fileName+ " nicht richtig sortiert!");
                    System.out.println("controll-counter: "+CONTROLL_COUNTER);
                    reader.close();
                    return false;
                }
                
                for (int i = 0; i < buffer.limit()-1; i++) {
                    CONTROLL_COUNTER++;
                    //aufsteigend sortieret
                    if(buffer.get(i) > buffer.get(i+1)){
                        System.out.println(buffer.get(i) +" ist großer als "+buffer.get(i+1));
                        System.out.println(fileName+ " nicht richtig sortiert!");
                        System.out.println("controll-counter: "+CONTROLL_COUNTER);
                        reader.close();
                        return false;
                    }
                }
                overhangElem = buffer.get(buffer.limit()-1);    
                CONTROLL_COUNTER++;
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(fileName+ " with " + CONTROLL_COUNTER + "Integers is sorted! Congratulations.");
        return true;
    }
}
