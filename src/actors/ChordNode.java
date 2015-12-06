package actors;

import akka.actor.ActorRef;
import systeme.Hashable;
import systeme.FingerTable;

public class ChordNode implements Hashable{

	int key;
	
	public ChordNode(int key){
		this.key=key;
	}

	public int getKey(){
		return this.key;
	}
	
	public ActorRef getRef(){
		//Paramètre à mettre dans .tell
		return null;
	}

}
