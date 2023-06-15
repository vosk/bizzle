package bizzle.removal;

import bizzle.solid.bitgrid.BitGrid;
import bizzle.solid.bitgrid.BitGridFactory;
import bizzle.space.Space;
import bizzle.space.Vector3D;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RemovalPathPlannerTest {
    public BitGridFactory bitGridFactory = new BitGridFactory(new Space(2, 2, 2));

    @Test
    public void testMultiplicity() {
        String OTHER = "01111111";
        long other = new BigInteger(new StringBuilder(OTHER).reverse().toString(), 2).longValue();
        BitGrid otherGrid = bitGridFactory.ofLongAt(0, other);
        BitGrid pieceGrid = otherGrid.complement();
        RemovalPathPlanner removalPathPlanner = new RemovalPathPlanner();
        List<Vector3D> lists = removalPathPlanner.removePaths(pieceGrid,otherGrid);
        System.out.println(lists);
        assertEquals(3, lists.size());
        assertTrue(lists.contains(new Vector3D(-1, 0, 0)));
        assertTrue(lists.contains(new Vector3D(0, -1, 0)));
        assertTrue(lists.contains(new Vector3D(0, 0, -1)));
    }

    @Test
    public void testMultiplicity2() {
        String OTHER = "00111111";
        long other = new BigInteger(new StringBuilder(OTHER).reverse().toString(), 2).longValue();
        BitGrid otherGrid = bitGridFactory.ofLongAt(0, other);
        BitGrid pieceGrid = otherGrid.complement();
        RemovalPathPlanner removalPathPlanner = new RemovalPathPlanner();
        List<Vector3D> lists = removalPathPlanner.removePaths(pieceGrid,otherGrid);
        System.out.println(lists);
        assertEquals(4, lists.size());
        assertTrue(lists.contains(new Vector3D(0, -1, 0)));
        assertTrue(lists.contains(new Vector3D(0, 0, -1)));
    }

    @Test
    public void testMultiplicity3() {
        BitGridFactory bitGridFactory = new BitGridFactory(new Space(3, 3, 3));
        String OTHER = "110111111100100111000011011";
        long other = new BigInteger(new StringBuilder(OTHER).reverse().toString(), 2).longValue();
        BitGrid otherGrid = bitGridFactory.ofLongAt(0, other);
        BitGrid pieceGrid = otherGrid.complement();
        RemovalPathPlanner removalPathPlanner = new RemovalPathPlanner();
        List<Vector3D> lists = removalPathPlanner.removePaths(pieceGrid,otherGrid);
        System.out.println(lists);
        assertEquals(1, lists.size());
    }

    @Test
    public void testMultiplicity4() {
        BitGridFactory bitGridFactory = new BitGridFactory(new Space(3, 3, 3));
        String OTHER = "111"+ "101" + "111".repeat(7);
        long other = new BigInteger(new StringBuilder(OTHER).reverse().toString(), 2).longValue();
        BitGrid otherGrid = bitGridFactory.ofLongAt(0, other);
        BitGrid pieceGrid = otherGrid.complement();
        RemovalPathPlanner removalPathPlanner = new RemovalPathPlanner();
        List<Vector3D> lists = removalPathPlanner.removePaths(pieceGrid, otherGrid);
        System.out.println(lists);
        assertEquals(1, lists.size());
        assertTrue(lists.contains(new Vector3D(0, 0, -1)));
    }


    @Test
    public void testMultiplicity5() {
        BitGridFactory bitGridFactory = new BitGridFactory(new Space(2, 2, 3));

        String OTHER = "11110110";
        long other = new BigInteger(new StringBuilder(OTHER).reverse().toString(), 2).longValue();
        BitGrid otherGrid = bitGridFactory.ofLongAt(0, other);
        BitGrid pieceGrid = otherGrid.complement();

        RemovalPathPlanner removalPathPlanner = new RemovalPathPlanner();
        List<Vector3D> lists = removalPathPlanner.removePaths(pieceGrid, otherGrid);
        System.out.println(lists);
        assertEquals(1, lists.size());
        assertTrue(lists.contains(new Vector3D(0, 0, 1)));

        List<Vector3D> lists2 = removalPathPlanner.removePaths(otherGrid, pieceGrid);
        System.out.println(lists2);
        assertEquals(1, lists.size());
        assertTrue(lists2.contains(new Vector3D(0, 0, -1)));
    }

}