package bizzle.solver.candidate;

import bizzle.solver.Constraint;
import bizzle.solver.ConstraintGenerator;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@SuperBuilder(toBuilder = true)
@Getter
public class DecidedConstraintCandidate extends ConstraintCandidate {
    private final List<Constraint.ConstraintOutCome> constraintOutComes;

    public boolean isValid() {
        boolean anyInvalid = constraintOutComes.stream().anyMatch(constraintOutCome -> !constraintOutCome.isValid());
        return !anyInvalid;
    }

    public List<PendingConstraintCandidate> possiblyValidPendingCandidates() {
        Boolean nonCandidateOutcome = constraintOutComes.stream()
                .filter(outcome -> !(outcome instanceof Constraint.PossiblyValidCandidatesOutCome)
                        && !(outcome instanceof Constraint.NextStepCandidateOutCome)
                )
                .map(Constraint.ConstraintOutCome::isValid)
                .reduce(true, (a, b) -> a && b);
        if (!nonCandidateOutcome) {
            return Collections.emptyList();
        }

        return constraintOutComes.stream()
                .filter(outcome -> outcome instanceof Constraint.PossiblyValidCandidatesOutCome)
                .map(outcome -> (Constraint.PossiblyValidCandidatesOutCome) outcome)
                .flatMap(possiblyValidCandidatesOutCome -> possiblyValidCandidatesOutCome.possibleFollowUps().stream())
                .map(componentGraph ->
                        (PendingConstraintCandidate)
                                PendingConstraintCandidate.builder()
                                        .constraintGenerators(getRepeatedConstraintGenerators())
                                        .componentGraph(componentGraph)
                                        .build()
                )
                .toList();
    }

    private Collection<? extends ConstraintGenerator> getRepeatedConstraintGenerators() {
        return getConstraintGenerators()
                .stream()
                .filter(ConstraintGenerator::needsToBeRechecked)
                .toList();
    }

    public List<PendingConstraintCandidate> nextStepPendingCandidatesCandidate() {
        return constraintOutComes.stream()
                .filter(outcome -> outcome instanceof Constraint.NextStepCandidateOutCome)
                .map(outcome -> (Constraint.NextStepCandidateOutCome) outcome)
                .map(Constraint.NextStepCandidateOutCome::nextCandidate)
                .filter(Objects::nonNull)
                .map(componentGraph ->
                        (PendingConstraintCandidate)
                                PendingConstraintCandidate.builder()
                                        .constraintGenerators(getRepeatedConstraintGenerators())
                                        .componentGraph(componentGraph)
                                        .build()
                )
                .toList();
    }
}
