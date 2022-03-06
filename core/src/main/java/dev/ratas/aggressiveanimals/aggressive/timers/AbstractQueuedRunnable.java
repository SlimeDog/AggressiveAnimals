package dev.ratas.aggressiveanimals.aggressive.timers;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

public abstract class AbstractQueuedRunnable<T> implements Runnable {
    private final long processAtOnce;
    private final Queue<T> currentQueue = new LinkedList<>();
    private final Supplier<Collection<T>> supplier;

    public AbstractQueuedRunnable(long processAtOnce, Supplier<Collection<T>> supplier) {
        this.processAtOnce = processAtOnce;
        this.supplier = supplier;
    }

    public abstract void process(T item);

    @Override
    public void run() {
        if (this.currentQueue.isEmpty()) {
            this.currentQueue.addAll(supplier.get());
        }
        int counter = 0;
        while (counter < processAtOnce && !currentQueue.isEmpty()) {
            process(currentQueue.poll());
            counter++;
        }
    }

}
