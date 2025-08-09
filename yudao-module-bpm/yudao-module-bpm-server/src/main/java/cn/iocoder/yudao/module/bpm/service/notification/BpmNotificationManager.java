package cn.iocoder.yudao.module.bpm.service.notification;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusMessage;
import cn.iocoder.yudao.module.bpm.api.event.BpmNotificationTypeEnum;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * BPM 流程通知管理器
 * 统一管理各种通知方式的分发
 *
 * @author 芋道源码
 */
@Slf4j
@Component
public class BpmNotificationManager {

    @Resource
    private List<BpmNotificationHandler> notificationHandlers;

    /**
     * 默认通知方式
     */
    @Value("${yudao.bpm.notification.default-type:local}")
    private String defaultNotificationType;

    /**
     * 是否异步处理
     */
    @Value("${yudao.bmp.notification.async:true}")
    private Boolean asyncProcess;

    /**
     * 流程特定的通知配置
     * key: processDefinitionKey, value: 通知方式
     */
    @Value("#{${yudao.bpm.notification.process-config:{}}}")
    private Map<String, String> processNotificationConfig;

    /**
     * 通知处理器映射
     */
    private Map<BpmNotificationTypeEnum, BpmNotificationHandler> handlerMap = new HashMap<>();

    @PostConstruct
    public void init() {
        // 初始化处理器映射
        for (BpmNotificationHandler handler : notificationHandlers) {
            if (handler.isEnabled()) {
                handlerMap.put(handler.getSupportType(), handler);
                log.info("[init] 注册通知处理器: {}", handler.getSupportType().getName());
            }
        }
        log.info("[init] 通知管理器初始化完成，共注册 {} 个处理器", handlerMap.size());
    }

    /**
     * 发送流程状态通知
     *
     * @param processInstance 流程实例
     * @param status         流程状态
     */
    public void sendProcessStatusNotification(ProcessInstance processInstance, Integer status) {
        if (processInstance == null) {
            log.warn("[sendProcessStatusNotification] 流程实例为空，跳过通知");
            return;
        }

        // 构建通知消息
        BpmProcessInstanceStatusMessage message = buildNotificationMessage(processInstance, status);
        
        // 获取通知方式
        BpmNotificationTypeEnum notificationType = getNotificationType(processInstance.getProcessDefinitionKey());
        
        // 获取对应的处理器
        BpmNotificationHandler handler = handlerMap.get(notificationType);
        if (handler == null) {
            log.warn("[sendProcessStatusNotification] 未找到对应的通知处理器: {}", notificationType);
            return;
        }

        // 发送通知
        if (Boolean.TRUE.equals(asyncProcess)) {
            // 异步处理
            CompletableFuture.runAsync(() -> {
                try {
                    handler.handleNotification(message);
                } catch (Exception e) {
                    log.error("[sendProcessStatusNotification] 异步通知处理失败", e);
                }
            });
        } else {
            // 同步处理
            try {
                handler.handleNotification(message);
            } catch (Exception e) {
                log.error("[sendProcessStatusNotification] 同步通知处理失败", e);
            }
        }
    }

    /**
     * 构建通知消息
     */
    private BpmProcessInstanceStatusMessage buildNotificationMessage(ProcessInstance processInstance, Integer status) {
        return BpmProcessInstanceStatusMessage.builder()
                .processInstanceId(processInstance.getId())
                .processDefinitionKey(processInstance.getProcessDefinitionKey())
                .status(status)
                .businessKey(processInstance.getBusinessKey())
                .startUserId(processInstance.getStartUserId())
                .tenantId(processInstance.getTenantId())
                .eventTime(LocalDateTime.now())
                .build();
    }

    /**
     * 获取通知方式
     */
    private BpmNotificationTypeEnum getNotificationType(String processDefinitionKey) {
        // 1. 查找流程特定配置
        String configType = processNotificationConfig.get(processDefinitionKey);
        if (StrUtil.isNotBlank(configType)) {
            return BpmNotificationTypeEnum.getByCode(configType);
        }

        // 2. 使用默认配置
        return BpmNotificationTypeEnum.getByCode(defaultNotificationType);
    }

    /**
     * 获取可用的通知方式
     */
    public Map<BpmNotificationTypeEnum, BpmNotificationHandler> getAvailableHandlers() {
        return new HashMap<>(handlerMap);
    }

} 