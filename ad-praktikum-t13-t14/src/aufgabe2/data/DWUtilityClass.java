package aufgabe2.data;

import aufgabe2.interfaces.DataWrapper;

public class DWUtilityClass {
	
	
	public  static DataWrapper createNewDataWrapper(int[] data, int size)  {
		return DataWrapperImpl.create(data, size); 
	}
	
}
