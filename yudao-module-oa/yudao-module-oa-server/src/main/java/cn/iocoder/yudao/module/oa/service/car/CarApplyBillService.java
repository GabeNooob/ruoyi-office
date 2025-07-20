package cn.iocoder.yudao.module.oa.service.car;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.oa.controller.admin.car.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.car.CarApplyBillDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 用车申请单 Service 接口
 *
 * @author 芋道源码
 */
public interface CarApplyBillService {

    /**
     * 创建用车申请单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCarApplyBill(@Valid CarApplyBillSaveReqVO createReqVO);

    /**
     * 更新用车申请单
     *
     * @param updateReqVO 更新信息
     */
    void updateCarApplyBill(@Valid CarApplyBillSaveReqVO updateReqVO);

    /**
     * 删除用车申请单
     *
     * @param id 编号
     */
    void deleteCarApplyBill(Long id);

    /**
    * 批量删除用车申请单
    *
    * @param ids 编号
    */
    void deleteCarApplyBillListByIds(List<Long> ids);

    /**
     * 获得用车申请单
     *
     * @param id 编号
     * @return 用车申请单
     */
    CarApplyBillDO getCarApplyBill(Long id);

    /**
     * 获得用车申请单分页
     *
     * @param pageReqVO 分页查询
     * @return 用车申请单分页
     */
    PageResult<CarApplyBillDO> getCarApplyBillPage(CarApplyBillPageReqVO pageReqVO);

}