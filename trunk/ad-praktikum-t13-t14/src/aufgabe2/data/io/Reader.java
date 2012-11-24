package aufgabe2.data.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import aufgabe2.data.Constants;

/**
 * Created with IntelliJ IDEA.
 * User: Sven
 * Date: 03.11.12
 * Time: 16:33
 */
public class Reader {
	
	private String filePath; //Der Pfad der zu lesenden Datei
	private FileInputStream fIS; //Der Input-Stream 
	private FileChannel fileChan; //Das Hilfsobjekt zum Lesen aus der Datei
    private long currentCursorPosition; //Die Leseposition

    //Konstanten
    private final long fileChanSize; //Die Größe der Datei   
    private final int bytesPerRead; //Die anzahl der Elemente, die pro readToByteBuffer in den Buffer gelesen werden sollen
    //private final int internalMaxReadSize; //Noch nicht benutzt: Die maximale Anzahl der auf einmal eingelesenen Bytes für fileChan.read. Ist bBufferSize größer als dieser Wert, so wird ggf. mehrmals hintereinander gelesen, bis readToByteBuffer den kompletten Wert zurückliefert. 
        
    /**
     * Public Factory-Method
     * @param fileName
     * @param bufferSize
     * @return
     */
    public static Reader create(String fileName, int bufferSize){
        try {
			return new Reader(fileName, bufferSize);
		} catch (IOException e) {
			return null;
		}
    }

    /**
     * Konstructor
     * @param filePath
     * @param bBufferSize
     * @throws IOException 
     */
    private Reader(String filePath, int bBufferSize) throws IOException{
        if (bBufferSize % Constants.INTSIZE != 0)
        	throw new IllegalArgumentException("BuferSize muss ein Vielfaches von INTSIZE sein.");
            
        this.filePath = filePath;
        this.bytesPerRead = bBufferSize;
         
    	fIS = new FileInputStream(filePath);
        fileChan = fIS.getChannel();
        fileChanSize = fileChan.size();
        if (fileChanSize % Constants.INTSIZE != 0)
        	throw new IllegalArgumentException("Die Dateigröße muss ein vielfaches von INTSIZE sein");
    }
    
    //Getters --------------
    
    /**
     * gibt den Pfad der Datei zurück
     * @return
     */
    public String getFilePath(){
        return filePath;
    }
    /**
     * Gibt die Größe der Datei zurück
     * @return
     */
    public long getFileChanSize(){
        return fileChanSize;
    }
    /**
     * Gibt zurück, ob die Datei noch geöffnet ist
     * @return
     */
    public boolean isOpen(){
        return fileChan.isOpen();
    }
    /**
     * Gibt zurück, ob die Datei bereits vollständig gelesen wurde
     * @return
     */
    public boolean isFileFullyReaded(){
    	return currentCursorPosition >= fileChanSize;
    }
    
    
    public void close() throws IOException{
        fileChan.close();
        fIS.close();
    }


//    public void read(ByteBuffer target){
//        try {
//            fileChan.read(target);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    public long getFileChanPosition(){
//        try {
//            return fileChan.position();
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//        return 0;
//    }
    
    /**
     * Liest von der aktuellen Leseposition maximal bytesPerRead - Elemente in den TargetBuffer
     * @param target
     * @return
     * @throws IOException 
     */
    public void readToByteBuffer(ByteBuffer target) throws IOException {
        int bytesToRead = (int)Math.min(bytesPerRead, fileChanSize- currentCursorPosition); //Insgesammt zu lesende Menge an Bytes
        if (target.capacity() < bytesToRead)
    		throw new IllegalArgumentException("Übergebener Buffer zu klein");
        
        target.clear();
        
        //Datei ggf stückweise einlesen, da es erstaunlicherweise schneller geht, z.b. 8 x 256 MB zulesen als einmal 4GB  
        while(bytesToRead > 0){
        	int currentReadSize = Math.min(Constants.MAXBYTESPERREADCALL, bytesToRead);

        	//Limit neu setzen
        	target.limit(target.position() + currentReadSize);
            
            //Lesen
            fileChan.read(target, currentCursorPosition);
            
            //Abschließende Arbeiten
            currentCursorPosition += currentReadSize;
            bytesToRead -= currentReadSize;
        }
        
      
        //Abschließende Arbeiten
        target.rewind(); //Position auf 0 setzen   
    }
    
}
