package com.example.demo.delay;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 应用创建超时处理消息表
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName(value = "app_delay_message")
public class AppDelayMessage {
    /** ID */
    private String id;
    /** 应用ID */
    private String appId;
    /** 超时时长(H) */
    private Integer ttl;
    /** type1, type2*/
    private Type type;
    /** 编译:COMPILE; 测试:TEST; */
    private Stage stage;
    /** 待处理:PENDING; 已处理:PROCESSED; 超时:TIMEOUT; 无效:INVALID; */
    private Status status;
    /** 备考 */
    private String remark;
    /** 回调函数 */
    private String callback;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 修改时间 */
    private LocalDateTime modifyTime;
    /** 删除标 */
    private String deleteFlg;

    /** 场景类型 */
    public enum Type {
        /** 场景1 */
        TYPE1,
        /** 场景2 */
        TYPE2
    }

    /** 阶段 */
    public enum Stage {
        /** 编译 */
        REAL_COMPILE
    }

    /** 状态 */
    public enum Status {
        /** 待处理 */
        PENDING,
        /** 已处理 */
        PROCESSED,
        /** 超时 */
        TIMEOUT,
        /** 无效 */
        INVALID
    }
}
