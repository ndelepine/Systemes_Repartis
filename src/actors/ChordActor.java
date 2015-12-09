package actors;

import systeme.FingerTable;
import systeme.Hashable;
import systeme.Interval;
import systeme.Key;
import systeme.TableEntry;


public class ChordActor implements Hashable{

	Key key;
	FingerTable fingerTable;
	
	
	
	public ChordActor(Key key) {
		super();
		this.key = key;
	}



	@Override
	public Key getKey() {
		// TODO Auto-generated method stub
		return this.key;
	}
	
	
	
	public FingerTable getFingerTable() {
		return fingerTable;
	}



	public void setFingerTable(FingerTable fingerTable) {
		this.fingerTable = fingerTable;
	}

	//Fonction qui regarde à quelle intervalle appartient l'acteur et qui renvoie la ligne
	public TableEntry findIntervallReferent(ChordNode acteur) {
		//Voir si on retourne un Node ou un int
		TableEntry res=null;
		//ON parcourt la table à l'envers (gain de temps)
		for (int i=1;i<=fingerTable.getFingerTable().size();i++){
			TableEntry temp = fingerTable.getFingerTable().get(i);
			Interval interval = temp.getInterval();
			//On prend en compte le tour du cercle (ex : 6 n'appartient pas [4;0] mais à [4;8]
			int lower = interval.getLowerBound();
			int upper = interval.getUpperBound();
			if(upper < lower){
				upper += 16;
				if (acteur.getKey().getIntKey()<=upper & acteur.getKey().getIntKey()>=lower){
					System.out.println(interval.getLowerBound() +" - " + interval.getUpperBound() + " Ligne : " + i);
					res = temp;
				}
			}
		}
		return res;
	}

	//Protocole
}
