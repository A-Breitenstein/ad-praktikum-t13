package aufgabe2.data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import aufgabe2.data.buffer.MemPersistence;
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
    private String bufferKey1, bufferKey2, currentByteBufferKey;
    private WriterJob backgroundWriter;
    private MemPersistence persistenceBuffer;
	private float remainingPushesForMemWrite ;
	private float memPushFrequenz;
		
	public OutputBufferImpl(String filePath, DataManagerImpl owner, IOScheduler scheduler, String bufferKey1, String bufferKey2, boolean lastRun){
		this.owner = owner;
		this.scheduler= scheduler;
		this.filePath = filePath;
		this.persistenceBuffer = owner.getMemPersistence();
		this.bufferKey1 = bufferKey1;
		this.bufferKey2 = bufferKey2;
		owner.getBBuffer(bufferKey1).clear();
		owner.getBBuffer(bufferKey2).clear();
		this.writer = Writer.create(filePath);
		aquireNewBuffer();
		
		int pushCount = (int)Math.ceil((owner.integersToSort * 4 / (lastRun ? 1 : 2))  / (double)Constants.BUFFERSIZE_MERGEPAGE);
		if (persistenceBuffer.getPageCount() > 1 && ! lastRun){
			memPushFrequenz = pushCount / (persistenceBuffer.getPageCount() / 2F); //Mem-Pushes gleichmäßig verteilen: Anzahl der Pushvorgänge / Anzahl der maximal möglichen Pushes beim Schreiben
			remainingPushesForMemWrite = 1; //Wird beim ersten Push auf null runtergezählt, sodass der erste Write-Job im MemPersistence gepspeichert wird
		} else {
			remainingPushesForMemWrite = pushCount + 1; 
		}
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
	private long writerPos = 0; 

	/**
	 * Gibt den Auftrag, den Currentbuffer wegzuschreiben
	 * PostCondition: der Buffer des Nicht-CurrentByteBufferKey ist frei und kann wieder verwendet werden. Der CurrentByteBufferKey darf geändert werden!
	 */
	private void pushWriterJob(){
		currentByteBuffer.position(currentIntBuffer.position()*4);
		currentByteBuffer.flip();
		remainingPushesForMemWrite -=1;
		boolean shouldWriteInMemPersistence = remainingPushesForMemWrite <= 0 && (scheduler.actQueueCount() > 0 || persistenceBuffer.getFreePages() <= 2) ;//möglichst gleichverteilt schreiben, doch wenn der Scheduler so gut wie nix zu tun hat ihm eine Aufgabe geben, wenn möglich (MemPersistence wird um einen Push verschoben)
		if (shouldWriteInMemPersistence && persistenceBuffer.getFreePages() >  0){
			remainingPushesForMemWrite += memPushFrequenz;
			ByteBuffer newFreeBuffer = persistenceBuffer.pushPage(getFilePath(), writerPos , currentByteBuffer);
			newFreeBuffer.clear();
			owner.exchangeBBuffer(currentByteBufferKey, newFreeBuffer);
			currentByteBufferKey = (currentByteBufferKey == bufferKey1 ? bufferKey2 : bufferKey1); //Sehr fieser Fehler: CurrentBuffer muss getauscht werden. Normalerweise wird davon ausgegangen, dass der Nicht-CurrentBuffer nun wieder frei ist, da bei pushJob so lange gewartet wird, bis der alte Job abgearbeitet ist. Hier wird an dem JobScheduler "vorbei" programmiert; d.h, der Job läuft evtl. noch und der Speicher ist noch nicht frei. Wird in diesem Buffer nun neue werte geschrieben, landet Murks auf der Platte. Aber der Speicher für den aktuellen Writejob ist schon frei und soll bei AquireBuffer verwendet werden. 
		} else {
			if(shouldWriteInMemPersistence)
				System.out.println("Zwangsschreiben in Datei, da MemBuffer voll ist");
			WriterJob job = new WriterJob(writer, currentByteBuffer);
			scheduler.pushJob(job);
			backgroundWriter = job;//Reihenfolge ist wichtig, ggf später noch einmal checken...
		}
		writerPos += currentByteBuffer.limit();
		
	}
		
	/**
	 * Erzeugt einen neuen CurrentBuffer
	 */
	private void aquireNewBuffer(){
		currentByteBufferKey = (currentByteBufferKey == bufferKey1 ? bufferKey2 : bufferKey1);
		currentByteBuffer = owner.getBBuffer(currentByteBufferKey);
;		currentIntBuffer = currentByteBuffer.asIntBuffer();
	}

}
