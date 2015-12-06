package test;

import static org.junit.Assert.*;

import org.junit.Test;

import systeme.FingerTable;
import actors.ChordActor;
import actors.ChordNode;

public class TestChordActor {

	@Test
	public void testFindSuccessor() {
		//Test de la recherche du successeur
		ChordActor catest = new ChordActor(0);
		ChordNode cntest = new ChordNode(3);
		FingerTable fttest = new FingerTable(catest);

		int succ = catest.findSuccessor(cntest);
		//On teste l'égalité entre les éléments de la TableEntry et ceux insérés
		assertEquals(0, succ);
	}

}
