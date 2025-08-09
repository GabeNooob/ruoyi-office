package cn.iocoder.yudao.module.oa.process.local;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusEvent;
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
    private CarApplyBillService carApplyBillService;

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

            }

            // 只处理OA相关流程
            if (!processDefinitionKey.startsWith("oa_")) {
                log.debug("[processStatusChange] 非OA流程，跳过处理: {}", processDefinitionKey);
            }

            log.info("[processStatusChange] 处理OA流程状态变化，processDefinitionKey: {}, businessKey: {}, status: {}",
                    processDefinitionKey, businessKey, status);

            // 根据流程类型分发处理
            switch (processDefinitionKey) {
                case "oa_car_apply_bill":
                    // 更新用车申请单状态
                    Long carApplyBillId = Long.parseLong(businessKey);
                    carApplyBillService.updateProcessStatus(carApplyBillId, status);
                    log.info("[processStatusChange] 用车申请单状态更新成功，id: {}, status: {}", carApplyBillId, status);
                    break;

                // 可以在这里添加其他OA流程的处理
                // case "oa_leave":
                //     handleLeaveProcess(businessKey, status);
                //     break;

                default:
                    log.warn("[processStatusChange] 未知的OA流程类型: {}", processDefinitionKey);
            }


        } catch (Exception e) {
            log.error("[processStatusChange][Feign回调] 处理流程状态变化失败", e);
        }
    }

}
