package cn.iocoder.yudao.module.wms.service.purchaseorder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.iocoder.yudao.module.wms.dal.mysql.purchaseorder.PurchaseOrderDetailMapper;
import cn.iocoder.yudao.module.wms.framework.security.enums.FlowCodeEnum;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.wms.controller.admin.purchaseorder.vo.*;
import cn.iocoder.yudao.module.wms.dal.dataobject.purchaseorder.PurchaseOrderDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.purchaseorderdetail.PurchaseOrderDetailDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.wms.dal.mysql.purchaseorder.PurchaseOrderMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.*;

/**
 * 采购订单 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    @Resource
    private PurchaseOrderMapper purchaseOrderMapper;
    @Resource
    private PurchaseOrderDetailMapper purchaseOrderDetailMapper;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPurchaseOrder(PurchaseOrderSaveReqVO createReqVO) {
        // 插入
        PurchaseOrderDO purchaseOrder = BeanUtils.toBean(createReqVO, PurchaseOrderDO.class);
        purchaseOrderMapper.insert(purchaseOrder);
        // 插入子表
        createPurchaseOrderDetailList(purchaseOrder.getId(), createReqVO.getPurchaseOrderDetails());
        // 返回
        return purchaseOrder.getId();
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitPurchaseOrder(PurchaseOrderSaveReqVO createReqVO) {
        // 插入
        PurchaseOrderDO purchaseOrder = BeanUtils.toBean(createReqVO, PurchaseOrderDO.class);
        purchaseOrderMapper.insert(purchaseOrder);
        // 插入子表
        createPurchaseOrderDetailList(purchaseOrder.getId(), createReqVO.getPurchaseOrderDetails());

        // 发起 BPM 流程
        Map<String, Object> processInstanceVariables = new HashMap<>();
        String processInstanceId = processInstanceApi.createProcessInstance(Long.valueOf(createReqVO.getApplicantUserId()),
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(FlowCodeEnum.WMS_PURCHASE_ORDER_FLOW.getFlowCode())
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(purchaseOrder.getId()))
        ).getCheckedData();

        // 跟新单据工作流的编号
        purchaseOrderMapper.updateById(new PurchaseOrderDO().setId(purchaseOrder.getId()).setProcessInstanceId(processInstanceId));

        // 返回
        return purchaseOrder.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePurchaseOrder(PurchaseOrderSaveReqVO updateReqVO) {
        // 校验存在
        validatePurchaseOrderExists(updateReqVO.getId());
        // 更新
        PurchaseOrderDO updateObj = BeanUtils.toBean(updateReqVO, PurchaseOrderDO.class);
        purchaseOrderMapper.updateById(updateObj);

        // 更新子表
        updatePurchaseOrderDetailList(updateReqVO.getId(), updateReqVO.getPurchaseOrderDetails());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePurchaseOrder(Long id) {
        // 校验存在
        validatePurchaseOrderExists(id);
        // 删除
        purchaseOrderMapper.deleteById(id);

        // 删除子表
        deletePurchaseOrderDetailByPurchaseOrderId(id);
    }

    @Override
        @Transactional(rollbackFor = Exception.class)
    public void deletePurchaseOrderListByIds(List<Long> ids) {
        // 删除
        purchaseOrderMapper.deleteByIds(ids);
    
        // 删除子表
        deletePurchaseOrderDetailByPurchaseOrderIds(ids);
    }


    private void validatePurchaseOrderExists(Long id) {
        if (purchaseOrderMapper.selectById(id) == null) {
            throw exception(PURCHASE_ORDER_NOT_EXISTS);
        }
    }

    @Override
    public PurchaseOrderDO getPurchaseOrder(Long id) {
        return purchaseOrderMapper.selectById(id);
    }

    @Override
    public PageResult<PurchaseOrderDO> getPurchaseOrderPage(PurchaseOrderPageReqVO pageReqVO) {
        return purchaseOrderMapper.selectPage(pageReqVO);
    }

    // ==================== 子表（采购订单明细） ====================

    @Override
    public List<PurchaseOrderDetailDO> getPurchaseOrderDetailListByPurchaseOrderId(String purchaseOrderId) {
        return purchaseOrderDetailMapper.selectListByPurchaseOrderId(purchaseOrderId);
    }

    private void createPurchaseOrderDetailList(Long purchaseOrderId, List<PurchaseOrderDetailDO> list) {
        list.forEach(o -> o.setPurchaseOrderId(String.valueOf(purchaseOrderId)).clean());
        purchaseOrderDetailMapper.insertBatch(list);
    }

    private void updatePurchaseOrderDetailList(Long purchaseOrderId, List<PurchaseOrderDetailDO> list) {
	    list.forEach(o -> o.setPurchaseOrderId(String.valueOf(purchaseOrderId)).clean());
	    List<PurchaseOrderDetailDO> oldList = purchaseOrderDetailMapper.selectListByPurchaseOrderId(String.valueOf(purchaseOrderId));
	    List<List<PurchaseOrderDetailDO>> diffList = diffList(oldList, list, (oldVal, newVal) -> {
            boolean same = ObjectUtil.equal(oldVal.getId(), newVal.getId());
            if (same) {
                newVal.setId(oldVal.getId()).clean(); // 解决更新情况下：updateTime 不更新
            }
            return same;
	    });

	    // 第二步，批量添加、修改、删除
	    if (CollUtil.isNotEmpty(diffList.get(0))) {
	        purchaseOrderDetailMapper.insertBatch(diffList.get(0));
	    }
	    if (CollUtil.isNotEmpty(diffList.get(1))) {
	        purchaseOrderDetailMapper.updateBatch(diffList.get(1));
	    }
	    if (CollUtil.isNotEmpty(diffList.get(2))) {
	        purchaseOrderDetailMapper.deleteByIds(convertList(diffList.get(2), PurchaseOrderDetailDO::getId));
	    }
    }

    private void deletePurchaseOrderDetailByPurchaseOrderId(Long purchaseOrderId) {
        purchaseOrderDetailMapper.deleteByPurchaseOrderId(String.valueOf(purchaseOrderId));
    }

	private void deletePurchaseOrderDetailByPurchaseOrderIds(List<Long> purchaseOrderIds) {
        purchaseOrderDetailMapper.deleteByPurchaseOrderIds(purchaseOrderIds);
	}

   /* public void updateFlowDataByKey(WmsProcessInstanceStatusMessage message) {
        logger.info("[updateFlowDataByKey][MQ消费] 采购订单工作流状态变化消息: {}", message);
    }*/
}