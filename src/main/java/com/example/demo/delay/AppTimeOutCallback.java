package com.example.demo.delay;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *超时后，业务发生回调处理
 */
@Slf4j
@Component
public class AppTimeOutCallback {

   
    /** 超时消息处理服务 */
    @Autowired
    private AppDelayMessageService messageService;

    /**
     * FOTA审核回调后超时处理
     * @param request 参数
     * @return boolean 返回类型
     */
	public boolean doSotaAuditTimeOutCallback(Map<String, Object> request) {

		String id = request.get("id").toString();
		log.info("@@ 接口回调, request:{}", JSON.toJSONString(request));
		boolean flag;
		try {
			// 修改延迟消息状态
			flag = messageService.changeToProcessed(id, AppDelayMessage.Stage.REAL_COMPILE);
		} catch (Exception ex) {
			log.error("审核超时处理, id:{}, Stage:{}", id, AppDelayMessage.Stage.REAL_COMPILE, ex);
			throw new RuntimeException("审核超时处理", ex);
		}
		return flag;
	}

}
