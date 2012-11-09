package aufgabe2.algorithm;

import aufgabe2.interfaces.*;

class InputBuffer {
	
	/* Interne Klasse f�r Merge-Schritt */ 

	public DataManager tapes;
	private Channels currentChannel;
	private DataWrapper input;
	private int pos = 0;
			
	public	InputBuffer(DataManager data, Channels channel){
		tapes = data;
		currentChannel = channel;
		naechstesHaeppchenVomMergeRun(); //erste Zahlenfolge einlesen
	}
	
	public enum Channels{
		LEFTCHANNEL,
		RIGHTCHANNEL
	}
	
	/**
	 * Gibt das aktuelle Element zur�ck, sofern eines existiert
	 * @return
	 */
	public int getCurrent(){
		return input.getData()[pos];
	}
	/**
	 * Gibt zur�ck, ob es noch ein aktuelles Element gibt, welches zur�ckgegeben werden kann
	 * @return
	 */
	public boolean hasCurrent(){
		return pos < input.getSize();
	}
	/**
	 * Verschiebt das aktuelle Element um 1 nach rechts. Es wird kein Fehler ausgegeben, wenn hasCurrent==false ist.
	 */
	public void moveNext(){
		pos ++;
		if (!hasCurrent()){
			naechstesHaeppchenVomMergeRun();
		}	
	}
	
	private void naechstesHaeppchenVomMergeRun(){
        if (input == null || !input.isFolgeKomplett()) {
            input = null;
            input = (currentChannel == Channels.LEFTCHANNEL ? tapes.readLeftChannel() : tapes.readRightChannel());
        } else {
            input.setSize(0);
        }
		pos = 0;
	}
}
