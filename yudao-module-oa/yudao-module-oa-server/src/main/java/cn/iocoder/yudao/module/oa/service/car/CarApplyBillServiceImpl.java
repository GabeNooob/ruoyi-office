package cn.iocoder.yudao.module.oa.service.car;

import cn.iocoder.yudao.framework.common.enums.SystemEnum;
import cn.iocoder.yudao.framework.common.util.bill.BillCodeUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmTaskStatusEnum;
import cn.iocoder.yudao.module.oa.enums.OaBillTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.oa.controller.admin.car.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.car.CarApplyBillDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.oa.dal.mysql.car.CarApplyBillMapper;

import cn.iocoder.yudao.framework.common.service.FlowBillService;

import cn.iocoder.yudao.module.oa.enums.CarReturnStatusEnum;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.bpm.enums.ErrorCodeConstants.OA_LEAVE_NOT_EXISTS;
import static cn.iocoder.yudao.module.oa.enums.ErrorCodeConstants.*;

/**
 * 用车申请单 Service 实现类
 *
 * @author 芋道源码
 */
@Slf4j
@Service
@Validated
public class CarApplyBillServiceImpl implements CarApplyBillService, FlowBillService<OaBillTypeEnum> {

    @Resource
    private CarApplyBillMapper carApplyBillMapper;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;


    @Override
    public Long saveCarApplyBill(CarApplyBillSaveReqVO saveReqVO) {

        // 如果单号为空，需要生成
        if(StringUtils.isBlank(saveReqVO.getBillCode())){
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_CAR_APPLY_BILL));
        }

        // 插入或更新
        CarApplyBillDO carApplyBill = BeanUtils.toBean(saveReqVO, CarApplyBillDO.class);
        carApplyBillMapper.insertOrUpdate(carApplyBill);

        // 返回
        return carApplyBill.getId();
    }

    @Override
    public Long submitCarApplyBill(CarApplyBillSaveReqVO saveReqVO) {

        // 如果单号为空，需要生成
        if(StringUtils.isBlank(saveReqVO.getBillCode())){
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_CAR_APPLY_BILL));
        }

        // 保存或更新
        CarApplyBillDO carApplyBill = BeanUtils.toBean(saveReqVO, CarApplyBillDO.class)
                .setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        carApplyBillMapper.insertOrUpdate(carApplyBill);

        // 发起 BPM 流程
        Map<String, Object> processInstanceVariables = new HashMap<>();
        String processInstanceId = processInstanceApi.createProcessInstance(Long.valueOf(saveReqVO.getCreator()),
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(OaBillTypeEnum.OA_CAR_APPLY_BILL.getProcessDefinitionKey())
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(carApplyBill.getId()))
        ).getCheckedData();

        // 将工作流的编号，更新到单据中
        carApplyBillMapper.updateById(new CarApplyBillDO().setId(carApplyBill.getId()).setProcessInstanceId(processInstanceId));
        // 返回
        return carApplyBill.getId();
    }

    @Override
    public Long createCarApplyBill(CarApplyBillSaveReqVO createReqVO) {
        // 插入
        String billCode = BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_CAR_APPLY_BILL);
        createReqVO.setBillCode(billCode);
        // 插入
        CarApplyBillDO carApplyBill = BeanUtils.toBean(createReqVO, CarApplyBillDO.class);
        carApplyBillMapper.insertOrUpdate(carApplyBill);

        // 返回
        return carApplyBill.getId();
    }



    @Override
    public void updateCarApplyBill(CarApplyBillSaveReqVO updateReqVO) {
        // 校验存在
        validateCarApplyBillExists(updateReqVO.getId());
        // 更新
        CarApplyBillDO updateObj = BeanUtils.toBean(updateReqVO, CarApplyBillDO.class);
        carApplyBillMapper.updateById(updateObj);
    }

    @Override
    public void deleteCarApplyBill(Long id) {
        // 校验存在
        validateCarApplyBillExists(id);
        // 删除
        carApplyBillMapper.deleteById(id);
    }

    @Override
    public void deleteCarApplyBillListByIds(List<Long> ids) {
        // 删除
        carApplyBillMapper.deleteByIds(ids);
    }


    private void validateCarApplyBillExists(Long id) {
        if (carApplyBillMapper.selectById(id) == null) {
            throw exception(CAR_APPLY_BILL_NOT_EXISTS);
        }
    }

    @Override
    public CarApplyBillDO getCarApplyBill(Long id) {
        return carApplyBillMapper.selectById(id);
    }
    
    @Override
    public CarApplyBillDO getCarApplyBillByCode(String code) {
        return carApplyBillMapper.selectOne(new LambdaQueryWrapperX<CarApplyBillDO>().eq(CarApplyBillDO::getBillCode, code));
    }

    @Override
    public PageResult<CarApplyBillDO> getCarApplyBillPage(CarApplyBillPageReqVO pageReqVO) {
        return carApplyBillMapper.selectPage(pageReqVO);
    }


    // ==================== FlowBillService 接口实现 ====================

    @Override
    public OaBillTypeEnum getSupportedBillType() {
        return OaBillTypeEnum.OA_CAR_APPLY_BILL;
    }

    @Override
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        log.info("[updateProcessStatus] 更新用车申请单流程状态，id: {}, status: {}", id, status);

        // 校验用车申请单存在
        validateCarApplyBillExists(id);

        // 更新流程状态
        CarApplyBillDO updateObj = new CarApplyBillDO();
        updateObj.setId(id);
        updateObj.setProcessStatus(status);
        carApplyBillMapper.updateById(updateObj);

        log.info("[updateProcessStatus] 用车申请单流程状态更新成功，id: {}, status: {}", id, status);
    }



    @Override
    public void updateReturnStatus(Long id, Integer returnStatus) {
        log.info("[updateReturnStatus] 更新用车申请单还车状态，id: {}, returnStatus: {}", id, returnStatus);
        
        // 校验用车申请单存在
        validateCarApplyBillExists(id);
        
        // 更新还车状态
        CarApplyBillDO updateObj = new CarApplyBillDO();
        updateObj.setId(id);
        updateObj.setReturnStatus(returnStatus);
        carApplyBillMapper.updateById(updateObj);
        
        log.info("[updateReturnStatus] 用车申请单还车状态更新成功，id: {}, returnStatus: {}", id, returnStatus);
    }

    @Override
    public void markAsReturned(Long id) {
        updateReturnStatus(id, CarReturnStatusEnum.RETURNED.getStatus());
    }
    
    @Override
    public void markAsReturning(Long id) {
        updateReturnStatus(id, CarReturnStatusEnum.RETURNING.getStatus());
    }
    
    @Override
    public void markAsNotReturned(Long id) {
        updateReturnStatus(id, CarReturnStatusEnum.NOT_RETURNED.getStatus());
    }

}