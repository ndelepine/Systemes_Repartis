package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import systeme.FingerTable;
import actors.ChordNode;

public class TestFingerTable {

	@Test
	public void fingertableEntrytest() {
		//Test de l'update d'une ligne dans la FingerTable
		ChordNode cntest = new ChordNode(0);
		FingerTable fttest = new FingerTable(cntest);

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

	@Test
	public void Intervalltest() {
		//Test du calcul des intervalles
		ChordNode cntest = new ChordNode(0);
		FingerTable fttest = new FingerTable(cntest);

		//On récupère le numéro de la ligne et l'id du noeud pour calculer l'intervalle associée
		int numLine = 1; 
		int IDNoeud = cntest.getKey();
		//On calcule lower et upper en fonction de ces deux variables.
		int lower = fttest.calculIntervalle(numLine, IDNoeud).get(0);
		int upper = fttest.calculIntervalle(numLine, IDNoeud).get(1);
		
		//On teste l'égalité entre les éléments de la TableEntry et ceux insérés
		assertEquals(1, lower);
		assertEquals(2, upper);
	}

}
