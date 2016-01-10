package msg;

import actors.ChordNode;

public class SearchPredecessorMessage extends ChordMessage {

	ChordNode searcher;

	public SearchPredecessorMessage(ChordNode joiner) {
		super();
		this.searcher = joiner;
	}

	public ChordNode getSearcher() {
		return searcher;
	}

}
