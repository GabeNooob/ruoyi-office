package cn.iocoder.yudao.module.oa.service.car;

import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusEvent;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusEventListener;
import cn.iocoder.yudao.module.oa.enums.OaBillTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * OA 请假单的结果的监听器实现类
 *
 * @author 芋道源码
 */
@Component
public class CarApplyBpmStatusListener extends BpmProcessInstanceStatusEventListener {

    @Resource
    private CarApplyBillService carApplyBillService;

    @Override
    protected String getProcessDefinitionKey() {
        return OaBillTypeEnum.OA_CAR_APPLY_BILL.getProcessDefinitionKey();
    }

    @Override
    protected void onEvent(BpmProcessInstanceStatusEvent event) {
        carApplyBillService.updateBillStatus(Long.parseLong(event.getBusinessKey()), event.getStatus());
    }

}
