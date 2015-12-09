package test;

import static org.junit.Assert.*;

import org.junit.Test;

import systeme.Key;
import systeme.TableEntry;
import actors.ChordNode;

public class TestTableEntry {

	@Test
	public void testTableEntry() {
		Key key = new Key(0);
		ChordNode cn = new ChordNode(key);
		TableEntry tetest = new TableEntry(0,1,4,cn);
		
		int IDnoeud = tetest.getIDnoeud();
		int lower = tetest.getLowerBound();
		int upper = tetest.getUpperBound();
		ChordNode cnlel = tetest.getReferent();
		
		assertEquals(IDnoeud, 0);
		assertEquals(lower,1);
		assertEquals(upper,4);
		assertEquals(cnlel, cn);
		
	}

}
