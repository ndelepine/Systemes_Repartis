package systeme;

import java.util.TreeMap;

import actors.ChordNode;

public class FingerTable {
	
	TreeMap<Integer,TableEntry> fingerTable = new TreeMap<Integer,TableEntry>();
	
	public FingerTable(){
		
		for (int i=1;i<=4;i++){
			ChordNode cntest = new ChordNode();
			fingertableEntry(i,i+1,i+2,i+3,cntest);
		}
	}
	public void fingertableEntry (int numLine, int IDnoeud, int lowerBound, int upperBound, ChordNode referent){
		TableEntry tableEntry = new TableEntry(IDnoeud, lowerBound,upperBound, referent);
		fingerTable.put(numLine, tableEntry);
	}

	public TreeMap<Integer,TableEntry> getFingerTable() {
		return fingerTable;
	}

	public void setFingerTable(TreeMap fingerTable) {
		this.fingerTable = fingerTable;
	}
	
}
