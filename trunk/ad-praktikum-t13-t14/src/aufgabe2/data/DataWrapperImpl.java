package aufgabe2.data;

import java.lang.reflect.Array;
import java.util.Arrays;

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
    private boolean folgeKomplett;

    DataWrapperImpl(int[] data, int size, boolean folgeKomplett) {
        this.data = data;
        this.size = size;
        this.folgeKomplett = folgeKomplett;
    }

    public static DataWrapper create(int[] data, int size, boolean folgeKomplett) {
        return new DataWrapperImpl(data, size, folgeKomplett);
    }

    public static DataWrapper create(int[] data, int size) {
        return new DataWrapperImpl( data,  size,true);
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

    public void setFolgeKomplett(boolean folgeKomplett) {
        this.folgeKomplett = folgeKomplett;
    }

    @Override
    public boolean isFolgeKomplett() {
        return folgeKomplett;
    }
    
    @Override
    public boolean equals(Object o){
    	if (o == this) return true;
    	if (!(o instanceof DataWrapper)) return false;
    	return Arrays.equals(this.getData(), ((DataWrapper) o).getData()) && this.getSize() == ((DataWrapper) o).getSize(); 	
    }
}
