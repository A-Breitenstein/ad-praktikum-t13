package aufgabe2.data.jobs;

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
		//System.out.println("JobScheduler started.");
		try {
			while(true){
				if (isInterrupted() && jobSemaphore.availablePermits() == 0){
					break;
				} else {					
					if (jobSemaphore.availablePermits() == 0)
						System.out.println("Kein IO Auftrag!");
					jobSemaphore.acquire();
					System.out.println("Anzahl Jobs: " + jobs.size());
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
		//System.out.println("JobScheduler stopped.");
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
	 * Gibt die Anzahl der Jobs zurück, die zur Zeit in der Warteschlange stehen. Darüber hinaus kann es noch einen Job geben, der momentan bearbeitet wird.
	 * @return
	 */
	public int actQueueCount(){
		return jobs.size();
	}
	
}
