package actors;

import systeme.Hashable;
import systeme.Key;
import akka.actor.ActorRef;

public class ChordNode implements Hashable{

	Key key;
	ActorRef actorRef;
	
	public ChordNode(Key key, ActorRef actorRef ){
		this.key=key;
		this.actorRef=actorRef;
	}

	public Key getKey(){
		return this.key;
	}

	public ActorRef getRef(){
		return actorRef;
	}

}
