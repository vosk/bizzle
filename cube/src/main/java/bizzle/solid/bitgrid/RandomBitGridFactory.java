package bizzle.solid.bitgrid;

import java.util.Random;

public class RandomBitGridFactory {

    public final static int OVERLAP_OFFSET_BITS = 3;
    public final Random random;
    public final BitGridFactory bitGridFactory;

    public RandomBitGridFactory(Random random, BitGridFactory bitGridFactory) {
        this.random = random;
        this.bitGridFactory = bitGridFactory;
    }

    public BitGrid generateNext() {
        BitGrid datum = bitGridFactory.empty();

        for (int offset = 0; offset < bitGridFactory.getSpace().getSize(); offset += OVERLAP_OFFSET_BITS) {
            Long l = random.nextLong();
            datum = datum.symmetricDifference(bitGridFactory.ofLongAt(offset,l));
        }
        return datum;
    }
}
