package actors;

import systeme.FingerTable;
import systeme.Hashable;
import systeme.TableEntry;

public class ChordActor implements Hashable{

	int key;
	FingerTable fingerTable;
	
	
	
	public ChordActor(int key) {
		super();
		this.key = key;
	}



	@Override
	public int getKey() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	public FingerTable getFingerTable() {
		return fingerTable;
	}



	public void setFingerTable(FingerTable fingerTable) {
		this.fingerTable = fingerTable;
	}



	public void setKey(int key) {
		this.key = key;
	}



	public int findSuccessor(ChordNode acteur) {
		//Voir si on retourne un Node ou un int
		int res=-1;
		//A remplacer par while
		for (int i=1;i<=fingerTable.getFingerTable().size();i++){
			TableEntry temp = fingerTable.getFingerTable().get(i);
			int lower = temp.getLowerBound();
			int upper = temp.getUpperBound();
			//On prend en compte le tour du cercle (ex : 6 n'appartient pas [4;0] mais à [4;8]
			if(upper < lower){
				upper += 16;
				if (acteur.getKey()<=upper & acteur.getKey()>=lower){
					System.out.println(temp.getLowerBound() +" - " + temp.getUpperBound() + " Ligne : " + i);
					res = temp.getReferent().getKey();
				}
			}
		}
		return res;
	}

}
