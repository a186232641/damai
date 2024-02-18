package com.damai.core;

import com.damai.context.DelayQueuePart;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: 极度真实还原大麦网高并发实战项目。 添加 阿宽不是程序员 微信，添加时备注 damai 来获取项目的完整资料 
 * @description: 延迟队列 消费
 * @author: 阿宽不是程序员
 **/
@Slf4j
public class DelayConsumerQueue extends DelayBaseQueue{
    
    private final AtomicInteger threadCount = new AtomicInteger(1);
    
    private final ExecutorService executorService;
    
    private final AtomicBoolean runFlag = new AtomicBoolean(false);
    
    private final ConsumerTask consumerTask;
    
    public DelayConsumerQueue(DelayQueuePart delayQueuePart, String relTopic){
        super(delayQueuePart.getRedissonClient(),relTopic);
        this.executorService = new ThreadPoolExecutor(
                delayQueuePart.getDelayQueueProperties().getCorePoolSize(),
                delayQueuePart.getDelayQueueProperties().getMaximumPoolSize(),
                delayQueuePart.getDelayQueueProperties().getKeepAliveTime(),
                delayQueuePart.getDelayQueueProperties().getUnit(),
                new LinkedBlockingQueue<>(delayQueuePart.getDelayQueueProperties().getWorkQueueSize()),
                r -> new Thread(Thread.currentThread().getThreadGroup(), r, 
                        "delay-queue-consume-thread-" + threadCount.getAndIncrement()));
        this.consumerTask = delayQueuePart.getConsumerTask();
    }
    
    public synchronized void listenStart(){
        if (!runFlag.get()) {
            runFlag.set(true);
            new Thread(Thread.currentThread().getThreadGroup(), () -> {
                while (!Thread.interrupted()) {
                    try {
                        assert blockingQueue != null;
                        String content = blockingQueue.take();
                        executorService.execute(() -> {
                            try {
                                consumerTask.execute(content);
                            }catch (Exception e) {
                                log.error("consumer execute error",e);
                            }
                        });
                    } catch (InterruptedException e) {
                        destroy(executorService);
                    } catch (Throwable e) {
                        log.error("blockingQueue take error",e);
                    }
                }
            }, "listen-thread").start();
        }
    }
    
    public void destroy(ExecutorService executorService) {
        try {
            if (Objects.nonNull(executorService)) {
                executorService.shutdown();
            }
        } catch (Exception e) {
            log.error("destroy error",e);
        }
    }
}
