package actors;

import systeme.Hashable;
import systeme.Key;
import akka.actor.ActorRef;

public class ChordNode implements Hashable{

	Key key;
	
	public ChordNode(Key key){
		this.key=key;
	}

	public Key getKey(){
		return this.key;
	}
	
	public ActorRef getRef(){
		//Paramètre à mettre dans .tell
		return null;
	}

}
