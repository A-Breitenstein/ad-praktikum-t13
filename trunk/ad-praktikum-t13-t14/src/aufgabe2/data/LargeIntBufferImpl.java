package aufgabe2.data;

import aufgabe2.algorithm.parallel.QuickSortMultiThreaded;
import aufgabe2.algorithm.parallel.QuickSortMultiThreadedLargeBuffer;
import aufgabe2.data.io.Reader;
import aufgabe2.data.io.Writer;
import aufgabe2.interfaces.LargeIntBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: Sven
 * Date: 08.12.12
 * Time: 17:32
 */
public class LargeIntBufferImpl implements LargeIntBuffer{

    private long position;
    private long limit;
    private long capacityInBytes;
    private ByteBuffer[] byteBuffers;
    private IntBuffer[] intBuffers;
    private final int BUFFER_SIZE_IN_MB;
    private static final int _1024 = 1024;
    private final int INTERNAL_BUFFER_SIZE_IN_BYTES;
    private final long INTEGER_COUNT;
    private final long INTEGER_COUNT_PER_BUFFER;
    private final long BUFFER_COUNT;
    private final long RESTBUFFER_SIZE;
    private final int INTEGER_SIZE = Integer.SIZE / Byte.SIZE;
    private int tmp_IntBufferIndex = 0;
    private int tmp_Index = 0;


    public LargeIntBufferImpl(long integerCount,int sizePerBufferInMB) {
        BUFFER_SIZE_IN_MB = sizePerBufferInMB;
        INTERNAL_BUFFER_SIZE_IN_BYTES = BUFFER_SIZE_IN_MB * _1024 * _1024;
        INTEGER_COUNT = integerCount;
        INTEGER_COUNT_PER_BUFFER = INTERNAL_BUFFER_SIZE_IN_BYTES / INTEGER_SIZE;
        capacityInBytes = INTEGER_COUNT * INTEGER_SIZE;
        RESTBUFFER_SIZE = capacityInBytes % INTERNAL_BUFFER_SIZE_IN_BYTES;
        if (RESTBUFFER_SIZE == 0) {
            BUFFER_COUNT = capacityInBytes / INTERNAL_BUFFER_SIZE_IN_BYTES;
        } else {
            BUFFER_COUNT =  (capacityInBytes / INTERNAL_BUFFER_SIZE_IN_BYTES)+1;
        }

        limit = capacityInBytes;
        position = 0;

        initBuffers();

    }

    private void initBuffers(){
        byteBuffers = new ByteBuffer[(int) BUFFER_COUNT];
        intBuffers = new IntBuffer[(int) BUFFER_COUNT];

        for (int i = 0; i < byteBuffers.length - 1; i++) {
            byteBuffers[i] = ByteBuffer.allocateDirect(INTERNAL_BUFFER_SIZE_IN_BYTES);
            intBuffers[i] = byteBuffers[i].asIntBuffer();
        }
        if (RESTBUFFER_SIZE == 0) {
            byteBuffers[byteBuffers.length - 1] = ByteBuffer.allocateDirect(INTERNAL_BUFFER_SIZE_IN_BYTES);
            intBuffers[byteBuffers.length - 1] = byteBuffers[byteBuffers.length - 1].asIntBuffer();
        } else {
            byteBuffers[byteBuffers.length - 1] = ByteBuffer.allocateDirect((int) RESTBUFFER_SIZE);
            intBuffers[byteBuffers.length - 1] = byteBuffers[byteBuffers.length - 1].asIntBuffer();

        }

    }
    public static LargeIntBuffer allocateDirect(long integerCount,int sizePerBufferInMB) {
        return new LargeIntBufferImpl(integerCount, sizePerBufferInMB);
    }


    @Override
    public void put(long index, int val) {
        tmp_IntBufferIndex = (int) (index / INTEGER_COUNT_PER_BUFFER);
        tmp_Index = (int)(index - (((long)tmp_IntBufferIndex)*INTEGER_COUNT_PER_BUFFER));
//        tmp_Index = (int) (index % INTEGER_COUNT_PER_BUFFER);
        intBuffers[tmp_IntBufferIndex].put(tmp_Index,val);
    }

    @Override
    public int get(long index) {
        tmp_IntBufferIndex = (int) (index / INTEGER_COUNT_PER_BUFFER);
        tmp_Index = (int)(index - (((long)tmp_IntBufferIndex)*INTEGER_COUNT_PER_BUFFER));
//        tmp_Index = (int) (index % INTEGER_COUNT_PER_BUFFER);
        return intBuffers[tmp_IntBufferIndex].get(tmp_Index);
    }

    /**
     *
     * @return - Position in byte context
     */
    @Override
    public long position() {
        return position;
    }

    @Override
    public void rewind() {
        position = 0;
    }

    @Override
    public void limit(long newLimit) {
        if ((newLimit > capacityInBytes) || (newLimit < 0))
            throw new IllegalArgumentException();
        limit = newLimit;
        if (position > limit) position = limit;
    }

    @Override
    public long limit() {
        return limit;
    }

    @Override
    public long limitIntBuffer() {
        return limit / INTEGER_SIZE;
    }

    @Override
    public void flip() {
        limit = position;
        position = 0;
    }

    @Override
    public void clear() {
        position = 0;
        limit = capacityInBytes;
    }

    @Override
    public void wirteBuffer(Writer writer) {
        for (ByteBuffer byteBuffer : byteBuffers) {
            position+=byteBuffer.limit();
            if(limit >= position)
                writer.writeByteBufferToFile(byteBuffer);
        }
    }

    @Override
    public void readBuffer(Reader reader) {
        try {
            clear();
            int i = 0;
            for (ByteBuffer byteBuffer : byteBuffers) {
                reader.readToByteBuffer(byteBuffer);
                position+=byteBuffer.limit();
                intBuffers[i] = byteBuffer.asIntBuffer();
                if(byteBuffer.capacity() != byteBuffer.limit())
                    break;
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        flip();
    }


    public static void main(String[] args) {
//        test_put_int_and_get_int();
//        test_read_file_test();
//        test_writeBuffer();
//        test_destinationFileIsEqualToSourceFile();
//        test_writeFolge();
//        test_sort();
        test_quicksortMultiThreadedNormalBuffer();
    }


 //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ TEST @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ TEST @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ TEST @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ TEST @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ TEST @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    private static void test_put_int_and_get_int(){
        LargeIntBuffer ib = LargeIntBufferImpl.allocateDirect(5660000,1);
        ib.put(0,1337);
        ib.put(3444550,1337);
        ib.put(5599999,1337);
//        ib.put(5660000,1337); muss IndexOutOffBoundsException werfen

        int index_0 =  ib.get(0);
        int index_3444550 = ib.get(3444550);
        int index_5599999 = ib.get(5599999);
//        int index_5660000 = ib.get(5660000);

        System.out.println("fertig");

    }

    private static void test_read_file_test(){
        LargeIntBuffer ib = LargeIntBufferImpl.allocateDirect(5660000,1);

        Reader reader = Reader.create("testSort",1*_1024*_1024);

        long controll_counter = 0;
        while (!reader.isFileFullyReaded()) {
            ib.readBuffer(reader);
            for (long i = 0; i < ib.limitIntBuffer(); i++) {
                controll_counter++;
            }
            System.out.println("controll_counter = "+ controll_counter);
            System.out.println("limitIntBuffer = "+ ib.limitIntBuffer());

        }
        System.out.println("integer in file = "+reader.getFileChanSize()/4);


    }

    private static void test_writeBuffer(){
        LargeIntBuffer ib = LargeIntBufferImpl.allocateDirect(5660000, 1);
        Writer writer = Writer.create("testSortCopy");
        Reader reader = Reader.create("testSort",1*_1024*_1024);

        long controll_counter = 0;
        while (!reader.isFileFullyReaded()) {
            ib.readBuffer(reader);
            for (long i = 0; i < ib.limitIntBuffer(); i++) {
                controll_counter++;
            }
            System.out.println("controll_counter = "+ controll_counter);
            ib.wirteBuffer(writer);

        }
        System.out.println("integer in reader file = "+reader.getFileChanSize()/4);
        System.out.println("integer in writer file = "+writer.getFileChanSize()/4);
    }
    private static void test_destinationFileIsEqualToSourceFile(){

        LargeIntBuffer destinationBuffer = LargeIntBufferImpl.allocateDirect(5660000, 1);
        ByteBuffer bb = ByteBuffer.allocateDirect(5660000*4);
//        ByteBuffer bb2 = ByteBuffer.allocateDirect(5660000*4);
        IntBuffer sourceBuffer;
//        IntBuffer destinationBuffer;
        Reader sourceReader = Reader.create("testSort",1*_1024*_1024);
        Reader destinationReader = Reader.create("testSort2",1 * _1024 * _1024);
        // testSortCopy created with test_writeBuffer
        long controll_counter = 0;
        while (!sourceReader.isFileFullyReaded()){
            try {
                sourceReader.readToByteBuffer(bb);
                sourceBuffer = bb.asIntBuffer();
                destinationBuffer.readBuffer(destinationReader);
//                destinationReader.readToByteBuffer(bb2);
//                destinationBuffer = bb2.asIntBuffer();

                for (int i = 0; i < sourceBuffer.limit(); i++) {
                    controll_counter++;
                    if(sourceBuffer.get(i) != destinationBuffer.get(i))
                        System.out.println("sourceBuffer.get("+i+") "+sourceBuffer.get(i)+" != destinationBuffer.get("+i+") " +destinationBuffer.get(i));

                }
                System.out.println("controll_counter = "+ controll_counter);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }

    }
    private static void test_writeFolge(){
        LargeIntBuffer ib = LargeIntBufferImpl.allocateDirect(5660000, 1);
        Writer writer = Writer.create("testSortCopy");
        for (int i = 0; i < 100; i++) {
            ib.put(i,i);
            System.out.println(ib.get(i));
        }
        ib.wirteBuffer(writer);
    }

    private static void test_sort(){
        LargeIntBuffer ib = LargeIntBufferImpl.allocateDirect(5500000, 4);
        Reader reader = Reader.create("testSort",5500000*4);
        Writer writer = Writer.create("hierrein");
        ExecutorService threadPool = Executors.newCachedThreadPool();
//        while(!reader.isFileFullyReaded()){
            ib.readBuffer(reader);
            double start = System.currentTimeMillis();
//            QuickSortMultiThreadedLargeBuffer.sort(ib, 0, ib.limitIntBuffer() - 1, threadPool);
            QuickSortMultiThreadedLargeBuffer.blockSort_quick_singleThreaded(ib,0,ib.limitIntBuffer() - 1);
        System.out.println("ElapsedTime : "+(System.currentTimeMillis()-start));
            ib.wirteBuffer(writer);
//        }
        System.out.println(threadPool);
        TestFileGenerator.isSorted("hierrein");
    }
    private static void test_quicksortMultiThreadedNormalBuffer(){
        ByteBuffer bb = ByteBuffer.allocateDirect(5500000*4);
        Reader reader = Reader.create("testSort",5500000*4);
        Writer writer = Writer.create("hierrein");
        ExecutorService threadPool = Executors.newCachedThreadPool();
        IntBuffer ib;
        try {
            reader.readToByteBuffer(bb);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        ib = bb.asIntBuffer();

        double start = System.currentTimeMillis();
//        QuickSortMultiThreaded.sort(ib, 0, ib.limit() - 1, threadPool);
        QuickSortMultiThreaded.blockSort_quick_singleThreaded(ib, 0, ib.limit() - 1);
        System.out.println("ElapsedTime : "+(System.currentTimeMillis()-start));
        System.out.println(ib.limit());
        System.out.println(threadPool);
        writer.writeByteBufferToFile(bb);

        TestFileGenerator.isSorted("hierrein");
    }
}
