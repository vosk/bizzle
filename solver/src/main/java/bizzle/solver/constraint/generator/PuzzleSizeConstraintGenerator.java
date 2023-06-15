package bizzle.solver.constraint.generator;

import bizzle.solid.ComponentGraph;
import bizzle.solver.Constraint;
import bizzle.solver.ConstraintGenerator;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorCompletionService;

@RequiredArgsConstructor
public class PuzzleSizeConstraintGenerator implements ConstraintGenerator {
    private final int minSize;
    private final int maxSize;

    @Override
    public Constraint generate(ComponentGraph graph) {
        return () ->
               () -> (graph.getComponents().size() + graph.getIgnored().size()) >= minSize && (graph.getComponents().size() <= maxSize);
    }

    @Override
    public boolean needsToBeRechecked() {
        return true;
    }
}
