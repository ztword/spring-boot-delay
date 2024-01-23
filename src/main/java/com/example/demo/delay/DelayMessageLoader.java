package com.example.demo.delay;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * 延迟消息初始化
 */
@Slf4j
@Component
public class DelayMessageLoader {

    /** 应用创建超时消息处理服务 */
    @Autowired
    private AppDelayMessageService service;
    /** 延迟消息生产者 */
    @Autowired
    private DelayQueueProducer producer;
    /** 延迟消息消费者 */
    @Autowired
    private DelayQueueConsumer consumer;

    /**
     * 消息初期处理
     */
    @PostConstruct
    public void load() {
        log.info("@@ 处理积压消息并重新加入延迟队列");

        // 设置延迟队列
        consumer.setDelayQueue(producer.obtainQueue());
        // 创建并启动延迟队列的消费者线程
        ThreadUtil.newThread(consumer,"Delay-Message-Thread").start();

        // 消息初期处理
        List<AppDelayMessage> messages = service.lambdaQuery()
            .eq(AppDelayMessage::getStatus, AppDelayMessage.Status.PENDING)
            .list();

        // 无积压消息
        if (CollectionUtil.isEmpty(messages)) {
            log.info("@@ 无积压消息，无需处理");
            return;
        }
        // 处理积压消息
        clear(messages);
    }

    /**
     * 清空积压消息
     * @param messages 积压消息列表
     */
    private void clear(List<AppDelayMessage> messages) {
        log.info("@@ 清空积压消息, messages:{}", JSON.toJSONString(messages));

        // 当前系统时间
        long now = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        for (AppDelayMessage message : messages) {
            long resetTTL;
            // ttl(单位: H)
            long ttl = message.getTtl() * 60 * 60 * 1000;
            // 创建消息时间
            LocalDateTime parse = message.getCreateTime();
            long deadline = parse.toInstant(ZoneOffset.UTC).toEpochMilli() + ttl;
            // 是否超时
            if (deadline <= now) {
                message.setRemark("清空积压消息");
                // 已超时，[30~60秒内]进行消费
                resetTTL = RandomUtil.randomInt(30 * 1000, 60 * 1000);
                producer.offer(new DelayMessage(JSON.toJSONString(message), resetTTL));
                log.info("积压的[已超时], 并且, [没有处理]的消息加入延迟队列:{}", JSON.toJSONString(message));
            } else {
                resetTTL = deadline - now;
                producer.offer(new DelayMessage(JSON.toJSONString(message), resetTTL));
                log.info("积压的[没有超时], 并且, [没有处理]的消息加入延迟队列:{}", JSON.toJSONString(message));
            }
        }
    }
}