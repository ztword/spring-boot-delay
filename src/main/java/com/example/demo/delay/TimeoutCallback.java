package com.example.demo.delay;

/**
 * 回调函数接口
 */
public interface TimeoutCallback {

    /**
     * 提供给实现类完成回调函数处理方法
     * @param message 消息
     */
    void handle(AppDelayMessage message);

}
