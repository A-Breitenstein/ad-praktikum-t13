package aufgabe2.data;

/**
 * Created with IntelliJ IDEA.
 * User: abg667
 * Date: 30.10.12
 * Time: 11:50
 */
public class DataWrapper {

    private int[] data;
    private int size;

    private DataWrapper(int[] data, int size) {
        this.data = data;
        this.size = size;
    }

    public static DataWrapper create(int[] data, int size){
        return new DataWrapper(data,size);
    }

    //    Getter
    public int[] getData() {
        return data;
    }

    public int getSize() {
        return size;
    }

//    Setter
    public void setData(int[] data) {
        this.data = data;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
