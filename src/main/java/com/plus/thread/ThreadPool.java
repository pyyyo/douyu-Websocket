package com.plus.thread;

import java.util.concurrent.*;

public class ThreadPool {
    private ThreadPoolExecutor executorService;
    /**
     * Runtime.getRuntime().availableProcessors()方法返回到Java虚拟机的可用的处理器数量，最大线程数需大于等于它，否则报错。
     * @param maxPoolSize 最大线程数
     */
    public ThreadPool(int maxPoolSize) {
        executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                maxPoolSize, 120L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        executorService.allowCoreThreadTimeOut(true);
    }

    public void execute(Runnable task) {
        executorService.execute(task);
    }
}
