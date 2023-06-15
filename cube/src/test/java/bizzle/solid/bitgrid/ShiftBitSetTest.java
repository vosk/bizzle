package bizzle.solid.bitgrid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShiftBitSetTest {

    @Test
    public void setBitWorks() {
        ShiftBitSet original = new ShiftBitSet(130);
        ShiftBitSet set2bits = original.set(1).set(66).set(129).set(130);
        for (int i = 0; i <= 130; i++) {
            assertEquals(i == 1 || i == 66 || i == 129, set2bits.get(i));
        }
        assertEquals(3, set2bits.cardinality());
        assertFalse(set2bits.isEmpty());
    }

    @Test
    public void notWorks() {
        ShiftBitSet original = new ShiftBitSet(130);
        ShiftBitSet set2bits = original.set(1).set(130).not();
        for (int i = 0; i < 130; i++) {
            assertEquals(i != 1, set2bits.get(i));
        }
        assertEquals(129, set2bits.cardinality());
        assertFalse(set2bits.isEmpty());

    }

    @Test
    public void orWorks() {
        ShiftBitSet original = new ShiftBitSet(130);
        ShiftBitSet bitone = original.set(1);
        ShiftBitSet bitsixty = original.set(60);
        ShiftBitSet result = bitone.or(bitsixty);
        for (int i = 0; i < 130; i++) {
            assertEquals(i == 1 || i == 60, result.get(i));
        }
        assertEquals(2, result.cardinality());
        assertFalse(result.isEmpty());

    }

    @Test
    public void andWorks() {
        ShiftBitSet original = new ShiftBitSet(130);
        ShiftBitSet bitone = original.set(1);
        ShiftBitSet bitsixty = original.set(60).not();
        ShiftBitSet result = bitone.and(bitsixty);
        for (int i = 0; i < 130; i++) {
            assertEquals(i == 1, result.get(i));
        }
        assertEquals(1, result.cardinality());
        assertFalse(result.isEmpty());


    }

    @Test
    public void xorWorks() {
        ShiftBitSet original = new ShiftBitSet(130);
        ShiftBitSet bitone = original.set(1);
        ShiftBitSet bitsixty = original.set(60).not();
        ShiftBitSet result = bitone.xor(bitsixty);
        for (int i = 0; i < 130; i++) {
            assertEquals(i != 1 && i != 60, result.get(i));
        }
        assertEquals(128, result.cardinality());
        assertFalse(result.isEmpty());

    }

    @Test
    public void shiftRightWorks() {
        ShiftBitSet original = new ShiftBitSet(130);
        ShiftBitSet bitTwoShift = original
                .set(2)
                .set(ShiftBitSet.WORDSIZE)
                .set(ShiftBitSet.WORDSIZE + 1)
                .shiftRight(2);
        for (int i = 0; i < 130; i++) {
            assertEquals(i == 0 || i == 62 || i == 63, bitTwoShift.get(i));
        }
        assertEquals(3, bitTwoShift.cardinality());
        assertFalse(bitTwoShift.isEmpty());
    }

    @Test
    public void shiftLeftWorks() {
        ShiftBitSet original = new ShiftBitSet(130);
        ShiftBitSet bitTwoShift = original
                .set(0)
                .set(ShiftBitSet.WORDSIZE - 1)
                .set(ShiftBitSet.WORDSIZE)
                .set(ShiftBitSet.WORDSIZE + 1)
                .set(130)
                .shiftRight(-2);
        for (int i = 0; i < 130; i++) {
            assertEquals(i == 2 || i == 65 || i == 66 || i == 67, bitTwoShift.get(i));
        }
        assertEquals(4, bitTwoShift.cardinality());
        assertFalse(bitTwoShift.isEmpty());


    }
}