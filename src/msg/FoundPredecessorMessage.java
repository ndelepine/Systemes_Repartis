package msg;


import actors.ChordNode;

public class FoundPredecessorMessage extends ChordMessage {

	ChordNode predecessor;	
	
	public FoundPredecessorMessage(ChordNode predecessor) {
		this.predecessor = predecessor;
	}

	public ChordNode getPredecessor() {
		return predecessor;
	}
	
}
