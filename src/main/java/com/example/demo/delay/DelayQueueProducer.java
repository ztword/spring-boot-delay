package com.example.demo.delay;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.DelayQueue;

/**
 * 延迟消息生产者
 */
@Slf4j
@Component
public class DelayQueueProducer {

    /** 创建延迟消息队列 */
    private static final DelayQueue<DelayMessage> DELAY_QUEUE = new DelayQueue<>();

    /**
     * 消息入队列
     * @param delayMessage 消息内容
     * @return 成功:{@code true}, 失败:{@code false}
     */
    public boolean offer(DelayMessage delayMessage) {
        return DELAY_QUEUE.offer(delayMessage);
    }

    /**
     * 获取延迟消息队列
     * @return {@link DelayQueue<DelayMessage>}
     */
    public DelayQueue<DelayMessage> obtainQueue() {
        return DELAY_QUEUE;
    }
}
