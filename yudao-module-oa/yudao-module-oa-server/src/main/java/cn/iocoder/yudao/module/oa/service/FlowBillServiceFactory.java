package cn.iocoder.yudao.module.oa.service;

import cn.iocoder.yudao.module.oa.enums.OaBillTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程表单服务工厂类
 * 根据单据类型获取对应的流程表单服务实现
 *
 * @author 芋道源码
 */
@Slf4j
@Component
public class FlowBillServiceFactory implements InitializingBean {

    @Resource
    private List<FlowBillService> flowBillServices;

    /**
     * 服务映射表：单据类型 -> 服务实现
     */
    private final Map<OaBillTypeEnum, FlowBillService> serviceMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        // 初始化服务映射表
        for (FlowBillService service : flowBillServices) {
            OaBillTypeEnum billType = service.getSupportedBillType();
            serviceMap.put(billType, service);
            log.info("注册流程表单服务: {} -> {}", billType.getTypeName(), service.getClass().getSimpleName());
        }
    }

    /**
     * 根据单据类型获取对应的服务实现
     *
     * @param billType 单据类型
     * @return 服务实现
     */
    public FlowBillService getService(OaBillTypeEnum billType) {
        FlowBillService service = serviceMap.get(billType);
        if (service == null) {
            throw new IllegalArgumentException("不支持的单据类型: " + billType);
        }
        return service;
    }

    /**
     * 根据流程定义Key获取对应的服务实现
     *
     * @param processDefinitionKey 流程定义Key
     * @return 服务实现
     */
    public FlowBillService getServiceByProcessKey(String processDefinitionKey) {
        for (OaBillTypeEnum billType : OaBillTypeEnum.values()) {
            if (billType.getProcessDefinitionKey().equals(processDefinitionKey)) {
                return getService(billType);
            }
        }
        throw new IllegalArgumentException("未找到对应的单据类型，流程定义Key: " + processDefinitionKey);
    }
}
