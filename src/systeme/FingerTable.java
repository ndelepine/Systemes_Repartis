package systeme;

import java.util.TreeMap;

import actors.ChordNode;

public class FingerTable {
	
	TreeMap<Integer,TableEntry> fingerTable = new TreeMap<Integer,TableEntry>();
	
	public FingerTable(int id){	
		//Id veut rentrer dans le système. A partir de id, on calcul les Noeuds vers lesquels il pointe (id+1,id+2,id+4,id+8,..id+M)
		//A partir de ces noeuds et des numéros de la ligne, on calcule lowerBound et upperBound. On met comme referent lui même.
		for (int i=1;i<=4;i++){
			ChordNode cntest = new ChordNode();
			fingertableEntry(i,0,0,0,cntest);
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
