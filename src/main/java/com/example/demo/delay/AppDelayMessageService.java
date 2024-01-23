package com.example.demo.delay;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 应用创建超时消息处理服务接口
 */
public interface AppDelayMessageService extends IService<AppDelayMessage> {

    /**
     * 发布延迟消息
     * @param appId   应用ID
     * @param timeout 超时时长(H)
     * @param type    TYPE_ONE; TYPE_TWO;
     * @param stage   编译:COMPILE; 测试:TEST;
     * @param callback 回调函数
     */
    void publish(String appId, Integer timeout, AppDelayMessage.Type type, AppDelayMessage.Stage stage, Class callback);

    /**
     * 超时回调
     * @param message 消息体
     */
    void callback(AppDelayMessage message);

    /**
     * 修改延迟消息状态[PROCESSED]
     * @param appId 应用ID
     * @param stage 编译:COMPILE; 测试:TEST;
     * @return 成功:true, 失败：false
     */
    boolean changeToProcessed(String appId, AppDelayMessage.Stage stage);
}