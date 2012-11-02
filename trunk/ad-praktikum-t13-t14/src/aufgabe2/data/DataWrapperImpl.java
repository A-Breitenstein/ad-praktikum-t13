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
    private int folgen;
    private boolean folgeKomplett;

    private DataWrapperImpl(int[] data, int size, int folgen, boolean folgeKomplett) {
        this.data = data;
        this.size = size;
        this.folgen = folgen;
        this.folgeKomplett = folgeKomplett;
    }

    public static DataWrapper create(int[] data, int size, int folgen, boolean folgeKomplett) {
        return new DataWrapperImpl(data, size, folgen, folgeKomplett);
    }

    //Getter & Setter
    @Override
    public int[] getData() {
        return data;
    }

    @Override
    public int getSize() {
        return size;
    }

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

    public void setFolgen(int folgen) {
        this.folgen = folgen;
    }

    public void setFolgeKomplett(boolean folgeKomplett) {
        this.folgeKomplett = folgeKomplett;
    }

    @Override
    public int getFolgeAnzahl() {
        return folgen;
    }

    @Override
    public boolean isFolgeKomplett() {
        return folgeKomplett;
    }
}
