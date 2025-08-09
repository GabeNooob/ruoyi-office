package cn.iocoder.yudao.module.bpm.api.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 流程实例状态变化的统一消息
 * 支持 ApplicationEvent、MQ、Feign 等多种通知方式
 *
 * @author 芋道源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmProcessInstanceStatusMessage {

    /**
     * 流程实例的编号
     */
    @NotNull(message = "流程实例的编号不能为空")
    private String processInstanceId;

    /**
     * 流程实例的 key
     */
    @NotNull(message = "流程实例的 key 不能为空")
    private String processDefinitionKey;

    /**
     * 流程实例的结果
     */
    @NotNull(message = "流程实例的状态不能为空")
    private Integer status;

    /**
     * 流程实例对应的业务标识
     * 例如说，请假单ID、用车申请单ID
     */
    private String businessKey;

    /**
     * 事件发生时间
     */
    private LocalDateTime eventTime;

    /**
     * 租户ID（多租户支持）
     */
    private String tenantId;

    /**
     * 流程发起人ID
     */
    private String startUserId;

    /**
     * 扩展信息
     */
    private String extInfo;

} 