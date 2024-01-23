package com.example.demo.delay;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 延迟消息超时事件监听器
 */
@Component
public class DelayMessageListener {

    /** 延迟消息生产者 */
    @Autowired
    private DelayQueueProducer producer;
    /** 应用创建超时消息处理服务接口 */
    @Autowired
    private AppDelayMessageService service;

    /**
     * 事件监听处理方法
     * @param event {@link InvokeTimeoutEvent}
     */
    @EventListener
    public void onApplicationEvent(InvokeTimeoutEvent event) {
        // 监听延迟消息触发事件
        AppDelayMessage source = (AppDelayMessage)event.getSource();
        if (service.save(source)) {
            long ttl = 1000;
            if (Objects.nonNull(source.getTtl())) {
                ttl = source.getTtl() * 1000;
            }
            // 转换为毫秒
//            long ttl = 2 * 60 * 60 * 1000;
//            if (Objects.nonNull(source.getTtl())) {
//                ttl = source.getTtl() * 60 * 60 * 1000;
//            }
            producer.offer(new DelayMessage(JSON.toJSONString(source), ttl));
        }
    }
}
