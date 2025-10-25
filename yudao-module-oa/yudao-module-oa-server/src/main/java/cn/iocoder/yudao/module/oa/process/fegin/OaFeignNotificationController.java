package cn.iocoder.yudao.module.oa.process.fegin;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceInfo;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusMessage;
import cn.iocoder.yudao.module.bpm.api.event.BpmEventTypeEnum;
import cn.iocoder.yudao.module.bpm.api.event.BpmTaskInfo;
import cn.iocoder.yudao.module.oa.enums.ApiConstants;
import cn.iocoder.yudao.framework.common.service.FlowBillService;
import cn.iocoder.yudao.module.oa.enums.OaBillTypeEnum;
import cn.iocoder.yudao.module.oa.service.OaFlowBillServiceFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * OA 流程回调 Controller
 * 接收来自BPM服务的Feign调用
 *
 * @author 芋道源码
 */
@Tag(name = "管理后台 - OA流程回调")
@RestController
@RequestMapping(ApiConstants.PREFIX + "/process-callback")
@Validated
@Slf4j
public class OaFeignNotificationController {

    @Resource
    private OaFlowBillServiceFactory flowBillServiceFactory;

    @PostMapping("/bpm-event")
    @Operation(summary = "接收BPM事件回调（支持流程实例和任务事件）")
    public CommonResult<Boolean> handleBpmEvent(@RequestBody BpmProcessInstanceStatusMessage message) {
        log.info("[handleBpmEvent][Feign回调] 收到BPM事件回调: eventType={}, processInstanceId={}, processDefinitionKey={}",
                message.getEventType() != null ? message.getEventType().getName() : "unknown", 
                message.getProcessInstanceId(), message.getProcessDefinitionKey());
        
        try {
            // 参数校验
            if (StrUtil.isBlank(message.getProcessDefinitionKey()) || message.getEventType() == null) {
                log.warn("[handleBpmEvent] 关键参数为空，跳过处理");
                return CommonResult.error(400, "关键参数为空");
            }
            
            // 只处理OA相关流程
            if (!message.getProcessDefinitionKey().startsWith("oa_")) {
                log.debug("[handleBpmEvent] 非OA流程，跳过处理: {}", message.getProcessDefinitionKey());
                return success(true);
            }
            
            // 根据事件类型进行不同处理
            if (message.getEventType().isProcessInstanceEvent()) {
                return handleProcessInstanceEvent(message);
            } else if (message.getEventType().isTaskEvent()) {
                return handleTaskEvent(message);
            } else {
                log.warn("[handleBpmEvent] 未知的事件类型: {}", message.getEventType());
                return CommonResult.error(400, "未知的事件类型");
            }
            
        } catch (Exception e) {
            log.error("[handleBpmEvent][Feign回调] 处理BPM事件失败", e);
            return CommonResult.error(500, "处理失败: " + e.getMessage());
        }
    }

    @PostMapping("/status-change")
    @Operation(summary = "接收流程状态变化回调（兼容旧版本）")
    @Deprecated
    public CommonResult<Boolean> processStatusChange(@RequestBody BpmProcessInstanceStatusMessage message) {
        log.info("[processStatusChange][Feign回调] 收到流程状态变化回调（兼容模式）: {}", message);
        
        try {
            String processDefinitionKey = message.getProcessDefinitionKey();
            String businessKey = message.getBusinessKey();
            BpmProcessInstanceInfo processInstanceInfo = message.getProcessInstanceInfo();
            Integer status = processInstanceInfo.getStatus();
            
            // 参数校验
            if (StrUtil.isBlank(processDefinitionKey) || StrUtil.isBlank(businessKey) || status == null) {
                log.warn("[processStatusChange] 参数不完整，processDefinitionKey: {}, businessKey: {}, status: {}", 
                        processDefinitionKey, businessKey, status);
                return CommonResult.error(400, "参数不完整");
            }
            
            // 只处理OA相关流程
            if (!processDefinitionKey.startsWith("oa_")) {
                log.debug("[processStatusChange] 非OA流程，跳过处理: {}", processDefinitionKey);
                return success(true);
            }
            
            log.info("[processStatusChange] 处理OA流程状态变化，processDefinitionKey: {}, businessKey: {}, status: {}", 
                    processDefinitionKey, businessKey, status);
            
            try {
                // 通过工厂获取对应的服务实现
                FlowBillService<OaBillTypeEnum> flowBillService = flowBillServiceFactory.getServiceByProcessKey(processDefinitionKey);
                
                // 统一调用接口方法
                flowBillService.updateProcessStatus(businessKey, status);
                
                log.info("[processStatusChange] 流程状态更新成功，processDefinitionKey: {}, businessKey: {}, status: {}", 
                        processDefinitionKey, businessKey, status);
                        
            } catch (IllegalArgumentException e) {
                log.warn("[processStatusChange] 未知的OA流程类型: {}", processDefinitionKey);
                return CommonResult.error(404, "未知的流程类型: " + processDefinitionKey);
            }
            
            return success(true);
            
        } catch (Exception e) {
            log.error("[processStatusChange][Feign回调] 处理流程状态变化失败", e);
            return CommonResult.error(500, "处理失败: " + e.getMessage());
        }
    }

    /**
     * 处理流程实例事件
     */
    private CommonResult<Boolean> handleProcessInstanceEvent(BpmProcessInstanceStatusMessage message) {
        String processDefinitionKey = message.getProcessDefinitionKey();
        String businessKey = message.getBusinessKey();
        BpmProcessInstanceInfo processInstanceInfo = message.getProcessInstanceInfo();
        Integer status = processInstanceInfo.getStatus();
        
        if (StrUtil.isBlank(businessKey) || status == null) {
            log.warn("[handleProcessInstanceEvent] businessKey或status为空，跳过处理");
            return CommonResult.error(400, "businessKey或status为空");
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
            
            return success(true);
                    
        } catch (IllegalArgumentException e) {
            log.warn("[handleProcessInstanceEvent] 未知的OA流程类型: {}", processDefinitionKey);
            return CommonResult.error(404, "未知的流程类型: " + processDefinitionKey);
        } catch (Exception e) {
            log.error("[handleProcessInstanceEvent] 处理流程实例事件失败", e);
            return CommonResult.error(500, "处理失败: " + e.getMessage());
        }
    }

    /**
     * 处理任务事件
     */
    private CommonResult<Boolean> handleTaskEvent(BpmProcessInstanceStatusMessage message) {
        BpmEventTypeEnum eventType = message.getEventType();
        BpmTaskInfo taskInfo = message.getTaskInfo();

        log.info("[handleTaskEvent] 处理任务事件，eventType: {}, taskId: {}, taskName: {}, assigneeId: {}, taskResult: {}, taskReason: {}",
                eventType.getName(), taskInfo.getTaskId(), taskInfo.getTaskName(),
                taskInfo.getAssigneeId(), taskInfo.getTaskResult(), taskInfo.getTaskReason());

        // 根据不同的任务事件类型进行处理
        switch (eventType) {
            case TASK_CREATED:
                return handleTaskCreated(message);
            case TASK_APPROVED:
                return handleTaskApproved(message);
            case TASK_REJECTED:
                return handleTaskRejected(message);
            case TASK_WITHDRAWN:
                return handleTaskWithdrawn(message);
            case TASK_TRANSFERRED:
                return handleTaskTransferred(message);
            case TASK_DELEGATED:
                return handleTaskDelegated(message);
            default:
                log.debug("[handleTaskEvent] 暂不处理的任务事件类型: {}", eventType.getName());
                return success(true);
        }
    }

    /**
     * 处理任务创建事件
     */
    private CommonResult<Boolean> handleTaskCreated(BpmProcessInstanceStatusMessage message) {
        BpmEventTypeEnum eventType = message.getEventType();
        BpmTaskInfo taskInfo = message.getTaskInfo();

        log.info("[handleTaskCreated] 任务创建通知，taskId: {}, taskName: {}, assigneeId: {}",
                taskInfo.getTaskId(), taskInfo.getTaskName(), taskInfo.getAssigneeId());
        
        // 这里可以实现具体的业务逻辑，比如：
        // 1. 发送待办提醒
        // 2. 更新业务单据状态
        // 3. 记录操作日志等
        
        return success(true);
    }

    /**
     * 处理任务审批通过事件
     */
    private CommonResult<Boolean> handleTaskApproved(BpmProcessInstanceStatusMessage message) {
        BpmEventTypeEnum eventType = message.getEventType();
        BpmTaskInfo taskInfo = message.getTaskInfo();

        log.info("[handleTaskApproved] 任务审批通过，taskId: {}, assigneeId: {}, reason: {}",
                taskInfo.getTaskId(), taskInfo.getAssigneeId(), taskInfo.getTaskReason());
        
        // 这里可以实现具体的业务逻辑，比如：
        // 1. 更新业务单据审批记录
        // 2. 发送审批结果通知
        // 3. 触发下游业务流程等
        
        return success(true);
    }

    /**
     * 处理任务审批拒绝事件
     */
    private CommonResult<Boolean> handleTaskRejected(BpmProcessInstanceStatusMessage message) {
        BpmEventTypeEnum eventType = message.getEventType();
        BpmTaskInfo taskInfo = message.getTaskInfo();

        log.info("[handleTaskRejected] 任务审批拒绝，taskId: {}, assigneeId: {}, reason: {}",
                taskInfo.getTaskId(), taskInfo.getAssigneeId(), taskInfo.getTaskReason());
        
        // 这里可以实现具体的业务逻辑，比如：
        // 1. 更新业务单据为拒绝状态
        // 2. 发送拒绝通知给申请人
        // 3. 记录拒绝原因等
        
        return success(true);
    }

    /**
     * 处理任务撤回事件
     */
    private CommonResult<Boolean> handleTaskWithdrawn(BpmProcessInstanceStatusMessage message) {
        BpmEventTypeEnum eventType = message.getEventType();
        BpmTaskInfo taskInfo = message.getTaskInfo();

        log.info("[handleTaskWithdrawn] 任务撤回，taskId: {}, reason: {}",
                taskInfo.getTaskId(), taskInfo.getTaskReason());
        
        // 这里可以实现具体的业务逻辑，比如：
        // 1. 更新业务单据为撤回状态
        // 2. 发送撤回通知
        // 3. 清理相关数据等
        
        return success(true);
    }

    /**
     * 处理任务转办事件
     */
    private CommonResult<Boolean> handleTaskTransferred(BpmProcessInstanceStatusMessage message) {
        BpmEventTypeEnum eventType = message.getEventType();
        BpmTaskInfo taskInfo = message.getTaskInfo();

        log.info("[handleTaskTransferred] 任务转办，taskId: {}, assigneeId: {}",
                taskInfo.getTaskId(), taskInfo.getAssigneeId());
        
        // 这里可以实现具体的业务逻辑，比如：
        // 1. 发送转办通知
        // 2. 更新审批人信息等
        
        return success(true);
    }

    /**
     * 处理任务委派事件
     */
    private CommonResult<Boolean> handleTaskDelegated(BpmProcessInstanceStatusMessage message) {
        BpmEventTypeEnum eventType = message.getEventType();
        BpmTaskInfo taskInfo = message.getTaskInfo();

        log.info("[handleTaskDelegated] 任务委派，taskId: {}, assigneeId: {}",
                taskInfo.getTaskId(), taskInfo.getAssigneeId());
        
        // 这里可以实现具体的业务逻辑，比如：
        // 1. 发送委派通知
        // 2. 更新委派人信息等
        
        return success(true);
    }

} 