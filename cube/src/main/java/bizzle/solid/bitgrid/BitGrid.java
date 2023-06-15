package bizzle.solid.bitgrid;

import bizzle.space.Coordinates3D;
import bizzle.space.Vector3D;
import lombok.*;
import org.javatuples.Pair;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

@Data
public class BitGrid {
    @EqualsAndHashCode.Exclude
    private final BitGridFactory.BitGridSpace space;
    private final ShiftBitSet bitSet;

    //    @EqualsAndHashCode.Exclude
//    @Getter(AccessLevel.PRIVATE)
//    @Setter(AccessLevel.PRIVATE)
//    private Pair<Coordinates3D, Coordinates3D> boundingBox;
    @EqualsAndHashCode.Exclude
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private ConcurrentHashMap<Vector3D, BitGrid> moveNeighbours = new ConcurrentHashMap<>();

    @Builder(toBuilder = true)
    private BitGrid(BitGridFactory.BitGridSpace space, ShiftBitSet bitSet) {
        this.space = space;
        this.bitSet = bitSet;
    }


    public boolean isEmpty() {
        return bitSet.isEmpty();
    }

    public BitGrid complement() {
        ShiftBitSet clone = this.bitSet.not();
        return this.toBuilder()
                .bitSet(clone)
                .build();
    }

    public BitGrid union(BitGrid other) {
        return binaryOperator(ShiftBitSet::or, other);
    }

    public BitGrid intersection(BitGrid other) {
        return binaryOperator(ShiftBitSet::and, other);
    }

    public BitGrid difference(BitGrid other) {
        return binaryOperator(ShiftBitSet::andNot, other);
    }

    public BitGrid symmetricDifference(BitGrid other) {
        return binaryOperator(ShiftBitSet::xor, other);
    }

    private BitGrid binaryOperator(BiFunction<ShiftBitSet, ShiftBitSet, ShiftBitSet> operator, BitGrid other) {
        ShiftBitSet clone = operator.apply(this.bitSet, other.bitSet);

        return this.toBuilder()
                .bitSet(clone)
                .build();
    }

    public boolean get(int index) {
        return bitSet.get(index);
    }

    public int cardinality() {
        return bitSet.cardinality();
    }

    public BitGrid move(Vector3D offset) {
        BitGrid cachedMove = moveNeighbours.get(offset);
        if (cachedMove != null) {
            return cachedMove;
        }
        BitGrid mask = space.maskFor(offset);
        int offsetIndex = Coordinates3D.expand(0, space).plus(offset).index();
        BitGrid result = this.toBuilder()
                .bitSet(bitSet.andNot(mask.bitSet).shiftRight(-offsetIndex))
                .build();
        moveNeighbours.put(offset, result);
        return result;
    }

    public BitGrid copy() {
        return new BitGrid(this.space, new ShiftBitSet(this.bitSet));
    }

    public Collection<Coordinates3D> toCoordinates() {
        return space.all()
                .stream()
                .filter(coordinates3D -> bitSet.get(coordinates3D.index()))
                .toList();
    }

}
