package bizzle.solid.bitgrid;

import bizzle.space.Coordinates3D;
import bizzle.space.Locus;
import bizzle.space.Space;
import bizzle.space.Vector3D;
import bizzle.space.locus.LocusAdapter;
import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Getter
public class BitGridFactory {

    private final BitGridSpace space;

    public BitGridFactory(Space space) {
        this.space = new BitGridSpace(space, this);
    }


    public BitGrid empty() {
        return BitGrid.builder()
                .space(space)
                .bitSet(space.allocate())
                .build();
    }

    public BitGrid of(Collection<Coordinates3D> points) {
        ShiftBitSet allocated = space.allocate();
        allocated = allocated.setAll(points.stream().map(Coordinates3D::index).toList());
        return BitGrid.builder()
                .space(space)
                .bitSet(allocated)
                .build();
    }

    public BitGrid ofIndices(Collection<Integer> points) {
        ShiftBitSet allocated = space.allocate();
        allocated = allocated.setAll(points);
        return BitGrid.builder()
                .space(space)
                .bitSet(allocated)
                .build();
    }

    public BitGrid ofLongAt(int index, long data) {
        ShiftBitSet allocated = space.allocate();
        allocated = allocated.placeLong(index, data);
        return BitGrid.builder()
                .space(space)
                .bitSet(allocated)
                .build();
    }

    public BitGrid full() {
        ShiftBitSet allocated = space.allocate().not();
        return BitGrid.builder()
                .space(space)
                .bitSet(allocated)
                .build();
    }

    public Locus<BitGrid> locus() {
        return new LocusAdapter<>(
                space,
                this::of
        ).getAdapter();
    }

    public static class BitGridSpace extends Space {

        private final ShiftBitSet empty;
        private final BitGridFactory factory;
        private final ConcurrentHashMap<Vector3D, BitGrid> masks = new ConcurrentHashMap<>();

        private BitGridSpace(Space space, BitGridFactory bitGridFactory) {
            super(space);
            factory = bitGridFactory;
            empty = new ShiftBitSet(getSize());
        }

        public ShiftBitSet allocate() {
            return empty.copy();
        }

        public BitGrid maskFor(Vector3D offset) {
            BitGrid bitGrid = masks.get(offset);
            if (bitGrid == null) {
                bitGrid = generateMask(offset);
                masks.put(offset, bitGrid);
            }
            return bitGrid;
        }

        private BitGrid generateMask(Vector3D offset) {
            List<Integer> xPlanes = offsetToPlane(offset.getX(), getSizeX());
            List<Integer> yPlanes = offsetToPlane(offset.getY(), getSizeY());
            List<Integer> zPlanes = offsetToPlane(offset.getZ(), getSizeZ());
            return factory.of(
                    filterCoordinates(coordinates3D ->
                            xPlanes.contains(coordinates3D.getX())
                                    || yPlanes.contains(coordinates3D.getY())
                                    || zPlanes.contains(coordinates3D.getZ()))
            );
        }

        public List<Integer> offsetToPlane(int offset, int size) {
            if (offset > 0) {
                return IntStream.range(size - offset, size).boxed().toList();
            } else {
                return IntStream.range(0, -offset).boxed().toList();
            }
        }
    }
}
