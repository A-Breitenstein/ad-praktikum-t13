package aufgabe2.algorithm;

import aufgabe2.interfaces.*;

class InputBuffer {
	
	/* Interne Klasse für Merge-Schritt */ 

	private DataManager tapes;
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
	 * Gibt das aktuelle Element zurück, sofern eines existiert
	 * @return
	 */
	public int getCurrent(){
		return input.getData()[pos];
	}
	/**
	 * Gibt zurück, ob es noch ein aktuelles Element gibt, welches zurückgegeben werden kann
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
		input = null;
		input = (currentChannel == Channels.LEFTCHANNEL ? tapes.readLeftChannel() : tapes.readRightChannel());
		pos = 0;
	}
}
