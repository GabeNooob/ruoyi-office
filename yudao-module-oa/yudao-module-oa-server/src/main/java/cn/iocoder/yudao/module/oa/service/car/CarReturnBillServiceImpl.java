package cn.iocoder.yudao.module.oa.service.car;

import cn.iocoder.yudao.framework.common.enums.SystemEnum;
import cn.iocoder.yudao.framework.common.util.bill.BillCodeUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmTaskStatusEnum;
import cn.iocoder.yudao.module.oa.dal.dataobject.car.CarApplyBillDO;
import cn.iocoder.yudao.module.oa.enums.OaBillTypeEnum;
import cn.iocoder.yudao.module.oa.service.FlowBillService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.oa.controller.admin.car.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.car.CarReturnBillDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.oa.dal.mysql.car.CarReturnBillMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.oa.enums.ErrorCodeConstants.*;

/**
 * 还车申请单 Service 实现类
 *
 * @author 芋道源码
 */
@Slf4j
@Service
@Validated
public class CarReturnBillServiceImpl implements CarReturnBillService, FlowBillService {

    @Resource
    private CarReturnBillMapper carReturnBillMapper;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Resource
    private CarApplyBillService carApplyBillService;

    @Override
    public Long saveCarReturnBill(CarReturnBillSaveReqVO saveReqVO) {

        // 如果单号为空，需要生成
        if(StringUtils.isBlank(saveReqVO.getBillCode())){
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_CAR_RETURN_BILL));
        }

        // 插入或更新
        CarReturnBillDO carReturnBill = BeanUtils.toBean(saveReqVO, CarReturnBillDO.class);
        carReturnBillMapper.insertOrUpdate(carReturnBill);

        // 返回
        return carReturnBill.getId();
    }

    @Override
    public Long submitCarReturnBill(CarReturnBillSaveReqVO saveReqVO) {

        // 如果单号为空，需要生成
        if(StringUtils.isBlank(saveReqVO.getBillCode())){
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_CAR_RETURN_BILL));
        }

        // 验证用车申请单是否已还车
        Long applyBillId = validateApplyBillNotReturned(saveReqVO.getApplyBill());

        // 保存或更新
        CarReturnBillDO carReturnBill = BeanUtils.toBean(saveReqVO, CarReturnBillDO.class)
                .setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        carReturnBillMapper.insertOrUpdate(carReturnBill);

        // 发起 BPM 流程
        Map<String, Object> processInstanceVariables = new HashMap<>();
        String processInstanceId = processInstanceApi.createProcessInstance(Long.valueOf(saveReqVO.getCreator()),
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(OaBillTypeEnum.OA_CAR_RETURN_BILL.getProcessDefinitionKey())
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(carReturnBill.getId()))
        ).getCheckedData();

        // 将工作流的编号，更新到单据中
        carReturnBillMapper.updateById(new CarReturnBillDO().setId(carReturnBill.getId()).setProcessInstanceId(processInstanceId));
        
        // 标记对应用车申请单为已还车
        carApplyBillService.markAsReturned(applyBillId);
        
        // 返回
        return carReturnBill.getId();
    }

    @Override
    public Long createCarReturnBill(CarReturnBillSaveReqVO createReqVO) {
        // 插入
        String billCode = BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_CAR_RETURN_BILL);
        createReqVO.setBillCode(billCode);
        // 插入
        CarReturnBillDO carReturnBill = BeanUtils.toBean(createReqVO, CarReturnBillDO.class);
        carReturnBillMapper.insertOrUpdate(carReturnBill);

        // 返回
        return carReturnBill.getId();
    }

    @Override
    public void updateCarReturnBill(CarReturnBillSaveReqVO updateReqVO) {
        // 校验存在
        validateCarReturnBillExists(updateReqVO.getId());
        // 更新
        CarReturnBillDO updateObj = BeanUtils.toBean(updateReqVO, CarReturnBillDO.class);
        carReturnBillMapper.updateById(updateObj);
    }

    @Override
    public void deleteCarReturnBill(Long id) {
        // 校验存在
        validateCarReturnBillExists(id);
        // 删除
        carReturnBillMapper.deleteById(id);
    }

    @Override
    public void deleteCarReturnBillListByIds(List<Long> ids) {
        // 删除
        carReturnBillMapper.deleteByIds(ids);
    }

    private void validateCarReturnBillExists(Long id) {
        if (carReturnBillMapper.selectById(id) == null) {
            throw exception(CAR_RETURN_BILL_NOT_EXISTS);
        }
    }

    @Override
    public CarReturnBillDO getCarReturnBill(Long id) {
        return carReturnBillMapper.selectById(id);
    }

    @Override
    public PageResult<CarReturnBillDO> getCarReturnBillPage(CarReturnBillPageReqVO pageReqVO) {
        return carReturnBillMapper.selectPage(pageReqVO);
    }

    @Override
    public void updateProcessStatus(Long id, Integer status) {
        log.info("[updateProcessStatus] 更新还车申请单流程状态，id: {}, status: {}", id, status);
        
        // 校验还车申请单存在
        validateCarReturnBillExists(id);
        
        // 更新流程状态
        CarReturnBillDO updateObj = new CarReturnBillDO();
        updateObj.setId(id);
        updateObj.setProcessStatus(status);
        carReturnBillMapper.updateById(updateObj);
        
        log.info("[updateProcessStatus] 还车申请单流程状态更新成功，id: {}, status: {}", id, status);
    }

    // ==================== FlowBillService 接口实现 ====================

    @Override
    public OaBillTypeEnum getSupportedBillType() {
        return OaBillTypeEnum.OA_CAR_RETURN_BILL;
    }

    @Override
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        updateProcessStatus(id, status);
    }

    /**
     * 验证用车申请单是否已还车
     *
     * @param applyBillCode 用车申请单ID
     */
    private Long validateApplyBillNotReturned(String applyBillCode) {
        Long applyBillId = 0L;
        if (applyBillCode == null) {
            throw exception(CAR_APPLY_BILL_NOT_EXISTS);
        }
        
        CarApplyBillDO applyBill = carApplyBillService.getCarApplyBillByCode(applyBillCode);
        if (applyBill == null) {
            throw exception(CAR_APPLY_BILL_NOT_EXISTS);
        }
        
        if (Boolean.TRUE.equals(applyBill.getIsReturned())) {
            throw exception(CAR_APPLY_BILL_ALREADY_RETURNED);
        }
        return applyBill.getId();
    }

}