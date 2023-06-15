package bizzle.solver;

import bizzle.solver.candidate.PendingConstraintCandidate;

import java.util.concurrent.LinkedBlockingQueue;

public class SolverQueue {

    private final LinkedBlockingQueue<PendingConstraintCandidate> queue;
    private final QueueFeeder feeder;
    private final int capacity;

    private long counter = 0;
    private boolean killed;


    public SolverQueue(int capacity, QueueFeeder feeder) {
        this.capacity = capacity;
        this.feeder = feeder;
        queue = new LinkedBlockingQueue<>();
        killed = false;
    }
//    public void addWaiting(PendingConstraintCandidate candidate) throws InterruptedException {
//        lock.lock();
//        while( queue.size()>capacity/2) {
//            notHalfFull.await();
//        }
//        lock.unlock();
//        doAdd(candidate);
//    }

    public void addBlock(PendingConstraintCandidate candidate) throws InterruptedException {
//        lock.lock();
        doAdd(candidate);
//        lock.unlock();
    }

    public PendingConstraintCandidate getNext() throws InterruptedException {
        if (killed) {
            throw new RuntimeException("killed");
        }
        while (queue.size() < capacity / 2) {
            addBlock(feeder.feed());
        }

        PendingConstraintCandidate take = queue.take();
        return take;
    }

    public int size() {
        return queue.size();
    }

    private void doAdd(PendingConstraintCandidate candidate) throws InterruptedException {
        counter++;
        if (counter % 100000 == 0) {
            System.out.println("Q:" + counter + " S" + queue.size());
        }
        queue.put(candidate);
    }

    public void killIt() {
        killed = true;
    }
}
