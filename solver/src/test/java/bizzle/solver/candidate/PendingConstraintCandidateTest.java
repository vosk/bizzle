package bizzle.solver.candidate;

import bizzle.solid.ComponentGraph;
import bizzle.solver.Constraint;
import bizzle.solver.ConstraintGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertFalse;

class PendingConstraintCandidateTest {


    @Test
    public void testFailFast() throws ExecutionException, InterruptedException {
        List<TimedDummyConstraintGenerator> generators = List.of(
                new TimedDummyConstraintGenerator(0, false),
                new TimedDummyConstraintGenerator(25, true)
        );
        PendingConstraintCandidate constraintCandidate = PendingConstraintCandidate.builder()
                .constraintGenerators(generators)
                .build();

        DecidedConstraintCandidate decidedConstraintCandidate = constraintCandidate.checkConstraints();
        assertFalse(decidedConstraintCandidate.isValid());


    }


    private static class TimedDummyConstraintGenerator implements ConstraintGenerator {

        private final int timeout;
        private final boolean result;

        private TimedDummyConstraintGenerator(int timeout, boolean result) {
            this.timeout = timeout;
            this.result = result;
        }

        @Override
        public Constraint generate(ComponentGraph graph) {
            return () -> {
                try {
                    Thread.sleep(timeout * 1000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return () -> result;
            };
        }

        @Override
        public boolean needsToBeRechecked() {
            return true;
        }
    }

}