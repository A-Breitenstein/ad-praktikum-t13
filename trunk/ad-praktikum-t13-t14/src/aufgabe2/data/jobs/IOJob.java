package aufgabe2.data.jobs;

import java.util.concurrent.Semaphore;

/**
 * Ermöglicht das Ausführen eines Lese/Schreibjobs über einen Batchartigen Scheduler. Dieser
 * stellt sicher, dass dem Job stets genügend Abeitsspeicher zur Verfügung steht.
 * @author Markus Bruhn
 *
 */
public interface IOJob {//extends Runnable{

        /**
         * Gibt die Identifikation (z.B. Pfad) der Datei zurück, auf welcher dieser IO-Job arbeitet
         * @return
         */
        public String getWorkingFile();
        
        /**
         * Gibt dem Job das Schlüsselobjekt für den Speicher für die Datei. Der Schlüssel muss 
         * in jenem Zeitraum gehalten werden, wie die Klasse Speicher verbraucht. Erst wenn der
         * Job sämmtlichen Speicher (in dieser Klasse) freigibt, darf und muss der Schlüssel
         * zurückgegeben werden
         * @param lock
         */
        public void setMemoryLock(Semaphore lock);
        
        /**
         * Bereitet sich auf die Aufgabe vor, spätestens zu diesem Zeitpunkt muss der Schlüssel
         * gehalten werden. Ist dieses noch nicht möglich, so wird false zurückgegeben und die 
         * Aufgabe kann noch nicht ausgeführt werden.
         * @return
         */
        public boolean prepareRun();
        
        /**
         * führt die I/O-Aufgabe (synchron) aus. Der Schlüssel muss nach Beendigung der Aufgabe noch
         * nicht zurückgegeben worden sein.
         */
         public void run();
        
}