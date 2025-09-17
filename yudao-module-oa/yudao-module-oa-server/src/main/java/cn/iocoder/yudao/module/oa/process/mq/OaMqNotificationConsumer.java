package cn.iocoder.yudao.module.oa.process.mq;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.mq.redis.core.stream.AbstractRedisStreamMessageListener;
import cn.iocoder.yudao.module.oa.service.car.CarApplyBillService;
import cn.iocoder.yudao.module.oa.service.car.CarReturnBillService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * OA 流程状态通知消息消费者
 * 监听来自BPM服务的流程状态变化消息
 *
 * @author 芋道源码
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "yudao.bpm.notification.mq.enabled", havingValue = "true", matchIfMissing = false)
public class OaMqNotificationConsumer extends AbstractRedisStreamMessageListener<OaProcessInstanceStatusMessage> {

    @Resource
    private CarApplyBillService carApplyBillService;

    @Resource
    private CarReturnBillService carReturnBillService;

    @Override
    public void onMessage(OaProcessInstanceStatusMessage message) {
        log.info("[onMessage][MQ消费] 收到流程状态变化消息: processInstanceId={}, processDefinitionKey={}, status={}",
                message.getProcessInstanceId(), message.getProcessDefinitionKey(), message.getStatus());

        try {
            // 根据流程类型分发处理
            if (StrUtil.isBlank(message.getProcessDefinitionKey())) {
                log.warn("[onMessage] processDefinitionKey为空，跳过处理");
                return;
            }

            if (message.getProcessDefinitionKey().startsWith("oa_")) {
                handleOaProcessNotification(message);
            } else {
                log.debug("[onMessage] 非OA流程，跳过处理: {}", message.getProcessDefinitionKey());
            }

        } catch (Exception e) {
            log.error("[onMessage][MQ消费] 处理流程状态变化消息失败", e);
            // 这里可以考虑重试或死信队列处理
            throw e; // 重新抛出异常，触发重试机制
        }
    }

    /**
     * 处理OA流程通知
     */
    private void handleOaProcessNotification(OaProcessInstanceStatusMessage message) {
        String processDefinitionKey = message.getProcessDefinitionKey();
        String businessKey = message.getBusinessKey();
        Integer status = message.getStatus();
        
        if (StrUtil.isBlank(businessKey) || status == null) {
            log.warn("[handleOaProcessNotification] businessKey或status为空，跳过处理");
            return;
        }
        
        log.info("[handleOaProcessNotification] 处理OA流程状态变化，processDefinitionKey: {}, businessKey: {}, status: {}", 
                processDefinitionKey, businessKey, status);
        
        try {
            switch (processDefinitionKey) {
                case "oa_car_apply_bill":
                    // 更新用车申请单状态
                    Long carApplyBillId = Long.parseLong(businessKey);
                    carApplyBillService.updateProcessStatus(carApplyBillId, status);
                    log.info("[handleOaProcessNotification] 用车申请单状态更新成功，id: {}, status: {}", carApplyBillId, status);
                    break;
                case "oa_car_return_bill":
                    // 更新还车申请单状态
                    Long carReturnBillId = Long.parseLong(businessKey);
                    carReturnBillService.updateProcessStatus(carReturnBillId, status);
                    log.info("[handleOaProcessNotification] 还车申请单状态更新成功，id: {}, status: {}", carReturnBillId, status);
                    break;
                // 可以在这里添加其他OA流程的处理
                // case "oa_leave":
                //     handleLeaveProcess(businessKey, status);
                //     break;
                    
                default:
                    log.debug("[handleOaProcessNotification] 未知的OA流程类型: {}", processDefinitionKey);
            }
        } catch (Exception e) {
            log.error("[handleOaProcessNotification] 处理OA流程状态变化失败", e);
            throw e; // 重新抛出异常，触发重试机制
        }
    }

} 