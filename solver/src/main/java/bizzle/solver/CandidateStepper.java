package bizzle.solver;

import bizzle.solid.ComponentGraph;
import bizzle.solver.candidate.DecidedConstraintCandidate;
import bizzle.solver.candidate.PendingConstraintCandidate;
import bizzle.solver.constraint.generator.OnePieceIsRemovableConstraintGenerator;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
public class CandidateStepper {
    private final ExecutorService executorService;

    public List<PendingConstraintCandidate> step(DecidedConstraintCandidate candidate) {
//        System.out.println(candidate);
        if (!candidate.isValid()) {
            return candidate.possiblyValidPendingCandidates();
        }
        return candidate.nextStepPendingCandidatesCandidate();
//        List<OnePieceIsRemovableConstraintGenerator.RemoveblePieceConstraintOutcome> list =
//                candidate.getConstraintOutComes().stream()
//                        .filter(outcome -> outcome instanceof OnePieceIsRemovableConstraintGenerator.RemoveblePieceConstraintOutcome)
//                        .map(outcome -> (OnePieceIsRemovableConstraintGenerator.RemoveblePieceConstraintOutcome) outcome)
//                        .toList();
//        return list.stream()
//                .map((outcome) -> {
//                    ComponentGraph newComponentGraph = candidate.getComponentGraph().forgetAbout(outcome.getRemovable());
//                    if (!newComponentGraph.isDone()) {
//                        return (PendingConstraintCandidate)
//                                PendingConstraintCandidate.builder()
//                                        .constraintGenerators(candidate.getConstraintGenerators())
//                                        .componentGraph(candidate.getComponentGraph().forgetAbout(outcome.getRemovable()))
//                                        .build();
//                    } else {
//                        System.out.println("DONE!");
//                        System.out.println("Comp:"+candidate.getComponentGraph().getComponents());
//                        System.out.println("Ignored:"+candidate.getComponentGraph().getIgnored());
//                        throw new RuntimeException("DONE");
////                        return null;
//                    }
//                })
//                .filter(Objects::nonNull)
//                .toList();
    }
}
