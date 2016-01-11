package msg;

import actors.ChordNode;

public class SearchPredecessorMessage extends ChordMessage {

	ChordNode searcher;

	public SearchPredecessorMessage(ChordNode searcher) {
		super();
		this.searcher = searcher;
	}

	public ChordNode getSearcher() {
		return searcher;
	}

}
