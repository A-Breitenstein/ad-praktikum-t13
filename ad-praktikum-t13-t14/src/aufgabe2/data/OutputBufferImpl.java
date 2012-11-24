package aufgabe2.data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import aufgabe2.data.io.Writer;
import aufgabe2.data.jobs.IOScheduler;
import aufgabe2.data.jobs.WriterJob;
import aufgabe2.interfaces.OutputBuffer;

public class OutputBufferImpl implements OutputBuffer {

	private DataManagerImpl owner;
	private IOScheduler scheduler;
	private String filePath;
	private Writer writer;
	private ByteBuffer currentByteBuffer;
    private IntBuffer currentIntBuffer;
    private ByteBuffer buffer1, buffer2;
    private WriterJob backgroundWriter;
    
	public OutputBufferImpl(String filePath, DataManagerImpl owner, IOScheduler scheduler, ByteBuffer buffer1, ByteBuffer buffer2){
		this.owner = owner;
		this.scheduler= scheduler;
		this.filePath = filePath;
		this.buffer1 = buffer1;
		this.buffer2 = buffer2;
		buffer1.clear();
		buffer2.clear();
		this.writer = Writer.create(filePath);
		aquireNewBuffer();
	}
	
	public void close() throws IOException{
		if(currentIntBuffer.position() != 0){
			pushWriterJob();//Den Rest schreiben
		}
		
		if (backgroundWriter != null){
			backgroundWriter.waitForComplete();
			backgroundWriter = null;
		}
		writer.close();	
		currentByteBuffer = null;
		currentIntBuffer = null;
	}
	
	@Override
	public void push(int val) {
		currentIntBuffer.put(val);
		if (!currentIntBuffer.hasRemaining()) {
			pushWriterJob();
			aquireNewBuffer();
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
		currentByteBuffer.position(currentIntBuffer.position()*4);
		currentByteBuffer.flip();
		WriterJob job = new WriterJob(writer, currentByteBuffer);
		scheduler.pushJob(job);
		backgroundWriter = job;//Reihenfolge ist wichtig, ggf später noch einmal checken...
	}
	/**
	 * Erzeugt einen neuen CurrentBuffer
	 */
	private void aquireNewBuffer(){
		currentByteBuffer = (currentByteBuffer == buffer1 ? buffer2 : buffer1);// ByteBuffer.allocate((int)Constants.BUFFERSIZE_SORTWRITE);
		currentIntBuffer = currentByteBuffer.asIntBuffer();
	}

}
