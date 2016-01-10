package msg;

import akka.actor.ActorRef;


public class StartMessage extends ChordMessage{

	ActorRef connaissance;
	
	
	public StartMessage(ActorRef connaissance) {
		this.connaissance = connaissance;
	}

	
	public ActorRef getConnaissance() {
		return connaissance;
	}
	
}
