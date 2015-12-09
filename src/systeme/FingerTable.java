package systeme;

import java.util.TreeMap;

import actors.ChordActor;
import actors.ChordNode;

public class FingerTable {
	
	TreeMap<Integer,TableEntry> fingerTable = new TreeMap<Integer,TableEntry>();
	
	public FingerTable(ChordActor actor){
		//Id veut rentrer dans le système. A partir de id, on calcul les Noeuds vers lesquels il pointe (id+1,id+2,id+4,id+8,..id+M)
		//A partir de ces noeuds et des numéros de la ligne, on calcule lowerBound et upperBound. On met comme referent lui même.
		System.out.println("-----------------------------\n");
		for (int i=1;i<=Key.nbLignesFingerTable;i++){
			int noeud = (int) (actor.getKey().getIntKey()+Math.pow(2, (i-1)));
			Interval interval = calculIntervalle(i, actor.getKey().getIntKey());
			ChordNode ref = new ChordNode(actor.getKey());
			TableEntry entry = fingertableEntry(i,noeud,interval,ref);

			System.out.println(entry);

		}
		System.out.println("-----------------------------\n\n\n");
		//System.out.println(this.getFingerTable());
		actor.setFingerTable(this);
	}
	public TableEntry fingertableEntry (int numLine, int IDnoeud, Interval interval, ChordNode referent){
		TableEntry tableEntry = new TableEntry(IDnoeud, interval, referent);
		fingerTable.put(numLine, tableEntry);
		return tableEntry;
	}

	public TreeMap<Integer,TableEntry> getFingerTable() {
		return fingerTable;
	}

	public void setFingerTable(TreeMap<Integer,TableEntry> fingerTable) {
		this.fingerTable = fingerTable;
	}
	
	public Interval calculIntervalle(int numLine, int IDNoeud){
		int nbLignes = Key.nbLignesFingerTable;
		int lower = (int)(IDNoeud + Math.pow(2, (numLine-1))%Math.pow(2,nbLignes));
		int upper = (int)(IDNoeud + Math.pow(2, (numLine))%Math.pow(2,nbLignes));
		Interval res = new Interval(lower,upper);
		return res;
	}
	
}
