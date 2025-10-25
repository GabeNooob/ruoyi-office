package cn.iocoder.yudao.module.oa.process.mq;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.mq.redis.core.stream.AbstractRedisStreamMessageListener;
import cn.iocoder.yudao.module.bpm.api.event.BpmEventTypeEnum;
import cn.iocoder.yudao.framework.common.service.FlowBillService;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceInfo;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusMessage;
import cn.iocoder.yudao.module.bpm.api.event.BpmTaskInfo;
import cn.iocoder.yudao.module.oa.enums.OaBillTypeEnum;
import cn.iocoder.yudao.module.oa.process.local.OaBpmEventNotificationListener;
import cn.iocoder.yudao.module.oa.service.OaFlowBillServiceFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * OA 模块统一BPM事件MQ消费者
 * 支持流程实例事件和任务事件的统一处理
 *
 * @author 芋道源码
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "yudao.bpm.notification.mq.enabled", havingValue = "true", matchIfMissing = false)
public class OaBpmEventMqConsumer extends AbstractRedisStreamMessageListener<OaBpmProcessInstanceStatusMessage> {

    @Resource
    private OaFlowBillServiceFactory flowBillServiceFactory;

    @Override
    public void onMessage(OaBpmProcessInstanceStatusMessage message) {
        log.info("[onMessage][MQ消费] 收到BPM事件消息: eventType={}, processInstanceId={}, processDefinitionKey={}",
                message.getEventType() != null ? message.getEventType().getName() : "unknown", 
                message.getProcessInstanceInfo().getProcessInstanceId(), message.getProcessInstanceInfo().getProcessDefinitionKey());

        try {
            // 参数校验
            if (StrUtil.isBlank(message.getProcessInstanceInfo().getProcessDefinitionKey()) || message.getEventType() == null) {
                log.warn("[onMessage] 关键参数为空，跳过处理");
                return;
            }

            // 只处理OA相关流程
            if (!message.getProcessInstanceInfo().getProcessDefinitionKey().startsWith("oa_")) {
                log.debug("[onMessage] 非OA流程，跳过处理: {}", message.getProcessInstanceInfo().getProcessDefinitionKey());
                return;
            }

            // 根据事件类型进行不同处理
            if (message.getEventType().isProcessInstanceEvent()) {
                handleProcessInstanceEvent(message);
            } else if (message.getEventType().isTaskEvent()) {
                handleTaskEvent(message);
            }

        } catch (Exception e) {
            log.error("[onMessage][MQ消费] 处理BPM事件消息失败", e);
            // 这里可以考虑重试或死信队列处理
            throw e; // 重新抛出异常，触发重试机制
        }
    }

    /**
     * 处理流程实例事件
     */
    private void handleProcessInstanceEvent(OaBpmProcessInstanceStatusMessage message) {
        BpmProcessInstanceInfo processInstanceInfo = message.getProcessInstanceInfo();
        String processDefinitionKey = processInstanceInfo.getProcessDefinitionKey();
        String businessKey = processInstanceInfo.getBusinessKey();
        Integer status = processInstanceInfo.getStatus();
        
        if (StrUtil.isBlank(businessKey) || status == null) {
            log.warn("[handleProcessInstanceEvent] businessKey或status为空，跳过处理");
            return;
        }
        
        log.info("[handleProcessInstanceEvent] 处理流程实例事件，eventType: {}, processDefinitionKey: {}, businessKey: {}, status: {}", 
                message.getEventType().getName(), processDefinitionKey, businessKey, status);
        
        try {
            // 通过工厂获取对应的服务实现
            FlowBillService<OaBillTypeEnum> flowBillService = flowBillServiceFactory.getServiceByProcessKey(processDefinitionKey);
            
            // 统一调用接口方法更新流程状态
            flowBillService.updateProcessStatus(businessKey, status);
            
            log.info("[handleProcessInstanceEvent] 流程实例状态更新成功，eventType: {}, processDefinitionKey: {}, businessKey: {}, status: {}", 
                    message.getEventType().getName(), processDefinitionKey, businessKey, status);
                    
        } catch (IllegalArgumentException e) {
            log.debug("[handleProcessInstanceEvent] 未知的OA流程类型: {}", processDefinitionKey);
        } catch (Exception e) {
            log.error("[handleProcessInstanceEvent] 处理流程实例事件失败", e);
            throw e; // 重新抛出异常，触发重试机制
        }
    }

    /**
     * 处理任务事件
     */
    private void handleTaskEvent(OaBpmProcessInstanceStatusMessage message) {
        BpmEventTypeEnum eventType = message.getEventType();
        BpmProcessInstanceInfo processInstanceInfo = message.getProcessInstanceInfo();
        BpmTaskInfo taskInfo = message.getTaskInfo();
        String processDefinitionKey = processInstanceInfo.getProcessDefinitionKey();
        
        log.info("[handleTaskEvent] 处理任务事件，eventType: {}, processDefinitionKey: {}, taskId: {}, taskName: {}, assigneeId: {}, taskResult: {}, taskReason: {}",
                eventType.getName(), processDefinitionKey, taskInfo.getTaskId(), taskInfo.getTaskName(),
                taskInfo.getAssigneeId(), taskInfo.getTaskResult(), taskInfo.getTaskReason());

        // 根据不同的任务事件类型进行处理
        switch (eventType) {
            case TASK_CREATED:
                handleTaskCreated(message);
                break;
            case TASK_APPROVED:
                handleTaskApproved(message);
                break;
            case TASK_REJECTED:
                handleTaskRejected(message);
                break;
            case TASK_WITHDRAWN:
                handleTaskWithdrawn(message);
                break;
            case TASK_TRANSFERRED:
                handleTaskTransferred(message);
                break;
            case TASK_DELEGATED:
                handleTaskDelegated(message);
                break;
            default:
                log.debug("[handleTaskEvent] 暂不处理的任务事件类型: {}", eventType.getName());
                break;
        }
    }

    /**
     * 处理任务创建事件
     */
    private void handleTaskCreated(OaBpmProcessInstanceStatusMessage message) {
        BpmTaskInfo taskInfo = message.getTaskInfo();
        log.info("[handleTaskCreated] 任务创建通知，taskId: {}, taskName: {}, assigneeId: {}",
                taskInfo.getTaskId(), taskInfo.getTaskName(), taskInfo.getAssigneeId());
        
        // 这里可以实现具体的业务逻辑，比如：
        // 1. 发送待办提醒
        // 2. 更新业务单据状态
        // 3. 记录操作日志等
        

    }

    /**
     * 处理任务审批通过事件
     */
    private void handleTaskApproved(OaBpmProcessInstanceStatusMessage message) {
        BpmTaskInfo taskInfo = message.getTaskInfo();
        log.info("[handleTaskApproved] 任务审批通过，taskId: {}, assigneeId: {}, reason: {}",
                taskInfo.getTaskId(), taskInfo.getAssigneeId(), taskInfo.getTaskReason());
        
        // 这里可以实现具体的业务逻辑，比如：
        // 1. 更新业务单据审批记录
        // 2. 发送审批结果通知
        // 3. 触发下游业务流程等
        

    }

    /**
     * 处理任务审批拒绝事件
     */
    private void handleTaskRejected(OaBpmProcessInstanceStatusMessage message) {
        BpmTaskInfo taskInfo = message.getTaskInfo();
        log.info("[handleTaskRejected] 任务审批拒绝，taskId: {}, assigneeId: {}, reason: {}",
                taskInfo.getTaskId(), taskInfo.getAssigneeId(), taskInfo.getTaskReason());
        
        // 这里可以实现具体的业务逻辑，比如：
        // 1. 更新业务单据为拒绝状态
        // 2. 发送拒绝通知给申请人
        // 3. 记录拒绝原因等
        

    }

    /**
     * 处理任务撤回事件
     */
    private void handleTaskWithdrawn(OaBpmProcessInstanceStatusMessage message) {
        BpmTaskInfo taskInfo = message.getTaskInfo();
        log.info("[handleTaskWithdrawn] 任务撤回，taskId: {}, reason: {}",
                taskInfo.getTaskId(), taskInfo.getTaskReason());
        
        // 这里可以实现具体的业务逻辑，比如：
        // 1. 更新业务单据为撤回状态
        // 2. 发送撤回通知
        // 3. 清理相关数据等
        

    }

    /**
     * 处理任务转办事件
     */
    private void handleTaskTransferred(OaBpmProcessInstanceStatusMessage message) {
        BpmTaskInfo taskInfo = message.getTaskInfo();
        log.info("[handleTaskTransferred] 任务转办，taskId: {}, assigneeId: {}",
                taskInfo.getTaskId(), taskInfo.getAssigneeId());
        
        // 这里可以实现具体的业务逻辑，比如：
        // 1. 发送转办通知
        // 2. 更新审批人信息等
        

    }

    /**
     * 处理任务委派事件
     */
    private void handleTaskDelegated(OaBpmProcessInstanceStatusMessage message) {
        BpmTaskInfo taskInfo = message.getTaskInfo();
        log.info("[handleTaskDelegated] 任务委派，taskId: {}, assigneeId: {}",
                taskInfo.getTaskId(), taskInfo.getAssigneeId());
        
        // 这里可以实现具体的业务逻辑，比如：
        // 1. 发送委派通知
        // 2. 更新委派人信息等

    }

}
