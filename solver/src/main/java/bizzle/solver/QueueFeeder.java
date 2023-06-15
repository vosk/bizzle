package bizzle.solver;

import bizzle.solver.candidate.PendingCandidateGenerator;
import bizzle.solver.candidate.PendingConstraintCandidate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QueueFeeder {
    public final PendingCandidateGenerator pendingCandidateGenerator;
    private long counter =0;

    public PendingConstraintCandidate feed() {
        counter ++;
        if (counter % 1000 == 0) {
            System.out.println("Gen:" + counter);
        }
        return pendingCandidateGenerator.next();
    }
}
