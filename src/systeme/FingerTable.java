package systeme;

import actors.ChordNode;

public class FingerTable {
	public TableEntry fingertableEntry (int IDnoeud, int ordreLigne, ChordNode referent){
		TableEntry tableEntry = new TableEntry(IDnoeud, ordreLigne, referent);
		return tableEntry;
	}
}
