package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import systeme.FingerTable;
import systeme.Key;
import systeme.TableEntry;
import actors.ChordActor;
import actors.ChordNode;

public class TestChordActor {

	@Test
	public void testFindSuccessor() {
		//Test de la recherche du successeur
		Key keyNode = new Key(0);
		Key keyActor = new Key(10);
		
		ChordActor catest = new ChordActor(keyNode);
		ChordNode cntest = new ChordNode(keyActor);
		FingerTable fttest = new FingerTable(catest);

		TableEntry succ = catest.findIntervallReferent(cntest);
		//On teste l'égalité entre les éléments de la TableEntry et ceux insérés
		int res = succ.getReferent().getKey().getIntKey();
		assertEquals(0, res);
	}

}
