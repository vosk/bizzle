package bizzle.solid;

import bizzle.solid.bitgrid.BitGrid;
import lombok.Getter;
import bizzle.solid.bitgrid.BitGridFactory;
import bizzle.space.Coordinates3D;
import bizzle.space.Directions;
import bizzle.space.Space;
import org.javatuples.Tuple;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Getter
public class ConnectedComponentsScanner {
    private final BitGrid[] bitGrids;

    private final int[] values; //cached computation of valueOfIndex()
    private final int[] assignments;
    private final int[] labels;
    private boolean hasRun;

    public ConnectedComponentsScanner(BitGrid[] bitGrids) {
        this.bitGrids = bitGrids;
        Space space = bitGrids[0].getSpace();
        assignments = new int[space.getSize()];
        labels = new int[space.getSize()];
        values = new int[space.getSize()];
        for (int i = 0; i < space.getSize(); i++) {
            labels[i] = i;
            values[i] = valueOfIndex(i);
        }

        hasRun = false;
    }

    private void runHoshenKopelman() {
        int nextLabel = 0;
        Space space = bitGrids[0].getSpace();

        for (int index = 0; index < space.getSize(); index++) {
            Coordinates3D current = Coordinates3D.expand(index, space);
            int value = values[index];

            int[] neighbours = Directions
                    .negativeNeighbours(current)
                    .filter(coordinates3D -> values[coordinates3D.index()] == value)
                    .mapToInt(coordinates3D -> assignments[coordinates3D.index()])
                    .toArray();
            PairOfCoordinates3D[] neighbourPairs = allPairs(neighbours);
            if (neighbours.length == 0) {
                assignments[index] = nextLabel++;
                continue;
            }
            if (neighbours.length == 1) {
                assignments[index] = find(neighbours[0]);
                continue;
            }
            int assignment = space.getSize() + 1;
            for (PairOfCoordinates3D pair : neighbourPairs) {
                if (pair.a != pair.b) {
                    int minIndex = Math.min(pair.a, pair.b);
                    int maxIndex = Math.max(pair.a, pair.b);
                    union(maxIndex, minIndex);
                    assignment = Math.min(minIndex, assignment);
                } else {
                    assignment = Math.min(pair.a, assignment);
                }

            }
            assignments[index] = find(assignment);
        }

    }

    void union(int toChange, int reference) {
        labels[find(toChange)] = find(reference);
    }

    int find(int x) {
        int y = x;

        while (labels[y] != y)
            y = labels[y];

        while (labels[x] != x) {
            int z = labels[x];
            labels[x] = y;
            x = z;
        }

        return y;
    }
    private int valueOfIndex(int index) {
        int value=0;
        for(int i=0;i<bitGrids.length;i++) {
            value = value<<1;
            value = value | (bitGrids[i].get(index)?1:0);
        }
        return value;
    }
    private void condenseAssignments() {
        for (int i = 0; i < bitGrids[0].getSpace().getSize(); i++) {
            assignments[i] = find(assignments[i]);
        }
    }

    private void run() {
        if (!hasRun) {
            hasRun = true;
            runHoshenKopelman();
            condenseAssignments();
        }
    }

    public List<BitGrid> getComponents(BitGridFactory bitGridFactory) {
        run();
        return IntStream.range(0, bitGridFactory.getSpace().getSize())
                .boxed()
                .map(i -> {
                    Collection<Integer> points = indicesOfComponent(i);
                    if (points.size() == 0) {
                        return null;
                    }
                    return bitGridFactory.ofIndices(points);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private Collection<Integer> indicesOfComponent(int component) {
        return IntStream.range(0, bitGrids[0].getSpace().getSize())
                .filter(i -> assignments[i] == component)
                .boxed()
                .toList();
    }

    public static PairOfCoordinates3D[] allPairs(int[] coordinates) {
        PairOfCoordinates3D[] result = new PairOfCoordinates3D[(coordinates.length * (coordinates.length - 1)) / 2];
        int counter = 0;
        for (int i = 0; i < coordinates.length - 1; i++) {
            for (int j = i + 1; j < coordinates.length; j++) {
                result[counter++] = new PairOfCoordinates3D(coordinates[i], coordinates[j]);
            }
        }
        return result;
    }

    public record PairOfCoordinates3D(int a, int b) {
    }
}
