package test;

import static org.junit.Assert.*;

import org.junit.Test;

import systeme.Interval;
import systeme.Key;
import systeme.TableEntry;
import actors.ChordNode;

public class TestTableEntry {

	@Test
	public void testTableEntry() {
		Key key = new Key(0);
		ChordNode cn = new ChordNode(key);
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
