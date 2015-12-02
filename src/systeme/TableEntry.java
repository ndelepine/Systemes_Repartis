package systeme;

import actors.ChordNode;

public class TableEntry {
	int IDnoeud;
	int ordreLigne;
	ChordNode referent;
	
	public TableEntry(int IDnoeud, int ordreligne, ChordNode referent){
		this.IDnoeud = IDnoeud;
		this.ordreLigne=ordreligne;
		this.referent=referent;
	}
}
