package cn.iocoder.yudao.module.oa.process.local;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusEvent;
import cn.iocoder.yudao.module.oa.service.FlowBillService;
import cn.iocoder.yudao.module.oa.service.FlowBillServiceFactory;
import cn.iocoder.yudao.module.oa.service.car.CarApplyBillService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * OA 请假单的结果的监听器实现类
 *
 * @author 芋道源码
 */
@Component
@Slf4j
public class OaLocalEventNotificationListener implements ApplicationListener<BpmProcessInstanceStatusEvent> {

    @Resource
    private FlowBillServiceFactory flowBillServiceFactory;

    @Override
    public void onApplicationEvent(BpmProcessInstanceStatusEvent message) {
        log.info("[processStatusChange][Feign回调] 收到流程状态变化回调: {}", message);

        try {
            String processDefinitionKey = message.getProcessDefinitionKey();
            String businessKey = message.getBusinessKey();
            Integer status = message.getStatus();

            // 参数校验
            if (StrUtil.isBlank(processDefinitionKey) || StrUtil.isBlank(businessKey) || status == null) {
                log.warn("[processStatusChange] 参数不完整，processDefinitionKey: {}, businessKey: {}, status: {}",
                        processDefinitionKey, businessKey, status);
                return;
            }

            // 只处理OA相关流程
            if (!processDefinitionKey.startsWith("oa_")) {
                log.debug("[processStatusChange] 非OA流程，跳过处理: {}", processDefinitionKey);
                return;
            }

            log.info("[processStatusChange] 处理OA流程状态变化，processDefinitionKey: {}, businessKey: {}, status: {}",
                    processDefinitionKey, businessKey, status);

            try {
                // 通过工厂获取对应的服务实现
                FlowBillService flowBillService = flowBillServiceFactory.getServiceByProcessKey(processDefinitionKey);
                
                // 统一调用接口方法
                flowBillService.updateProcessStatus(businessKey, status);
                
                log.info("[processStatusChange] 流程状态更新成功，processDefinitionKey: {}, businessKey: {}, status: {}", 
                        processDefinitionKey, businessKey, status);
                        
            } catch (IllegalArgumentException e) {
                log.debug("[processStatusChange] 未知的OA流程类型: {}", processDefinitionKey);
            }


        } catch (Exception e) {
            log.error("[processStatusChange][Feign回调] 处理流程状态变化失败", e);
        }
    }

}
