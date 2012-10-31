package aufgabe2.data;

import aufgabe2.interfaces.DataWrapper;

/**
 * Created with IntelliJ IDEA.
 * User: abg667
 * Date: 30.10.12
 * Time: 11:50
 */
final class DataWrapperImpl implements DataWrapper {

    private int[] data;
    private int size;

    private DataWrapperImpl(int[] data, int size) {
        this.data = data;
        this.size = size;
    }

    public static DataWrapper create(int[] data, int size) {
        return new DataWrapperImpl(data, size);
    }

    //    Getter
    @Override
    public int[] getData() {
        return data;
    }

    @Override
    public int getSize() {
        return size;
    }

    //    Setter
    @Deprecated
    @Override
    public void setData(int[] data) {
        this.data = data;
    }

    @Deprecated
    @Override
    public void setSize(int size) {
        this.size = size;
    }
}
