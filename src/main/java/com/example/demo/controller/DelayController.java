package com.example.demo.controller;
/**
 * @create 2024/1/17 17:17
 */

import com.example.demo.delay.AppAuditCallback;
import com.example.demo.delay.AppDelayMessage;
import com.example.demo.delay.AppDelayMessageService;
import com.example.demo.delay.AppTimeOutCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @date 2024年01月17日 17:17
 */
@RestController
public class DelayController {

    @Autowired
    private AppDelayMessageService appDelayMessageService;

    @Autowired
    private AppTimeOutCallback appTimeOutCallback;

    @GetMapping("/testPublishDelay")
    public void testPublishDelay (){
        appDelayMessageService.publish("00000000011111111", 10,
                AppDelayMessage.Type.TYPE1,
                AppDelayMessage.Stage.REAL_COMPILE,
                AppAuditCallback.class);
    }

    @GetMapping("/testChangeDelay")
    public void testChangeDelay (){
        // 修改延迟消息状态
        appDelayMessageService.changeToProcessed("00000000011111111",
                AppDelayMessage.Stage.REAL_COMPILE);

    }
}
