package cn.iocoder.yudao.module.hrm.dal.mysql.employee;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.hrm.controller.admin.employee.vo.EmployeePageReqVO;
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.EmployeeDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工档案 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface EmployeeMapper extends BaseMapperX<EmployeeDO> {

    default PageResult<EmployeeDO> selectPage(EmployeePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<EmployeeDO>()
                .likeIfPresent(EmployeeDO::getEmployeeNo, reqVO.getEmployeeNo())
                .likeIfPresent(EmployeeDO::getName, reqVO.getName())
                .eqIfPresent(EmployeeDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(EmployeeDO::getEmployeeStatus, reqVO.getEmployeeStatus())
                .betweenIfPresent(EmployeeDO::getEntryDate, reqVO.getEntryDate())
                .betweenIfPresent(EmployeeDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(EmployeeDO::getId));
    }

}

