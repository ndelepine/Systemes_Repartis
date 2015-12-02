package test;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import systeme.FingerTable;
import systeme.TableEntry;
import actors.ChordNode;

public class TestFingerTable {

	@Test
	public void fingertableEntrytest() {
		ChordNode cntest = new ChordNode();
		FingerTable fttest = new FingerTable();
		
		//On insère une ligne dans la fingerTable
		fttest.fingertableEntry(1, 0, 1, 4, cntest);

		//On récupère chaque élément de la TableEntry dans la FingerTable
		int IDNoeud = fttest.getFingerTable().get(1).getIDnoeud();
		int lower = fttest.getFingerTable().get(1).getLowerBound();
		int upper = fttest.getFingerTable().get(1).getUpperBound();
		ChordNode cn = fttest.getFingerTable().get(1).getReferent();
		
		//On teste l'égalité entre les éléments de la TableEntry et ceux insérés
		assertEquals(IDNoeud, 0);
		assertEquals(lower, 1);
		assertEquals(upper, 4);
		assertEquals(cn, cntest);
	}
}
