package com.amd.aparapi;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.function.IntConsumer;

public class JavaThreadPoolDevice extends Device {
    static final int threads = Runtime.getRuntime().availableProcessors();
    void wait(CyclicBarrier barrier) {
        try {
            barrier.await();
        } catch (InterruptedException ex) {
        } catch (BrokenBarrierException ex) {
        }
    }

    public Device forEach(int _range, final IntConsumer _intConsumer) {
        return(forEach(0, _range, threads, _intConsumer));
    }
    public Device forEach(int _from, int _to, int _threads, final IntConsumer _intConsumer) {


        int range = _to-_from;
        if (range < _threads) {
            for (int t = 0; t < range; t++) {
                _intConsumer.accept(t+_from);
            }
        } else {
            final CyclicBarrier barrier = new CyclicBarrier(_threads + 1);
            for (int t = 0; t < _threads; t++) {
                int finalt = t;
                new Thread(() -> {
                    for (int x = finalt * (range / _threads); x < (finalt + 1) * (range / _threads); x++) {
                        _intConsumer.accept(x+_from);
                    }
                    wait(barrier);
                }).start();
            }
            wait(barrier);
        }
        return (this);
    }

}
