package bizzle.solid.bitgrid;

import lombok.EqualsAndHashCode;

import java.util.Collection;
import java.util.function.BiFunction;

@EqualsAndHashCode(cacheStrategy = EqualsAndHashCode.CacheStrategy.LAZY)
public class ShiftBitSet {

    public static final int WORDSIZE = Long.SIZE;
    private final long[] words;
    @EqualsAndHashCode.Exclude
    private final BitSetSetup setup;
    @EqualsAndHashCode.Exclude
    private int cardinalityCache=-1;

    public ShiftBitSet(int nbits) {
        this.setup = new BitSetSetup(nbits);
        words = new long[setup.nWords];
    }

    public ShiftBitSet(ShiftBitSet other) {
        this.setup = other.setup;
        words = other.words.clone();
    }

    public ShiftBitSet not() {
        ShiftBitSet not = new ShiftBitSet(this);
        for (int i = 0; i < words.length; i++) {
            not.words[i] = ~words[i];
        }
        not.words[setup.nWords - 1] &= setup.lastWordTrimMask;
        return not;
    }

    public boolean get(int index) {
        int wordIndex = index / WORDSIZE;
        int bitIndex = index % WORDSIZE;
        return (words[wordIndex] & (1L << bitIndex)) != 0;
    }

    public ShiftBitSet set(int index) {
        ShiftBitSet n = new ShiftBitSet(this);
        int wordIndex = index / WORDSIZE;
        int bitIndex = index % WORDSIZE;
        n.words[wordIndex] |= 1L << bitIndex;
        n.words[setup.nWords - 1] &= setup.lastWordTrimMask;
        return n;
    }

    public ShiftBitSet setAll(Collection<Integer> indices) {
        ShiftBitSet n = new ShiftBitSet(this);
        indices.forEach(i -> {
                    int wordIndex = i / WORDSIZE;
                    int bitIndex = i % WORDSIZE;
                    n.words[wordIndex] |= 1L << bitIndex;
                }
        );

        n.words[setup.nWords - 1] &= setup.lastWordTrimMask;
        return n;
    }

    public ShiftBitSet placeLong(int index, long data) {
        ShiftBitSet n = new ShiftBitSet(this);
        int wordIndex = index / WORDSIZE;
        int bitIndex = index % WORDSIZE;
        n.words[wordIndex] |= data << bitIndex;
        if (wordIndex + 1 < n.words.length && bitIndex != 0) {
            n.words[wordIndex + 1] |= data << (Long.SIZE - bitIndex);
        }
        n.words[setup.nWords - 1] &= setup.lastWordTrimMask;
        return n;
    }

    public ShiftBitSet shiftRight(int shift) {
        if (shift > 0) {
            return shiftRightPositive(shift);
        } else if (shift < 0) {
            return shiftLeftPositive(-shift);
        }
        return this;
    }

    private ShiftBitSet shiftRightPositive(int shift) {
        ShiftBitSet n = new ShiftBitSet(this);
        int wordOffset = shift / WORDSIZE;
        int bitOffset = shift % WORDSIZE;
        long lowerPartMask = (1L << bitOffset) - 1;

        //lower part-words
        for (int i = wordOffset; i < setup.nWords; i++) {
            n.words[i - wordOffset] = words[i] >>> bitOffset;
        }
        //upper part-words
        for (int i = wordOffset; i < setup.nWords - 1; i++) {
            n.words[i - wordOffset] |= (words[i + 1] & lowerPartMask) << (WORDSIZE - bitOffset);
        }
        return n;
    }

    private ShiftBitSet shiftLeftPositive(int shift) {
        ShiftBitSet n = new ShiftBitSet(this);
        int wordOffset = shift / WORDSIZE;
        int bitOffset = shift % WORDSIZE;

        //upper part-words
        for (int i = wordOffset; i < setup.nWords; i++) {
            n.words[i] = words[i - wordOffset] << bitOffset;
        }
        //lower part-words
        for (int i = wordOffset; i < setup.nWords - 1; i++) {
            n.words[i + 1] |= words[i - wordOffset] >>> (WORDSIZE - bitOffset);
        }
        //Trim the end
        n.words[setup.nWords - 1] &= setup.lastWordTrimMask;
        return n;
    }


    public int cardinality() {
        if (this.cardinalityCache <0) {
            int cardinality = 0;
            for (long word : words) {
                cardinality += Long.bitCount(word);
            }
            this.cardinalityCache = cardinality;
        }

        return cardinalityCache;
    }

    public boolean isEmpty() {
        long stack = 0;
        for (int i = 0; i < words.length; i++) {
            stack |= words[i];
        }
        return stack == 0;
    }

    private static int fitBitsIntoWords(int nbits, int wordsize) {
        return (int) Math.ceil((double) nbits / ((double) wordsize));
    }

    public ShiftBitSet or(ShiftBitSet other) {
        ShiftBitSet result = new ShiftBitSet(this);
        binaryOperation(result.words, other.words, (a, b) -> a | b);
        return result;
    }


    public ShiftBitSet and(ShiftBitSet other) {
        ShiftBitSet result = new ShiftBitSet(this);
        binaryOperation(result.words, other.words, (a, b) -> a & b);
        return result;
    }

    public ShiftBitSet xor(ShiftBitSet other) {
        ShiftBitSet result = new ShiftBitSet(this);
        binaryOperation(result.words, other.words, (a, b) -> a ^ b);
        return result;
    }

    public ShiftBitSet andNot(ShiftBitSet other) {
        ShiftBitSet result = new ShiftBitSet(this);
        binaryOperation(result.words, other.words, (a, b) -> a & (~b));
        return result;
    }

    private void binaryOperation(long[] a, long[] b, BiFunction<Long, Long, Long> op) {
        for (int i = 0; i < words.length; i++) {
            a[i] = op.apply(a[i], b[i]);
        }
        words[setup.nWords - 1] &= setup.lastWordTrimMask;
    }

    public ShiftBitSet copy() {
        return new ShiftBitSet(this);
    }


    private static class BitSetSetup {
        private final int nbits;
        private final int nWords;

        private final long lastWordTrimMask;

        private BitSetSetup(int nbits) {
            this.nbits = nbits;
            this.nWords = fitBitsIntoWords(nbits, WORDSIZE);
            long lastWordBits = nbits % WORDSIZE;
            if (lastWordBits != 0) {
                lastWordTrimMask = (1L << lastWordBits) - 1;
            } else {
                lastWordTrimMask = -1; //0xFF...FF
            }

        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = setup.nWords - 1; i >= 0; i--) {
            stringBuilder.append(Long.toBinaryString(words[i]));
        }
        return stringBuilder.reverse().toString();

    }

}
