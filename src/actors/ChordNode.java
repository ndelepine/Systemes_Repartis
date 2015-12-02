package actors;

import akka.actor.ActorRef;
import systeme.Hashable;

public class ChordNode implements Hashable{

	int key;

	public int getKey(){
		return this.key;
	}
	
	public ActorRef getRef(){
		//Paramètre à mettre dans .tell
		return null;
	}

}
