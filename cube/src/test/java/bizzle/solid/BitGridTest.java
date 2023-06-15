package bizzle.solid;

import bizzle.solid.bitgrid.BitGrid;
import org.junit.jupiter.api.Test;
import bizzle.solid.bitgrid.BitGridFactory;
import bizzle.space.Locus;
import bizzle.space.Space;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BitGridTest {

    public BitGridFactory factory = new BitGridFactory(new Space(2,4,5));

    @Test
    public void testBasicOperations() {

        Locus<BitGrid> locus = factory.locus();

        BitGrid x0andy1 = locus.planeX(0).union(locus.planeY(1));
        BitGrid x1 = locus.planeX(1);
//        assertEquals(Coordinates3D.builder()
//                .x(1)
//                .zx0andy1.intersection(x1).isEmpty());
        assertEquals(factory.getSpace().getSize(), x0andy1.union(x1).cardinality());
        assertTrue(x0andy1.union(x1).complement().isEmpty());
    }

    @Test
    public void testDifference() {
        Locus<BitGrid> locus = factory.locus();

        BitGrid x0 = locus.planeX(0);
        BitGrid notX1 = factory.full().difference(locus.planeX(1));
        assertEquals(x0, notX1);
        assertEquals(x0.union(notX1), x0);
        assertEquals(x0.union(notX1), notX1);
    }

}