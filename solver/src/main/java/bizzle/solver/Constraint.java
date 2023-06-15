package bizzle.solver;

import bizzle.solid.ComponentGraph;
import bizzle.solver.candidate.PendingConstraintCandidate;

import java.util.List;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

public interface Constraint {

    ConstraintOutCome run();

    interface ConstraintOutCome {
        boolean isValid();
    }

    interface PossiblyValidCandidatesOutCome extends  ConstraintOutCome {
        List<ComponentGraph> possibleFollowUps();
    }

    interface NextStepCandidateOutCome extends  ConstraintOutCome {
        ComponentGraph nextCandidate();
    }
}
