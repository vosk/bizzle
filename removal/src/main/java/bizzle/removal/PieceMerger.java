package bizzle.removal;

import bizzle.solid.ComponentGraph;
import bizzle.solid.bitgrid.BitGrid;
import bizzle.space.Directions;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PieceMerger {


    public List<ComponentGraph> possibleMerges(BitGrid smallPiece, ComponentGraph graph) {
        BitGrid bigger = dilate(smallPiece);
        return graph.getComponents()
                .stream()
                .filter(bitGrid -> bitGrid != smallPiece)
                .filter(bitgrid -> bitgrid.cardinality() > smallPiece.cardinality()
                        || (bitgrid.cardinality() == smallPiece.cardinality() && bitgrid.hashCode() > smallPiece.hashCode()))
                .filter(component -> component.intersection(bigger).cardinality() > 0)
                .map( component -> graph.mergeComponents(component, smallPiece))
                .toList();
    }

    private BitGrid dilate(BitGrid smallPiece) {
        return Directions.neighBours().stream()
                        .map(smallPiece::move)
                                .reduce(smallPiece, BitGrid::union);
    }
}
