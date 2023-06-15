package bizzle.solver;

import bizzle.removal.PieceMerger;
import bizzle.removal.RemovalPathPlanner;
import bizzle.solid.ComponentGraph;
import bizzle.solid.ConnectedComponentsScanner;
import bizzle.solid.bitgrid.BitGrid;
import bizzle.solid.bitgrid.BitGridFactory;
import bizzle.solid.bitgrid.RandomBitGridFactory;
import bizzle.solver.candidate.PendingCandidateGenerator;
import bizzle.solver.candidate.PendingConstraintCandidate;
import bizzle.solver.constraint.generator.OnePieceIsRemovableConstraintGenerator;
import bizzle.solver.constraint.generator.PieceMaxSizeConstraintGenerator;
import bizzle.solver.constraint.generator.PieceMinSizeConstraintGenerator;
import bizzle.solver.constraint.generator.PuzzleSizeConstraintGenerator;
import bizzle.space.Space;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] argv) throws InterruptedException {

        BitGridFactory bitGridFactory = new BitGridFactory(new Space(5, 5, 5));
        Random random = new Random(1994);

//
//        String OTHER = "11110110";
//        long other = new BigInteger(new StringBuilder(OTHER).reverse().toString(), 2).longValue();
//        BitGrid otherGrid = bitGridFactory.ofLongAt(0, other);
//        BitGrid pieceGrid = otherGrid.complement();
//        ComponentGraph componentGraph = new ComponentGraph(bitGridFactory, List.of(otherGrid, pieceGrid));

        RandomBitGridFactory randomBitGridFactory = new RandomBitGridFactory(random, bitGridFactory);
        RemovalPathPlanner removalPathPlanner = new RemovalPathPlanner();
        PieceMerger merger = new PieceMerger();
        OnePieceIsRemovableConstraintGenerator onePieceIsRemovableConstraintGenerator = new OnePieceIsRemovableConstraintGenerator(removalPathPlanner);
        PieceMaxSizeConstraintGenerator pieceMaxSizeConstraintGenerator = new PieceMaxSizeConstraintGenerator(24);

        PieceMinSizeConstraintGenerator pieceMinSizeConstraintGenerator = new PieceMinSizeConstraintGenerator(5, merger);
        PuzzleSizeConstraintGenerator puzzleSizeConstraintGenerator = new PuzzleSizeConstraintGenerator(7, 21);
        PendingCandidateGenerator pendingCandidateGenerator =
                () -> {
                    BitGrid[] data = null;
                    synchronized (randomBitGridFactory) {
                        data = new BitGrid[]{randomBitGridFactory.generateNext(), randomBitGridFactory.generateNext()};
                    }
                    return PendingConstraintCandidate.builder()
                            .constraintGenerators(
                                    List.of(pieceMaxSizeConstraintGenerator,
                                            puzzleSizeConstraintGenerator,
                                            onePieceIsRemovableConstraintGenerator,
                                            pieceMinSizeConstraintGenerator
                                    ))
                            .componentGraph(
                                    new ComponentGraph(bitGridFactory,
                                            new ConnectedComponentsScanner(data).getComponents(bitGridFactory)
                                    )
                            )
//                        .componentGraph(componentGraph)
                            .build();
                };
        QueueFeeder feeder = new QueueFeeder(pendingCandidateGenerator);
        SolverQueue solverQueue = new SolverQueue(128, feeder);


        try (ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2)) {
            CandidateStepper candidateStepper = new CandidateStepper(executorService);
            List<Future> runners = IntStream.range(0, 12)
                    .boxed()
                    .map((i) -> new QueueRunner(executorService, solverQueue, candidateStepper).prime())
                    .toList();
            while (true) {
                runners
                        .forEach(runner -> {
                             if (runner.isDone()) {
                                 try {
                                     runner.get();
                                 } catch (Exception e) {
                                     throw new RuntimeException(e.getCause());
                                 }
                             }
                        });
                Thread.sleep(100);
            }
        }
    }
}
