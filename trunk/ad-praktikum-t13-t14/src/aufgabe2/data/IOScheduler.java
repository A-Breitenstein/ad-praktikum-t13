package aufgabe2.data;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

//import java.lang.NotImplementedException;

/**
 * Diese Klasse arbeitet I/O-Jobs im Hintergrund parallel zur Berechnung ab
 * @author Markus Bruhn
 *
 */
public class IOScheduler extends Thread {

	List<IOJob> jobs = new ArrayList<IOJob>();
	Map<String, ReentrantLock> bufferLocks = new HashMap<String, ReentrantLock>();
	Semaphore jobSemaphore = new Semaphore(0);
	
	public void Run(){
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Fügt einen neuen Job an das Ende der Schlange hinzu.
	 * @param job - der in einem Hintergrundthread zu bearbeitende Job
	 */
	public void pushJob(IOJob job){
		job.setMemoryLock(new ReentrantLock());
		job.prepareRun();
		job.runJob();// Testweise synchron ausführen
		
		/*
		job.setMemoryLock(new ReentrantLock());
		synchronized (jobs){
			
		}*/
		//throw new NotImplementedException();
	}
	
	/**
	 * Wartet so lange, bis alle noch anstehenden Jobs abgearbeitet wurden
	 */
	public void terminate(){
		throw new UnsupportedOperationException();
	}
	
}
