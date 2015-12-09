package systeme;

import java.util.ArrayList;
import java.util.TreeMap;

import actors.ChordActor;
import actors.ChordNode;

public class FingerTable {
	
	TreeMap<Integer,TableEntry> fingerTable = new TreeMap<Integer,TableEntry>();
	
	public FingerTable(ChordActor actor){
		//Id veut rentrer dans le système. A partir de id, on calcul les Noeuds vers lesquels il pointe (id+1,id+2,id+4,id+8,..id+M)
		//A partir de ces noeuds et des numéros de la ligne, on calcule lowerBound et upperBound. On met comme referent lui même.
		System.out.println("-----------------------------\n");
		for (int i=1;i<=4;i++){
			int noeud = (int) (actor.getKey()+Math.pow(2, (i-1)));
			int lower = calculIntervalle(i, actor.getKey()).get(0);
			int upper = calculIntervalle(i, actor.getKey()).get(1);
			ChordNode ref = new ChordNode(actor.getKey());
			TableEntry entry = fingertableEntry(i,noeud,lower,upper,ref);

			System.out.println(entry);

		}
		System.out.println("-----------------------------\n\n\n");
		//System.out.println(this.getFingerTable());
		actor.setFingerTable(this);
	}
	public TableEntry fingertableEntry (int numLine, int IDnoeud, int lowerBound, int upperBound, ChordNode referent){
		TableEntry tableEntry = new TableEntry(IDnoeud, lowerBound,upperBound, referent);
		fingerTable.put(numLine, tableEntry);
		return tableEntry;
	}

	public TreeMap<Integer,TableEntry> getFingerTable() {
		return fingerTable;
	}

	public void setFingerTable(TreeMap<Integer,TableEntry> fingerTable) {
		this.fingerTable = fingerTable;
	}
	
	public ArrayList<Integer> calculIntervalle(int numLine, int IDNoeud){
		ArrayList<Integer> bounds = new ArrayList<Integer>();
		int lower = (int)(IDNoeud + Math.pow(2, (numLine-1))%Math.pow(2,4));
		int upper = (int)(IDNoeud + Math.pow(2, (numLine))%Math.pow(2,4));
		bounds.add(lower);
		bounds.add(upper);
		return bounds;
	}
	
}
