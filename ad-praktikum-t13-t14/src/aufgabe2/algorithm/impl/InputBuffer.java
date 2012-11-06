package aufgabe2.algorithm.impl;

import aufgabe2.interfaces.*;

public class InputBuffer {
	
	/* Interne Klasse für den Merge-Schritt */ 

	DataManager tapes;
	Channels currentChannel;
	DataWrapper input;
	int pos = 0;
	
	public	InputBuffer(DataManager data, Channels channel){
		tapes = data;
		currentChannel = channel;
		nächstesHäppchenVomMergeRun(); //erste Zahlenfolge einlesen 
	}
	
	public enum Channels{
		LEFTCHANNEL,
		RIGHTCHANNEL
	}
	
	public int getNext() {
		int elem = input.getData()[pos];
		pos++;
		if (!hasNext()) //then nächsten block holen oder wenn der algorithmus vorbei ist, zumindest versuchen.
		{ 
			nächstesHäppchenVomMergeRun(); //folgende Zahlenfolge einlesen
		}
		return elem;
	}
	
	public boolean hasNext(){
		/* Wenn der aktuelle block am ende ist gilt pos == inputgröße */ 
		return pos < input.getSize(); // am ende des algorithmus hat der input die größe 0. 
		
	}
	
	private void nächstesHäppchenVomMergeRun(){
		input = null;
		input = (currentChannel == Channels.LEFTCHANNEL ? tapes.readLeftChannel() : tapes.readRightChannel());
		pos = 0;
	}
}
