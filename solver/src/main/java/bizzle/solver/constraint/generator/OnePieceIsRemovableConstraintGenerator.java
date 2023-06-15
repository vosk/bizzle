package bizzle.solver.constraint.generator;

import bizzle.removal.RemovalPathPlanner;
import bizzle.solid.ComponentGraph;
import bizzle.solid.bitgrid.BitGrid;
import bizzle.solver.Constraint;
import bizzle.solver.ConstraintGenerator;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.ExecutorCompletionService;

@RequiredArgsConstructor
public class OnePieceIsRemovableConstraintGenerator implements ConstraintGenerator {
    private final RemovalPathPlanner planner;

    @Override
    public Constraint generate(ComponentGraph graph) {
        return () -> {
                    if (graph.getComponents().size() == 1) {

                        return new Constraint.NextStepCandidateOutCome() {
                            @Override
                            public ComponentGraph nextCandidate() {
                                return graph.forgetAbout(graph.getComponents().stream().findFirst().get());
                            }

                            @Override
                            public boolean isValid() {
                                return true;
                            }
                        };
                    }
                    List<BitGrid> candidatesForRemoval = graph.getComponents()
                            .stream()
                            .filter(piece -> planner.removePaths(piece, graph.forgetAbout(piece).getComponentsUnion()).size() == 1)
                            .toList();

                    BitGrid removable = null;
                    if (candidatesForRemoval.size() == 1 || (graph.getComponents().size() == 2 && candidatesForRemoval.size() == 2)) {
                        removable = candidatesForRemoval.get(0);
                    }
                    BitGrid finalRemovable = removable;
                    return new Constraint.NextStepCandidateOutCome() {
                        @Override
                        public ComponentGraph nextCandidate() {
                            if (finalRemovable == null) {
                                return null;
                            }
                            return graph.forgetAbout(finalRemovable);
                        }

                        @Override
                        public boolean isValid() {
                            return finalRemovable != null;
                        }
                    };
                };
    }

    @Override
    public boolean needsToBeRechecked() {
        return true;
    }
}
