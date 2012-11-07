package aufgabe2.algorithm;

import aufgabe2.interfaces.*;

class InputBuffer {
	
	/* Interne Klasse für Merge-Schritt */ 

	DataManager tapes;
	Channels currentChannel;
	DataWrapper input;
	int pos = 0;
	
	public	InputBuffer(DataManager data, Channels channel){
		tapes = data;
		currentChannel = channel;
		naechstesHaeppchenVomMergeRun(); //erste Zahlenfolge einlesen
	}
	
	public enum Channels{
		LEFTCHANNEL,
		RIGHTCHANNEL
	}
	
	public int getNext() {
        int elem;

        if (!hasNext()) //then nächsten block holen oder wenn der algorithmus vorbei ist, zumindest versuchen.
		{ 
			naechstesHaeppchenVomMergeRun(); //folgende Zahlenfolge einlesen

        }

        elem = input.getData()[pos];
		return elem;
	}
	
	public boolean hasNext(){
		/* Wenn der aktuelle block am ende ist gilt pos == inputgröße */ 
		return pos < input.getSize(); // am ende des algorithmus hat der input die größe. 
		
	}
	
	private void naechstesHaeppchenVomMergeRun(){
		input = null;
		input = (currentChannel == Channels.LEFTCHANNEL ? tapes.readLeftChannel() : tapes.readRightChannel());
		pos = 0;
	}
}
