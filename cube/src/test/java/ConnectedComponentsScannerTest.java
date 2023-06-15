import org.junit.jupiter.api.Test;
import bizzle.solid.ConnectedComponentsScanner;
import bizzle.solid.bitgrid.BitGrid;
import bizzle.solid.bitgrid.BitGridFactory;
import bizzle.solid.bitgrid.RandomBitGridFactory;
import bizzle.space.Space;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


class ConnectedComponentsScannerTest {

    public BitGridFactory bitGridFactory = new BitGridFactory(new Space(4,4,4));
//    public String SAMPLE= "0001001000110001110000101000110000011011011100101100011000011111";
    public Random random = new Random(0);

    @Test
    public void test() {

//        long sample = new BigInteger(new StringBuilder(SAMPLE).reverse().toString(), 2).longValue();
        RandomBitGridFactory randomBitGridFactory = new RandomBitGridFactory(random, bitGridFactory);
//        BitGrid bitGrid = bitGridFactory.ofLongAt(0, sample);

        for (int i=0;i<100000;i++) {
            ConnectedComponentsScanner connectedComponentsScanner = new ConnectedComponentsScanner(new BitGrid[]{randomBitGridFactory.generateNext()});

//            System.out.println(connectedComponentsScanner.getBitGrid().getBitSet());
//            System.out.println(Arrays.toString(connectedComponentsScanner.getLabels()));
//            System.out.println(Arrays.toString(connectedComponentsScanner.getAssignments()));
//            System.out.println(Arrays.toString(IntStream.range(0, bitGridFactory.getSpace().getSize()).map(j -> connectedComponentsScanner.componentOf(j)).asLongStream().toArray()));
            List<BitGrid> components = connectedComponentsScanner.getComponents(bitGridFactory);
            BitGrid union = components.stream().reduce(BitGrid::union).get();
            BitGrid intersection = components.stream().reduce(BitGrid::intersection).get();
            assertEquals(bitGridFactory.getSpace().getSize(), union.cardinality());
            assertEquals(0, intersection.cardinality());
//            components.forEach(c -> System.out.println(c.getBitSet().toString()));
//            System.out.println(Arrays.toString(connectedComponentsScanner.getAssignments()));


        }
    }

}