package aufgabe2.data;

import java.util.*;
import java.util.concurrent.Semaphore;

//import java.lang.NotImplementedException;

/**
 * Diese Klasse arbeitet I/O-Jobs im Hintergrund parallel zur Berechnung ab
 * @author Markus Bruhn
 *
 */
public class IOScheduler extends Thread {

	List<IOJob> jobs = new ArrayList<IOJob>();
	Map<String, Semaphore> bufferLocks = new HashMap<String, Semaphore>();
	Semaphore jobSemaphore = new Semaphore(0);
	
	@Override
	public void run(){
		System.out.println("JobScheduler started.");
		try {
			while(true){
				if (isInterrupted() && jobSemaphore.availablePermits() == 0){
					break;
				} else {					
					jobSemaphore.acquire();
					IOJob job = null;
					synchronized (jobs){
						for (int i = 0; i < jobs.size(); i++){
							if (jobs.get(i).prepareRun()){
								job = jobs.get(i);
								jobs.remove(i);
								break;
							}
						}
					}
					if (job != null){
						job.run();
					} else {
						jobSemaphore.release();//Das vorgesene Element konnte nicht verarbeitet werden --> zurück auf die Liste
						Thread.sleep(50);
						System.out.println("Spin Wait!!!");
					}
				}
					
			}
		} catch (InterruptedException e) {
			
		}
		System.out.println("JobScheduler stopped.");
	}
	
	/**
	 * Fügt einen neuen Job an das Ende der Schlange hinzu.
	 * @param job - der in einem Hintergrundthread zu bearbeitende Job
	 */
	public void pushJob(IOJob job){
		boolean runAsynchron = true;
		
		if (!bufferLocks.containsKey(job.getWorkingFile())){
			bufferLocks.put(job.getWorkingFile(), new Semaphore(1));
		}
		
		job.setMemoryLock(bufferLocks.get(job.getWorkingFile()));
		
		if (runAsynchron) {
			synchronized (jobs){
				jobs.add(job);
			}
			jobSemaphore.release();
		} else {
			job.prepareRun();
			job.run(); // Testweise synchron ausführen
			
		}
			
		
	}
	
	/**
	 * Wartet so lange, bis alle noch anstehenden Jobs abgearbeitet wurden
	 */
	public void terminate(){
		
	}
	
}
