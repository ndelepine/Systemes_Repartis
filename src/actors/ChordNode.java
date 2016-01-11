package actors;

import systeme.Hashable;
import systeme.Key;
import akka.actor.ActorRef;

public class ChordNode implements Hashable{

	Key key;
	ActorRef actorRef;
	ChordNode successeur;
	ChordNode predecesseur;
	
	public ChordNode(Key key, ActorRef actorRef, ChordNode successeur, ChordNode predecesseur){
		this.key=key;
		this.actorRef=actorRef;
		this.successeur=successeur;
		this.predecesseur=predecesseur;
	}

	public Key getKey(){
		return this.key;
	}

	public ActorRef getRef(){
		return actorRef;
	}

	public ChordNode getSuccesseur() {
		return successeur;
	}

	public void setSuccesseur(ChordNode successeur) {
		this.successeur = successeur;
	}

	public ChordNode getPredecesseur() {
		return predecesseur;
	}

	public void setPredecesseur(ChordNode predecesseur) {
		this.predecesseur = predecesseur;
	}

	
}
