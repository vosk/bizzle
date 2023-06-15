package bizzle.solver;

import bizzle.solid.ComponentGraph;

import java.util.Collection;
import java.util.Collections;

public interface ConstraintGenerator {

    Constraint generate(ComponentGraph graph);

    boolean needsToBeRechecked();

    default Collection<Constraint> generateAll(ComponentGraph graph) {
        return Collections.singleton(generate(graph));
    }
}
