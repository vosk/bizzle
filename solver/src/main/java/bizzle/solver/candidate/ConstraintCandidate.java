package bizzle.solver.candidate;

import bizzle.solver.ConstraintGenerator;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import bizzle.solid.ComponentGraph;

import java.util.Collection;

@SuperBuilder(toBuilder = true)
@Getter
public class ConstraintCandidate {
    private final ComponentGraph componentGraph;
    private final Collection<? extends ConstraintGenerator> constraintGenerators;
}
