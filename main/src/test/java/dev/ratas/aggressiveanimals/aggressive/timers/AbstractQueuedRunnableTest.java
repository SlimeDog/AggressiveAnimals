package dev.ratas.aggressiveanimals.aggressive.timers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AbstractQueuedRunnableTest {
    private AtomicInteger times;
    private Runnable runnable;

    @BeforeEach
    public void setup() {
        times = new AtomicInteger(0);
    }

    @SafeVarargs
    private <T> void assertGetsCalled(long atATime, T... vals) {
        List<T> list = new ArrayList<>();
        list.addAll(Arrays.asList(vals));
        runnable = new MockQueuedRunnable<>(atATime, () -> list,
                i -> {
                    Assertions.assertEquals(i, vals[times.getAndIncrement() % vals.length]);
                });
        runnable.run();
        if (atATime > vals.length) {
            Assertions.assertEquals(vals.length, times.get());
        } else {
            Assertions.assertEquals(atATime, times.get());
        }
    }

    @Test
    public void test_noElementNoProcess() {
        assertGetsCalled(100);
        Assertions.assertEquals(0, times.get(), "Should not be called with no elements");
    }

    @Test
    public void test_processesElement() {
        assertGetsCalled(1, 10);
        Assertions.assertEquals(1, times.get(), "Should be called with once");
    }

    @Test
    public void test_processesTwoElements() {
        assertGetsCalled(3, "str1", "str2");
        Assertions.assertEquals(2, times.get(), "Should be called with once");
    }

    @Test
    public void test_processesNoMoreThanAtATimeElements_1() {
        assertGetsCalled(1, "str1", "str2");
        Assertions.assertEquals(1, times.get(), "Should be called no more than once in this case");
    }

    @Test
    public void test_processesNoMoreThanAtATimeElements_3() {
        assertGetsCalled(3, "str1", "str2", "str3", "str4", "str5");
        Assertions.assertEquals(3, times.get(), "Should be called three times in this case");
    }

    @Test
    public void test_processesNoMoreThanAtATimeElements_secondTime() {
        assertGetsCalled(3, "str1", "str2", "str3", "str4", "str5");
        Assertions.assertEquals(3, times.get(), "Should be called three times in this case");
        runnable.run();
        Assertions.assertEquals(5, times.get(), "Should be called two times in this case");
    }

    @Test
    public void test_processesNoMoreThanAtATimeElements_thirdTimeStartsOver() {
        assertGetsCalled(3, "str1", "str2", "str3", "str4", "str5");
        Assertions.assertEquals(3, times.get(), "Should be called three times in this case");
        runnable.run();
        Assertions.assertEquals(5, times.get(), "Should be called two extra times in this case");
        runnable.run();
        Assertions.assertEquals(8, times.get(), "Should be called three extra times - started over");
    }

    public static class MockQueuedRunnable<T> extends AbstractQueuedRunnable<T> {
        private final Consumer<T> processConsumer;

        public MockQueuedRunnable(long processAtOnce, Supplier<Collection<T>> supplier, Consumer<T> processConsumer) {
            super(processAtOnce, supplier);
            this.processConsumer = processConsumer;
        }

        @Override
        public void process(T item) {
            processConsumer.accept(item);
        }

    }

}
