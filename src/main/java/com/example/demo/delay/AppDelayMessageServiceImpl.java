package com.example.demo.delay;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.delay.mapper.AppDelayMessageMapper;
import com.example.demo.old.Invoke111TimeoutCallbackEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 应用创建超时消息处理服务实现类
 */
@Slf4j
@Service
public class AppDelayMessageServiceImpl extends ServiceImpl<AppDelayMessageMapper, AppDelayMessage>
    implements AppDelayMessageService, ApplicationEventPublisherAware {

    /** ApplicationEventPublisher */
    private ApplicationEventPublisher eventPublisher;

    /**
     * 注入事件发布器
     * @param eventPublisher event publisher to be used by this object
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * 发布延迟消息
     * @param appId    应用ID
     * @param timeout  超时时长(H)
     * @param type     FOTA; SOTA;
     * @param stage    编译工程:COMPILE; 测试配置:TEST;
     * @param callback 回调函数
     */
    @Async("toolThreadPool")
    @Override
    public void publish(String appId, Integer timeout, AppDelayMessage.Type type, AppDelayMessage.Stage stage,
        Class callback) {
        log.info("@@ 发布延迟消息, appId:{}, type:{}, stage:{}", appId, type, stage);
        AppDelayMessage message = AppDelayMessage.builder()
            .appId(appId)
            .ttl(timeout)
            .type(type)
            .stage(stage)
            .callback(callback.getSimpleName())
            .build();
        // 发布延时消息时间事件
        InvokeTimeoutEvent event = new InvokeTimeoutEvent(message);
        eventPublisher.publishEvent(event);
    }

    /**
     * 超时回调
     * @param message 消息体
     */
    @Async("toolThreadPool")
    @Override
    public void callback(AppDelayMessage message) {
        log.info("@@ 超时回调函数处理, message:{}", JSON.toJSONString(message));
        // 发布延时消息时间事件
        Invoke111TimeoutCallbackEvent event = new Invoke111TimeoutCallbackEvent(message);
        eventPublisher.publishEvent(event);
    }

    /**
     * 修改延迟消息状态
     * @param appId 应用ID
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public boolean changeToProcessed(String appId, AppDelayMessage.Stage stage) {
        log.info("@@ 修改延迟消息状态, appId:{}, Stage:{}", appId, stage);

        // 获取消息
        AppDelayMessage one = lambdaQuery()
            .eq(AppDelayMessage::getAppId, appId)
            .eq(AppDelayMessage::getStage, stage)
            .orderByDesc(AppDelayMessage::getCreateTime)
            .last("limit 1")
            .one();

        // 无消息数据跳过处理
        if (Objects.isNull(one)) {
            return Boolean.TRUE;
        }

        // 消息已超时
        if (AppDelayMessage.Status.TIMEOUT.equals(one.getStatus())) {
            log.error("@@ 接口调用超时, 消息内容:{}", JSON.toJSONString(one));
            return Boolean.FALSE;
        }

        // 修改状态为[以处理]
        lambdaUpdate()
            .set(AppDelayMessage::getStatus, AppDelayMessage.Status.PROCESSED)
            .set(AppDelayMessage::getModifyTime, LocalDateTime.now())
            .eq(AppDelayMessage::getId, one.getId())
            .update();
        return Boolean.TRUE;
    }
}
