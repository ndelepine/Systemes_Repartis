package systeme;

import actors.ChordNode;

public class TableEntry {
	int IDnoeud;
	Interval interval;
	ChordNode referent;
	
	public TableEntry(int IDnoeud, Interval interval, ChordNode referent){
		this.IDnoeud = IDnoeud;
		this.interval = interval;
		this.referent=referent;
	}

	public int getIDnoeud() {
		return IDnoeud;
	}

	public void setIDnoeud(int iDnoeud) {
		IDnoeud = iDnoeud;
	}

	public ChordNode getReferent() {
		return referent;
	}

	public void setReferent(ChordNode referent) {
		this.referent = referent;
	}

	public Interval getInterval() {
		return interval;
	}

	public void setInterval(Interval interval) {
		this.interval = interval;
	}

	@Override
	public String toString() {
		return "ID = "+IDnoeud + " | intervall = " +interval+ " | referent = " + referent.getKey().getIntKey()+"\n" ;
	}
	
	
}
