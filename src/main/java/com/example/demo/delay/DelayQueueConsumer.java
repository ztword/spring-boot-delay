package com.example.demo.delay;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.DelayQueue;

/**
 * 延迟消息消费者
 */
@Slf4j
@Component
public class DelayQueueConsumer implements Runnable {

    /** 延迟队列 */
    private DelayQueue<DelayMessage> delayQueue;

    /**
     * 设置延迟队列
     * @param delayQueue 延迟队列
     */
    public void setDelayQueue(DelayQueue<DelayMessage> delayQueue) {
        this.delayQueue = delayQueue;
    }

    /** 超时消息处理服务 */
    @Autowired
    private AppDelayMessageService service;

    @Override
    public void run() {
        while (true) {
            try {
                log.info("@@ 启动异步线程 [{}] 消费以超时的消息", Thread.currentThread().getName());
                // 如果暂时没有过期消息或者队列为空, 则 take 方法会被阻塞, 直到有过期的消息为止
                DelayMessage delayMessage = delayQueue.take();
                AppDelayMessage message = JSON.parseObject(delayMessage.getMessage(), AppDelayMessage.class);
                // 处理 TIMEOUT 异常
                handleTimeoutError(message);
                log.info("@@ 以消费消息:{}", delayMessage.getMessage());
            } catch (InterruptedException e) {
                log.error("@@ 线程 [{}] 消费消息异常", Thread.currentThread().getName(), e);
            }
        }
    }

    /**
     * 应用超时错误处理
     * @param message 消息内容
     */
    @Transactional
    public void handleTimeoutError(AppDelayMessage message) {
        log.info("@@ 处理超时错误, AppDelayMessage:{}", message);

        // 更新消息状态 [PENDING -> TIMEOUT]
        boolean update = service.lambdaUpdate()
            .set(AppDelayMessage::getStatus, AppDelayMessage.Status.TIMEOUT)
            .set(AppDelayMessage::getModifyTime, LocalDateTime.now())
            .eq(AppDelayMessage::getId, message.getId())
            .eq(AppDelayMessage::getStatus, AppDelayMessage.Status.PENDING)
            .update();

        if (update) {
            log.info("@@ 处理超时调用回调函数, message:{}", JSON.toJSONString(message));
            service.callback(message);
        }
    }
}
