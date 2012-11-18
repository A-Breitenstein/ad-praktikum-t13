package aufgabe2.data;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Diese Klasse arbeitet I/O-Jobs im Hintergrund parallel zur Berechnung ab
 * @author Markus Bruhn
 *
 */
public class IOScheduler extends Thread {

	
	public void Run(){
		throw new NotImplementedException();
	}
	
	/**
	 * Fügt einen neuen Job an das Ende der Schlange hinzu.
	 * @param job - der in einem Hintergrundthread zu bearbeitende Job
	 */
	public void pushJob(IOJob job){
		throw new NotImplementedException();
	}
	
	/**
	 * Wartet so lange, bis alle noch anstehenden Jobs abgearbeitet wurden
	 */
	public void terminate(){
		throw new NotImplementedException();
	}
	
}
