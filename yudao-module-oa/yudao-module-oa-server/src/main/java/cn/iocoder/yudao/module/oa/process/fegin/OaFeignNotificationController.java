package cn.iocoder.yudao.module.oa.process.fegin;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusMessage;
import cn.iocoder.yudao.module.oa.enums.ApiConstants;
import cn.iocoder.yudao.module.oa.service.FlowBillService;
import cn.iocoder.yudao.module.oa.service.FlowBillServiceFactory;
import cn.iocoder.yudao.module.oa.service.car.CarApplyBillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    private FlowBillServiceFactory flowBillServiceFactory;

    @PostMapping("/status-change")
    @Operation(summary = "接收流程状态变化回调")
    public CommonResult<Boolean> processStatusChange(@RequestBody BpmProcessInstanceStatusMessage message) {
        log.info("[processStatusChange][Feign回调] 收到流程状态变化回调: {}", message);
        
        try {
            String processDefinitionKey = message.getProcessDefinitionKey();
            String businessKey = message.getBusinessKey();
            Integer status = message.getStatus();
            
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
                FlowBillService flowBillService = flowBillServiceFactory.getServiceByProcessKey(processDefinitionKey);
                
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

} 