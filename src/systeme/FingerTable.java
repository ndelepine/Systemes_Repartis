package systeme;

import java.util.TreeMap;

import actors.ChordNode;

public class FingerTable {
	
	TreeMap fingerTable = new TreeMap();
	
	public TableEntry fingertableEntry (int numLine, int IDnoeud, int lowerBound, int upperBound, ChordNode referent){
		TableEntry tableEntry = new TableEntry(IDnoeud, lowerBound,upperBound, referent);
		fingerTable.put(numLine, tableEntry);
		return tableEntry;
	}
}
