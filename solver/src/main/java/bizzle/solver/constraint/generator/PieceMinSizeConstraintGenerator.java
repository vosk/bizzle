package bizzle.solver.constraint.generator;

import bizzle.removal.PieceMerger;
import bizzle.solid.ComponentGraph;
import bizzle.solid.bitgrid.BitGrid;
import bizzle.solver.Constraint;
import bizzle.solver.ConstraintGenerator;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class PieceMinSizeConstraintGenerator implements ConstraintGenerator {
    private final int minSize;
    private final PieceMerger merger;

    @Override
    public Constraint generate(ComponentGraph graph) {
        return () -> {
            Optional<BitGrid> smallPiece = graph
                    .getComponents()
                    .stream()
                    .filter(piece -> {
                        var cardinality = piece.cardinality();
                        return cardinality < minSize;
                    })
                    .findFirst();

            List<ComponentGraph> merges = Collections.emptyList();
            if (smallPiece.isPresent()) {
                merges = merger.possibleMerges(smallPiece.get(), graph);

            }
//            System.out.println(merges.size());
            List<ComponentGraph> finalMerges = merges;
            return new Constraint.PossiblyValidCandidatesOutCome() {
                @Override
                public List<ComponentGraph> possibleFollowUps() {
                    return finalMerges;
                }

                @Override
                public boolean isValid() {
                    return finalMerges.isEmpty();
                }
            };
        };
    }

    @Override
    public boolean needsToBeRechecked() {
        return true;
    }
}
