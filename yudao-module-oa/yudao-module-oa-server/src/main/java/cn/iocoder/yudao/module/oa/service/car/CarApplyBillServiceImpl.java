package cn.iocoder.yudao.module.oa.service.car;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.oa.controller.admin.car.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.car.CarApplyBillDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.oa.dal.mysql.car.CarApplyBillMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.oa.enums.ErrorCodeConstants.*;

/**
 * 用车申请单 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class CarApplyBillServiceImpl implements CarApplyBillService {

    @Resource
    private CarApplyBillMapper carApplyBillMapper;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    /**
     * OA 用车申请单对应的流程定义 KEY
     */
    public static final String PROCESS_KEY = "oa_car_apply_bill";

    @Override
    public Long createCarApplyBill(CarApplyBillSaveReqVO createReqVO) {
        // 插入
        CarApplyBillDO carApplyBill = BeanUtils.toBean(createReqVO, CarApplyBillDO.class);
        carApplyBillMapper.insert(carApplyBill);

        // 返回
        return carApplyBill.getId();
    }

    @Override
    public Long submitCarApplyBill(CarApplyBillSaveReqVO createReqVO) {
        // 插入
        CarApplyBillDO carApplyBill = BeanUtils.toBean(createReqVO, CarApplyBillDO.class);
        carApplyBillMapper.insert(carApplyBill);

        // 发起 BPM 流程
        Map<String, Object> processInstanceVariables = new HashMap<>();
        String processInstanceId = processInstanceApi.createProcessInstance(Long.valueOf(createReqVO.getCreator()),
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(PROCESS_KEY)
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(carApplyBill.getId()))
                        ).getCheckedData();

        // 将工作流的编号，更新到 OA 请假单中
        carApplyBillMapper.updateById(new CarApplyBillDO().setId(carApplyBill.getId()).setProcessInstanceId(processInstanceId));
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
    public PageResult<CarApplyBillDO> getCarApplyBillPage(CarApplyBillPageReqVO pageReqVO) {
        return carApplyBillMapper.selectPage(pageReqVO);
    }

}