package cn.iocoder.yudao.module.bpm.service.notification.impl;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusMessage;
import cn.iocoder.yudao.module.bpm.api.event.BpmNotificationTypeEnum;
import cn.iocoder.yudao.module.bpm.service.notification.BpmNotificationHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 本地事件通知处理器
 * 适用于单体架构或同一JVM内的服务通信
 *
 * @author 芋道源码
 */
@Slf4j
@Component
public class BpmLocalEventNotificationHandler implements BpmNotificationHandler {

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Override
    public BpmNotificationTypeEnum getSupportType() {
        return BpmNotificationTypeEnum.LOCAL_EVENT;
    }

    @Override
    public void handleNotification(BpmProcessInstanceStatusMessage message) {
        try {
            log.info("[handleNotification][本地事件] 发布流程状态变化事件，processInstanceId: {}, status: {}", 
                    message.getProcessInstanceId(), message.getStatus());
            
            // 转换为本地事件对象
            BpmProcessInstanceStatusEvent event = new BpmProcessInstanceStatusEvent();
            event.setId(message.getProcessInstanceId());
            event.setProcessDefinitionKey(message.getProcessDefinitionKey());
            event.setStatus(message.getStatus());
            event.setBusinessKey(message.getBusinessKey());
            
            // 发布本地事件
            eventPublisher.publishEvent(event);
            
            log.info("[handleNotification][本地事件] 流程状态变化事件发布成功");
        } catch (Exception e) {
            log.error("[handleNotification][本地事件] 流程状态变化事件发布失败", e);
        }
    }

} 