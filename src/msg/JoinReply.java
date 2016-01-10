package msg;

import java.util.Set;

import actors.ChordNode;

public class JoinReply extends ChordMessage {

	ChordNode replier;
	Set<ChordNode> listeAct;
	
	
	public JoinReply(Set<ChordNode> listeAct) {
		this.listeAct = listeAct;
	}

	public ChordNode getReplier() {
		return replier;
	}

	public Set<ChordNode> getListeAct() {
		return listeAct;
	}
	
}

