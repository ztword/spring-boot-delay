package com.example.demo.delay;

import lombok.Data;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延迟消息体
 */
@Data
public class DelayMessage implements Delayed {

    /** 消息内容 */
    private String message;   // 延迟任务中的任务数据
    /** ttl */
    private long ttl;         // 延迟任务到期时间（过期时间）

    /**
     * 构造函数
     * @param message 消息实体
     * @param ttl     延迟时间，单位毫秒
     */
    public DelayMessage(String message, long ttl) {
        setMessage(message);
        this.ttl = System.currentTimeMillis() + ttl;
    }

    /**
     * 获取消息触发剩余时间
     * @param unit the time unit
     * @return {@link long}
     */
    @Override
    public long getDelay(TimeUnit unit) {
        // 计算该任务距离过期还剩多少时间
        long remaining = ttl - System.currentTimeMillis();
        return unit.convert(remaining, TimeUnit.MILLISECONDS);
    }

    /**
     * 比较消息延时时长
     * @param o {@link Delayed}
     * @return 延时时长
     */
    @Override
    public int compareTo(Delayed o) {
        // 比较、排序: 对任务的延时大小进行排序，将延时时间最小的任务放到队列头部
        return (int)(this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }
}
