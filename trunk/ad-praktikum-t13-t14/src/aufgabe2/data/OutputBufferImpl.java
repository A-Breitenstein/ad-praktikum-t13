package aufgabe2.data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import aufgabe2.interfaces.OutputBuffer;

public class OutputBufferImpl implements OutputBuffer {

	DataManagerImpl owner;
	private IOScheduler scheduler;
	String filePath;
	Writer writer;
	ByteBuffer currentByteBuffer;
	IntBuffer currentIntBuffer;
	
	public OutputBufferImpl(String filePath, DataManagerImpl owner, IOScheduler scheduler){
		this.owner = owner;
		this.scheduler= scheduler;
		this.filePath = filePath;
		this.writer = Writer.create(filePath);
		allocateNewBuffer();
		//job writerJob = new WriterJob(Writer, intbuffer)

	}
	
	public void close() throws IOException{
		writer.close();
		if(currentIntBuffer.position() != 0){
			pushWriterJob();//Den Rest schreiben
		}
		currentByteBuffer = null;
		currentIntBuffer = null;
	}
	
	@Override
	public void push(int val) {
		currentIntBuffer.put(val);
		if (!currentIntBuffer.hasRemaining()) {
			pushWriterJob();
			allocateNewBuffer();
		}
	}

	@Override
	public void finishBlock() {
		owner.finishBlock();		
	}
	
	public String getFilePath(){
		return filePath;
	}

	/**
	 * Gibt den Auftrag, den Currentbuffer wegzuschreiben
	 */
	private void pushWriterJob(){
		currentByteBuffer.flip();
		scheduler.pushJob(new WriterJob(writer, currentByteBuffer));
		currentByteBuffer = null;
		currentIntBuffer = null;
	}
	/**
	 * Erzeugt einen neuen CurrentBuffer
	 */
	private void allocateNewBuffer(){
		currentByteBuffer = ByteBuffer.allocate((int)Constants.BUFFERSIZE_SORTWRITE);
		currentIntBuffer = currentByteBuffer.asIntBuffer();
	}
}
