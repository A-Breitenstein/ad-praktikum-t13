package aufgabe2.data;

import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

public class WriterJob implements IOJob {
        
        private Writer zugehoerigesBand; //band auf das geschrieben werden soll. 
        private ByteBuffer zugehoerigeDaten; //daten die auf das band geschrieben werden
        private Semaphore jobLock; 
        private Semaphore jobFinished = new Semaphore(0);
        
        public WriterJob(Writer datei, ByteBuffer daten ) {
                zugehoerigeDaten = daten; 
                zugehoerigesBand = datei; 
        }

        @Override
        public String getWorkingFile() {
                return  zugehoerigesBand.getFileName(); 
        }
        public void waitForComplete(){
        	try {
				jobFinished.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        @Override
        public void setMemoryLock(Semaphore lock) {
                jobLock = lock; 
                try {
					jobLock.acquire();
				} catch (InterruptedException e) {}
        }

        @Override
        public boolean prepareRun() {
                if(jobLock == null ) return false; // falls der lock doch nicht vorhanden sein sollte. 
                return true; 
        }

        @Override
        public void run() {
                zugehoerigesBand.writeByteBufferToFile(zugehoerigeDaten); 
                zugehoerigeDaten = null; // für neue Daten bereit machen
                jobLock.release(); 
                jobFinished.release();
                
        }
        
        

}
