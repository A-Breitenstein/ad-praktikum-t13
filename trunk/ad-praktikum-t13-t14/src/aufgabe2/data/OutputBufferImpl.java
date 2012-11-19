package aufgabe2.data;

import java.io.IOException;

import aufgabe2.interfaces.OutputBuffer;

public class OutputBufferImpl implements OutputBuffer {

	DataManagerImpl owner;
	String filePath;
	
	public OutputBufferImpl(String filePath, DataManagerImpl owner){
		this.owner = owner;
		this.filePath = filePath;
		// TODO Auto-generated method stub
	}
	
	public void close() throws IOException{
		// TODO Auto-generated method stub
	}
	int diagnosticWriteCount = 0;
	@Override
	public void push(int val) {
		diagnosticWriteCount++;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finishBlock() {
		owner.finishBlock();		
	}
	
	public String getFilePath(){
		return filePath;
	}

}
