package msg;

import actors.ChordNode;

public class JoinMessage extends ChordMessage {
	
	ChordNode joiner;

	public JoinMessage(ChordNode joiner) {
		super();
		this.joiner = joiner;
	}

	public ChordNode getJoiner() {
		return joiner;
	}

}
