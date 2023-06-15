package bizzle.solver.constraint.generator;

import bizzle.solid.ComponentGraph;
import bizzle.solver.Constraint;
import bizzle.solver.ConstraintGenerator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PieceMaxSizeConstraintGenerator implements ConstraintGenerator {
    private final int maxSize;

    @Override
    public Constraint generate(ComponentGraph graph) {
        return () ->
                () -> {
                    boolean match = graph
                            .getComponents()
                            .stream()
                            .anyMatch(piece -> {
                                var cardinality = piece.cardinality();
                                return cardinality > maxSize;
                            });
//                        if (!match) {
//                            System.out.println("size good");
//                        }
                    return !match;
                };
    }

    @Override
    public boolean needsToBeRechecked() {
        return true;
    }
}
