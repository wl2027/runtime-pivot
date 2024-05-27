package com.runtime.pivot.agent.tools;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实现一个可以接收流式对象并监控它们是否被垃圾回收的系统，我
 * 们可以创建一个单独的线程来处理所有的 PhantomReference 和 ReferenceQueue。
 * 这样，我们只需要一个线程来监控所有的对象，而不是每次传入一个对象时启动一个新线程。
 * 这种方法更高效，资源占用更低。
 */
public class ObjectFinalizer {

    private static final ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
    private static final Map<PhantomReference<?>, Runnable> referenceRunnableMap = new ConcurrentHashMap<>();

    static {
        Thread monitorThread = new Thread(() -> {
            while (true) {
                try {
                    // Remove blocks until a reference is available
                    Reference<?> ref = referenceQueue.remove();
                    Runnable runnable = referenceRunnableMap.remove(ref);
                    if (runnable != null) {
                        runnable.run();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    public static void monitorObject(Object obj, Runnable runnable) {
        PhantomReference<Object> phantomReference = new PhantomReference<>(obj, referenceQueue);
        referenceRunnableMap.put(phantomReference, runnable);
    }

    public static void main(String[] args) throws InterruptedException {
        // Example usage
        Object myObject1 = new Object();
        Runnable myRunnable1 = () -> System.out.println("myObject1 has been garbage collected!");

        Object myObject2 = new Object();
        Runnable myRunnable2 = () -> System.out.println("myObject2 has been garbage collected!");

        // Monitor the objects
        monitorObject(myObject1, myRunnable1);
        monitorObject(myObject2, myRunnable2);

        // Remove references to the objects to make them eligible for GC
        myObject1 = null;
        myObject2 = null;

        // Suggest garbage collection
        System.gc();

        // Wait some time to see the result
        while (true){

        }
    }
}

