package com.example.demo.delay;

import org.springframework.context.ApplicationEvent;

/**
 * 延迟消息超时事件
 */
public class InvokeTimeoutEvent extends ApplicationEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     * @param source the object on which the event initially occurred or with which the event is associated (never
     *               {@code null})
     */
    public InvokeTimeoutEvent(Object source) {
        super(source);
    }
}
