package aufgabe2.algorithm;

import aufgabe2.interfaces.DataManager;
import aufgabe2.interfaces.DataWrapper;

class OutputBuffer {
	
	DataManager tapes; 
	DataWrapper output; 
	int pos; 
	
	public OutputBuffer(DataManager data){
		tapes = data;
		createNextOutput();
	}
	
	public void push(int val){
		output.getData()[pos] = val;
		
		if (pos+1 == output.getData().length){
			storeInTape();
			createNextOutput();	
		} else {
			pos++ ;
		}
		
	}
	
	public void storeInTape(){
		output.setSize(pos);
		tapes.write(output);
	}
	
	private void createNextOutput(){
		output = null;
		output = tapes.createOuputChannel();
		pos = 0;
	}

}
