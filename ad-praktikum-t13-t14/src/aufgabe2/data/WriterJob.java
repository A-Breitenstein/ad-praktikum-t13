package aufgabe2.data;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.locks.ReentrantLock;

public class WriterJob implements IOJob {
	
	private Writer zugehoerigesBand; //band auf das geschrieben werden soll. 
	private ByteBuffer zugehoerigeDaten; //daten die auf das band geschrieben werden
	private ReentrantLock jobLock; 
	
	
	public WriterJob(Writer datei, ByteBuffer daten ) {
		zugehoerigeDaten = daten; 
		zugehoerigesBand = datei; 
	}

	@Override
	public String getWorkingFile() {
		return 	zugehoerigesBand.getFileName(); 
	}

	@Override
	public void setMemoryLock(ReentrantLock lock) {
		jobLock = lock; 
		jobLock.lock();
	}

	@Override
	public boolean prepareRun() {
		if(jobLock == null ) return false; // falls der lock doch nicht vorhanden sein sollte. 
 		return true; 
	}

	@Override
	public void runJob() {
		zugehoerigesBand.writeByteBufferToFile(zugehoerigeDaten); 
		zugehoerigeDaten = null; // für neue Daten bereit machen
		jobLock.unlock(); 
		
		
	}
	
	

}
