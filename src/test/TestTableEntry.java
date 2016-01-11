package test;

import org.junit.*;
import static org.junit.Assert.assertEquals;

import systeme.Interval;
import systeme.Key;
import systeme.TableEntry;
import actors.ChordActor;
import actors.ChordNode;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class TestTableEntry {

	@Test
	public void testTableEntry() {
		Key key = new Key(0);
		//Création du système d'acteurs
		ActorSystem chord = ActorSystem.create("Chord");
		ActorRef catest = chord.actorOf(Props.create(ChordActor.class,key), "ChordActor-"+key.getIntKey());
		ChordNode cn = new ChordNode(key,catest,null,null);
		
		Interval interval = new Interval(1, 4);
		TableEntry tetest = new TableEntry(0,interval,cn);
		
		int IDnoeud = tetest.getIDnoeud();
		int lower = tetest.getInterval().getLowerBound();
		int upper = tetest.getInterval().getUpperBound();
		ChordNode cnlel = tetest.getReferent();
		
		assertEquals(IDnoeud, 0);
		assertEquals(lower,1);
		assertEquals(upper,4);
		assertEquals(cnlel, cn);
		
	}

}
