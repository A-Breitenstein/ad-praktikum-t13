package aufgabe2.data.jobs;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * Klasse zum Halten einiger Seiten (=ByteBuffers der Länge eines ReadCalls) im Abeitsspeicher
 * @author Markus Bruhn
 *
 */
public class MemPersistence {

	private List<ByteBuffer> bufferPages = new ArrayList<ByteBuffer>(); //Die aussschließlich im Abeitsspeicher gehaltenen Fragmente 
	private List<OccupancyData> bufferOccupancy = new ArrayList<OccupancyData>(); //Die Belegung der Buffers. NOTUSEDPAGE, wenn frei
	private final OccupancyData NOTUSEDPAGE = new OccupancyData("",1); 
	
	/**
	 * Gibt die Anzahl der aktuell freien Speicherseiten zurück
	 * @return
	 */
	public int getFreePages(){
		int count = 0;
		for (OccupancyData occupancy : bufferOccupancy){
			if (occupancy == NOTUSEDPAGE)
				count ++;
		}
		return count;
	}
	
	private int getFreePageIndex(){
		for(int i = 0; i<bufferOccupancy.size(); i++){
			if (bufferOccupancy.get(i) == NOTUSEDPAGE)
				return i;
		}
		return -1;
	}
	
	/**
	 * Gibt zurück, ob eine Seite im MemSpeicher abrufbar ist
	 * @param fileID die Bezeichnung der Datei (z.B. Pfad)
	 * @param startPos die StartPosition, ab welchem Inhalte benötigt werden
	 * @return
	 */
	public boolean isInPage(String fileID, long startPos){
		return getPageIndex(fileID,startPos) != -1;
	}
	
	/**
	 * Gibt den angeforderten Buffer zurück und tauscht ihn gegen einen angegebenen, freien Buffer aus
	 * @param fileID die Bezeichnung der Datei (z.B. Pfad)
	 * @param startPos die StartPosition, ab welchem Inhalte benötigt werden
	 * @param spareBuffer der Buffer, der statt dessen nun beschrieben werden kann
	 * @return
	 */
	public ByteBuffer popPage(String fileID, long startPos, ByteBuffer spareBuffer){
		int index = getPageIndex(fileID,startPos);
		if(index == -1)
			throw new IndexOutOfBoundsException("die gewünschte Page konnte nicht gefunden werden");  
		ByteBuffer page = bufferPages.get(index);
		bufferOccupancy.set(index, NOTUSEDPAGE);
		bufferPages.set(index, spareBuffer);
		return page;
	}
	
	/**
	 * speichert den Buffer ab und gibt dafür einen freien Speicher zurück
	 * @param fileID die Bezeichnung der Datei (z.B. Pfad)
	 * @param startPos die StartPosition, ab welchem die Inhalte in der Datei liegt
	 * @param data der Buffer, der
	 * @return
	 */
	public ByteBuffer pushPage(String fileID, long startPos, ByteBuffer data){
		int index = getFreePageIndex();
		if (index == -1)
			throw new IndexOutOfBoundsException("der MemPersistence-Speicher ist voll");
		ByteBuffer freePage = bufferPages.get(index);
		bufferOccupancy.set(index, new OccupancyData(fileID, startPos));
		bufferPages.set(index, data);
		return freePage;
	}
	
	
	private int getPageIndex(String fileID, long startPos){
		for(int i = 0; i<bufferOccupancy.size(); i++){
			OccupancyData occupancy = bufferOccupancy.get(i);
			if (occupancy.fileID == fileID && occupancy.startPos == startPos)
				return i;
		}
		return -1;
	}
	
	private class OccupancyData {
					
		private String fileID;
		private long startPos;
		
		public String getFileID(){
			return fileID;
		}
		
		public long getStartPos(){
			return startPos;
		}
		
		public OccupancyData(String fileID, long startPos){
			this.fileID = fileID;
			this.startPos = startPos;
		}
		
		
	}
	
	
}
