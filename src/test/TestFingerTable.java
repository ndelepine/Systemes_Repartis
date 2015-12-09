package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import systeme.FingerTable;
import systeme.Interval;
import systeme.Key;
import actors.ChordActor;
import actors.ChordNode;

public class TestFingerTable {

	@Test
	public void fingertableEntrytest() {
		
		Key key = new Key(0);
		
		//Test de l'update d'une ligne dans la FingerTable
		ChordActor catest = new ChordActor(key);
		FingerTable fttest = new FingerTable(catest);
		ChordNode cntest = new ChordNode(key);
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
		//Test du calcul des intervalles
		ChordActor catest = new ChordActor(key);
		FingerTable fttest = new FingerTable(catest);

		//On récupère le numéro de la ligne et l'id du noeud pour calculer l'intervalle associée
		int numLine = 1; 
		int IDNoeud = catest.getKey().getIntKey();
		//On calcule lower et upper en fonction de ces deux variables.
		int lower = fttest.calculIntervalle(numLine, IDNoeud).getLowerBound();
		int upper = fttest.calculIntervalle(numLine, IDNoeud).getUpperBound();
		
		//On teste l'égalité entre les éléments de la TableEntry et ceux insérés
		assertEquals(1, lower);
		assertEquals(2, upper);
	}

}
