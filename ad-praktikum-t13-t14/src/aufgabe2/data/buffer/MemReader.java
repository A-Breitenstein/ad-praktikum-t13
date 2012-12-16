package aufgabe2.data.buffer;

import java.nio.*;
import aufgabe2.data.jobs.IReaderJob;

public class MemReader implements IReaderJob{

	IntBuffer returnBuffer;
	
	public MemReader(MemPersistence dataSource, String fileID, long startPos, IBufferManager bufferManager, String spareBufferKey){
		ByteBuffer bBuffer = dataSource.popPage(fileID, startPos, bufferManager.getBBuffer(spareBufferKey));
		bufferManager.exchangeBBuffer(spareBufferKey, bBuffer); // Der freie spareBuffer wurde der MemPersistence als Austauschobjekt gegeben und "gehört" nun nicht mehr dem BufferManager. Dafür wurde ein Buffer vom MemPersistence gegegben, welcher nun dem BufferManager "gehört". Natürlich ist dieser gerade mit relevanten Daten gefüllt, doch das weiß der IntputBuffer.
		returnBuffer = bBuffer.asIntBuffer();
	}
	
	@Override
	public IntBuffer getIntBuffer() {
		return returnBuffer;
	}

	
	
}
