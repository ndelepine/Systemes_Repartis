package test;

import org.junit.*;
import static org.junit.Assert.assertEquals;

import systeme.FingerTable;
import systeme.Interval;
import systeme.Key;
import actors.ChordActor;
import actors.ChordNode;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class TestFingerTable {

	@Test
	public void fingertableEntrytest() {

		Key key = new Key(0);

		//Création du système d'acteurs
		ActorSystem chord = ActorSystem.create("Chord");
		ActorRef catest = chord.actorOf(Props.create(ChordActor.class,key), "ChordActor-"+key.getIntKey());

		//Test de l'update d'une ligne dans la FingerTable
		ChordNode cntest = new ChordNode(key,catest,null,null);
		FingerTable fttest = new FingerTable(cntest);

		Interval interval = new Interval(1,4);
		//On insère une ligne dans la fingerTable
		fttest.fingertableEntry(1, 0, interval, cntest);

		//On récupère chaque élément de la TableEntry dans la FingerTable
		int IDNoeud = fttest.getFingerTable().get(1).getIDnoeud();
		int lower = interval.getLowerBound();
		int upper = interval.getUpperBound();
		ChordNode cn = fttest.getFingerTable().get(1).getReferent();

		//On teste l'égalité entre les éléments de la TableEntry et ceux insérés
		assertEquals(IDNoeud, 0);
		assertEquals(lower, 1);
		assertEquals(upper, 4);
		assertEquals(cn, cntest);
	}

	@Test
	public void calculIntervalletest() {
		Key key = new Key(0);
		//Création du système d'acteurs
		ActorSystem chord = ActorSystem.create("Chord");
		ActorRef catest = chord.actorOf(Props.create(ChordActor.class,key), "ChordActor-"+key.getIntKey());
		
		ChordNode cntest = new ChordNode(key,catest,null,null);
		FingerTable fttest = new FingerTable(cntest);

		//On récupère le numéro de la ligne et l'id du noeud pour calculer l'intervalle associée
		int numLine = 1; 
		int IDNoeud = cntest.getKey().getIntKey();
		//On calcule lower et upper en fonction de ces deux variables.
		int lower = fttest.calculIntervalle(numLine, IDNoeud).getLowerBound();
		int upper = fttest.calculIntervalle(numLine, IDNoeud).getUpperBound();

		//On teste l'égalité entre les éléments de la TableEntry et ceux insérés
		assertEquals(1, lower);
		assertEquals(2, upper);
	}

}
