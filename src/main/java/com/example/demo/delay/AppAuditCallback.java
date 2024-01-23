package com.example.demo.delay;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

/**
 * 超时处理
 */

@Callback("AppAuditCallback")
@Slf4j
public class AppAuditCallback implements TimeoutCallback {

    /** 超时消息 */
    private static final String MESSAGE = "{}超时, 请重新审核应用";

    @Override
    public void handle(AppDelayMessage message) {
        log.info("@@ 应用超时异常处理, AppDelayMessage : {}", JSON.toJSONString(message));
        // 回调的业务处理
        doAnythings(formatMessage(message.getStage()));
    }

    /**
     * 格式化超时的消息
     * @param stage 超时阶段(COMPILE, TEST)
     * @return 消息内容
     */
    private String formatMessage(AppDelayMessage.Stage stage) {
        // 超时错误消息
        if (AppDelayMessage.Stage.REAL_COMPILE.equals(stage)) {
            return StrUtil.format(MESSAGE, "XXXX业务处理");
        }
        return MESSAGE;
    }

    public void doAnythings(String msg){
        log.info("@@ 超时异常业务处理 : {}", msg);
    }
}
