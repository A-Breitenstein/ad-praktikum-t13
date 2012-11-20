package aufgabe2_old.data;

import aufgabe2_old.interfaces.DataWrapper;

public class DWUtilityClass {
	
	
	public  static DataWrapper createNewDataWrapper(int[] data, int size)  {
		return DataWrapperImpl.create(data, size);
	}
	
}
