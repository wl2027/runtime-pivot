package com.runtime.pivot.plugin.actions.method.test2;

/**
 * 不修改原方法下的异步变同步
 */
public class AsyncMethods {
    public static void a1() {
        new Thread(() -> {
            System.out.println("a1 started");
            try {
                Thread.sleep(1000); // 模拟异步操作
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("a1 completed");
        }).start();
    }

    public static void a2() {
        System.out.println("a2 started");
        try {
            Thread.sleep(500); // 模拟异步操作
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("a2 completed");
    }
}

 class Main {
    public static void main(String[] args) throws InterruptedException {
        // 获取当前线程组
        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
        // 获取活动线程
        Thread[] threads = new Thread[currentGroup.activeCount()];
        currentGroup.enumerate(threads);

        // 调用 a1
        AsyncMethods.a1();

        // 获取新的活动线程，找到 a1 启动的线程
        Thread[] newThreads = new Thread[currentGroup.activeCount()];
        currentGroup.enumerate(newThreads);
        for (Thread t : newThreads) {
            if (!containsThread(threads, t)) {
                // 等待 a1 的线程完成
                t.join();
                break;
            }
        }

        // 调用 a2
        AsyncMethods.a2();
    }

    // 辅助方法，检查线程数组中是否包含指定线程
    private static boolean containsThread(Thread[] threads, Thread thread) {
        for (Thread t : threads) {
            if (t == thread) {
                return true;
            }
        }
        return false;
    }
}
