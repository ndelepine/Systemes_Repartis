package systeme;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import actors.ChordNode;
import akka.actor.ActorRef;


public class Data{
	//Classe qui contient les cléfs des acteurs présents dans le système ainsi que leur référence et la référence de leur successeur
	private TreeMap<Integer,ArrayList<ChordNode>> references = new TreeMap<Integer,ArrayList<ChordNode>>();
	
	/** Constructeur privé */
	private Data()
	{}

	/** Instance unique non préinitialisée */
	private static Data actorRefs = null;

	/** Point d'accès pour l'instance unique du singleton */
	public static Data getInstance()
	{			
		if (actorRefs == null)
		{ 	
			actorRefs = new Data();	
		}
		return actorRefs;
	}
	
	public ChordNode calculRef(int numeroActeur){
		ChordNode calc = null;
		try {
			calc = references.get(numeroActeur).get(0);
		} catch(NullPointerException npe) {

		}
		return calc;
	}
	
	//Calcul du sucesseur en fonction de la clé de l'acteur
	public ChordNode calculSuccessor(int numeroActeur){
		return references.get(numeroActeur).get(1);
	}
	
	//Calcul du sucesseur en fonction de la référence de l'acteur
	public ChordNode calculSuccessor(ActorRef ref){
		ChordNode result = null;
		for(Map.Entry<Integer,ArrayList<ChordNode>> entry : references.entrySet()) {
			if(entry.getValue().get(0).getRef()==ref){
				result = entry.getValue().get(1);
			}
		}
		return result;
	}
	
	public void updateRef(int numeroActeur, ChordNode ref, ChordNode successor){
		ArrayList<ChordNode> refs = new ArrayList<ChordNode>();
		refs.add(ref);
		refs.add(successor);
		references.put(numeroActeur, refs);
	}
	
}