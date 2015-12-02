package systeme;

import actors.ChordNode;

public class TableEntry {
	int IDnoeud;
	int lowerBound;
	int upperBound;
	ChordNode referent;
	
	public TableEntry(int IDnoeud, int lowerBound,int upperBound, ChordNode referent){
		this.IDnoeud = IDnoeud;
		this.lowerBound=lowerBound;
		this.upperBound=upperBound;
		this.referent=referent;
	}

	public int getIDnoeud() {
		return IDnoeud;
	}

	public void setIDnoeud(int iDnoeud) {
		IDnoeud = iDnoeud;
	}

	public int getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
	}

	public int getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(int upperBound) {
		this.upperBound = upperBound;
	}

	public ChordNode getReferent() {
		return referent;
	}

	public void setReferent(ChordNode referent) {
		this.referent = referent;
	}
	
	
}
