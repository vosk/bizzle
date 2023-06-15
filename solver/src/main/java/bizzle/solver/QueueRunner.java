package bizzle.solver;

import bizzle.solver.candidate.DecidedConstraintCandidate;
import bizzle.solver.candidate.PendingConstraintCandidate;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


@RequiredArgsConstructor
public class QueueRunner {

    private final ExecutorService executorService;
    private final SolverQueue queue;
    private final CandidateStepper stepper;

    public Future prime() {
        return executorService.submit( () -> {
            while (true) {
                PendingConstraintCandidate next = queue.getNext();
                DecidedConstraintCandidate decidedConstraintCandidate = next.checkConstraints();
                List<PendingConstraintCandidate> nextCandidates = stepper.step(decidedConstraintCandidate);
//                System.out.println(nextCandidates.size());
                nextCandidates.forEach(n -> {
                    try {
                        if (n.getComponentGraph().isDone()) {
                            System.out.println("DONE!");
                            System.out.println("Comp:"+n.getComponentGraph().getComponents());
                            System.out.println("Ignored:"+n.getComponentGraph().getIgnored());
                            SolidExport.save(n.getComponentGraph());
//                            queue.killIt();;
//                            throw new RuntimeException("DONE");
                        } else {
                            queue.addBlock(n);
                        }

                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });

            }
        });

    }
}
