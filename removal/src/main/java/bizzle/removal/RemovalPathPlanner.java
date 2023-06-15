package bizzle.removal;

import bizzle.solid.bitgrid.BitGrid;
import bizzle.space.Directions;
import bizzle.space.Vector3D;
import lombok.*;
import org.javatuples.Pair;

import java.util.*;

public class RemovalPathPlanner {

    public List<Vector3D> removePaths(BitGrid piece, BitGrid reducedComponents) {
//        Optional<BitGrid> reducedComponentsOptional = components.stream().reduce(BitGrid::union);
//
//        if (reducedComponentsOptional.isEmpty()) {
//            return Collections.emptyList();
//        }
//        BitGrid reducedComponents = reducedComponentsOptional.get();


        Node root = Node.builder().origin(piece).travelled(Vector3D.ZERO).build();
        List<Node> ways = new ArrayList<>(6);
        for (var dir : Directions.neighBours()) {
            Node neighbour = root.move(dir);
            if (neighbour == null) {
                continue;
            }
            ways.add(neighbour);
        }

        return ways.stream()
                .map( node -> Pair.with(node.travelled,leadsToRemoval(node, reducedComponents)))
                .filter(Pair::getValue1)
                .map(Pair::getValue0)
                .toList();
    }

    private boolean leadsToRemoval(Node node, BitGrid reducedComponents) {
        Queue<Node> openSet = new LinkedList<>();
        HashSet<Node> closedSet = new HashSet<>(reducedComponents.getSpace().getSize());

        openSet.add(node);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            closedSet.add(current);

            if (current.getPiece().intersection(reducedComponents).cardinality() > 0) {
                continue;
            }

            if (current.getPiece().cardinality() == 0) {
                return true;
            }

            for (var dir : Directions.neighBours()) {
                Node neighbour = current.move(dir);
                if (neighbour == null) {
                    continue;
                }
                if (!closedSet.contains(neighbour)) {
                    openSet.add(neighbour);
                }
            }
        }
        return false;
    }

    @Builder
    @Data
    private static class Node {
        private final BitGrid origin;
        private final Vector3D travelled;

        @Getter(AccessLevel.PRIVATE)
        @Setter(AccessLevel.PRIVATE)
        @EqualsAndHashCode.Exclude
        private BitGrid pieceCached;

        private Node move(Vector3D dir) {

            var travelled = this.travelled.plus(dir);
            return Node.builder()
                    .origin(this.origin)
                    .travelled(travelled)
                    .build();

        }

        public BitGrid getPiece() {
            if (pieceCached != null) {
                return pieceCached;
            }

            BitGrid piece = origin;
            pieceCached = piece.move(travelled);
            return pieceCached;
        }
    }

}
