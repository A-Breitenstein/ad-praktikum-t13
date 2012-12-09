package aufgabe2.interfaces;

import aufgabe2.data.io.Reader;
import aufgabe2.data.io.Writer;

import java.nio.channels.FileChannel;

/**
 * Created with IntelliJ IDEA.
 * User: Sven
 * Date: 08.12.12
 * Time: 17:36
 */
public interface LargeIntBuffer {
    void put(long index,int val);
    int get(long index);
    void rewind();
    void limit(long newlimit);
    long limit();
    long limitIntBuffer();
    void flip();
    void clear();
    long position();
    void wirteBuffer(Writer writer);
    void readBuffer(Reader reader);

}
