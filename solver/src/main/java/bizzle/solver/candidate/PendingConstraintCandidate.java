package bizzle.solver.candidate;

import bizzle.solver.Constraint;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

@SuperBuilder(toBuilder = true)
public class PendingConstraintCandidate extends ConstraintCandidate {

    public DecidedConstraintCandidate checkConstraints() {
        List<Constraint.ConstraintOutCome> constraintResult = runIn();
        return DecidedConstraintCandidate.builder()
                .constraintGenerators(getConstraintGenerators())
                .componentGraph(getComponentGraph())
                .constraintOutComes(constraintResult)
                .build();
    }

    private List<Constraint.ConstraintOutCome> runIn() {
        List<Constraint.ConstraintOutCome> outcomes =
                new ArrayList<>(getConstraintGenerators()
                        .stream()
                        .flatMap(generator -> generator.generateAll(getComponentGraph()).stream())
                        .map(Constraint::run)
                        .toList());
        return outcomes;
    }
}
