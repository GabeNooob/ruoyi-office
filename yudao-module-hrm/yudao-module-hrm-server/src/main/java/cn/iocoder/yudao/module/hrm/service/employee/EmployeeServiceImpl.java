package cn.iocoder.yudao.module.hrm.service.employee;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.hrm.controller.admin.employee.vo.*;
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.*;
import cn.iocoder.yudao.module.hrm.dal.mysql.employee.*;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.hrm.enums.ErrorCodeConstants.EMPLOYEE_ARCHIVE_NOT_EXISTS;

/**
 * 员工档案 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class EmployeeServiceImpl implements EmployeeService {

    @Resource
    private EmployeeMapper employeeArchiveMapper;

    @Resource
    private EmployeeWorkExperienceMapper employeeWorkExperienceMapper;

    @Resource
    private EmployeeEducationMapper employeeEducationMapper;

    @Resource
    private EmployeeFamilyMapper employeeFamilyMapper;

    @Resource
    private DeptApi deptApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEmployeeArchive(EmployeeSaveReqVO createReqVO) {
        // 插入主表
        EmployeeDO archive = BeanUtils.toBean(createReqVO, EmployeeDO.class);
        employeeArchiveMapper.insert(archive);

        // 插入工作经历
        saveWorkExperiences(archive.getId(), createReqVO.getWorkExperienceList());

        // 插入教育经历
        saveEducations(archive.getId(), createReqVO.getEducationList());

        // 插入家属信息
        saveFamilies(archive.getId(), createReqVO.getFamilyList());

        return archive.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmployeeArchive(EmployeeSaveReqVO updateReqVO) {
        // 校验存在
        validateEmployeeArchiveExists(updateReqVO.getId());

        // 更新主表
        EmployeeDO updateObj = BeanUtils.toBean(updateReqVO, EmployeeDO.class);
        employeeArchiveMapper.updateById(updateObj);

        // 删除旧的关联记录
        employeeWorkExperienceMapper.deleteByEmployeeId(updateReqVO.getId());
        employeeEducationMapper.deleteByEmployeeId(updateReqVO.getId());
        employeeFamilyMapper.deleteByEmployeeId(updateReqVO.getId());

        // 插入新的关联记录
        saveWorkExperiences(updateReqVO.getId(), updateReqVO.getWorkExperienceList());
        saveEducations(updateReqVO.getId(), updateReqVO.getEducationList());
        saveFamilies(updateReqVO.getId(), updateReqVO.getFamilyList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployeeArchive(Long id) {
        // 校验存在
        validateEmployeeArchiveExists(id);

        // 删除主表
        employeeArchiveMapper.deleteById(id);

        // 删除关联记录
        employeeWorkExperienceMapper.deleteByEmployeeId(id);
        employeeEducationMapper.deleteByEmployeeId(id);
        employeeFamilyMapper.deleteByEmployeeId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployeeArchiveList(List<Long> ids) {
        // 校验存在
        validateEmployeeArchiveExists(ids);

        // 删除主表
        employeeArchiveMapper.deleteBatchIds(ids);

        // 删除关联记录
        for (Long id : ids) {
            employeeWorkExperienceMapper.deleteByEmployeeId(id);
            employeeEducationMapper.deleteByEmployeeId(id);
            employeeFamilyMapper.deleteByEmployeeId(id);
        }
    }

    private void validateEmployeeArchiveExists(Long id) {
        if (employeeArchiveMapper.selectById(id) == null) {
            throw exception(EMPLOYEE_ARCHIVE_NOT_EXISTS);
        }
    }

    private void validateEmployeeArchiveExists(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<EmployeeDO> list = employeeArchiveMapper.selectBatchIds(ids);
        if (CollUtil.isEmpty(list) || list.size() != ids.size()) {
            throw exception(EMPLOYEE_ARCHIVE_NOT_EXISTS);
        }
    }

    @Override
    public EmployeeRespVO getEmployeeArchive(Long id) {
        EmployeeDO archive = employeeArchiveMapper.selectById(id);
        if (archive == null) {
            return null;
        }

        EmployeeRespVO respVO = BeanUtils.toBean(archive, EmployeeRespVO.class);

        // 获取部门名称
        if (archive.getDeptId() != null) {
            CommonResult<DeptRespDTO> dept = deptApi.getDept(archive.getDeptId());
            if (dept != null && dept.isSuccess() && dept.getData() != null) {
                respVO.setDeptName(dept.getData().getName());
            }
        }

        // 获取工作经历
        List<EmployeeWorkExperienceDO> workExperiences = employeeWorkExperienceMapper.selectListByEmployeeId(id);
        respVO.setWorkExperienceList(BeanUtils.toBean(workExperiences, EmployeeWorkExperienceVO.class));

        // 获取教育经历
        List<EmployeeEducationDO> educations = employeeEducationMapper.selectListByEmployeeId(id);
        respVO.setEducationList(BeanUtils.toBean(educations, EmployeeEducationVO.class));

        // 获取家属信息
        List<EmployeeFamilyDO> families = employeeFamilyMapper.selectListByEmployeeId(id);
        respVO.setFamilyList(BeanUtils.toBean(families, EmployeeFamilyVO.class));

        return respVO;
    }

    @Override
    public PageResult<EmployeeRespVO> getEmployeeArchivePage(EmployeePageReqVO pageReqVO) {
        PageResult<EmployeeDO> pageResult = employeeArchiveMapper.selectPage(pageReqVO);
        PageResult<EmployeeRespVO> respPageResult = BeanUtils.toBean(pageResult, EmployeeRespVO.class);

        // 批量获取部门名称
        List<Long> deptIds = respPageResult.getList().stream()
                .map(EmployeeRespVO::getDeptId)
                .filter(deptId -> deptId != null)
                .distinct()
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(deptIds)) {
            Map<Long, DeptRespDTO> deptMap = deptApi.getDeptMap(deptIds);
            respPageResult.getList().forEach(respVO -> {
                if (respVO.getDeptId() != null && deptMap.containsKey(respVO.getDeptId())) {
                    respVO.setDeptName(deptMap.get(respVO.getDeptId()).getName());
                }
            });
        }

        return respPageResult;
    }

    /**
     * 保存工作经历列表
     */
    private void saveWorkExperiences(Long employeeId, List<EmployeeWorkExperienceVO> workExperienceList) {
        if (CollUtil.isEmpty(workExperienceList)) {
            return;
        }
        List<EmployeeWorkExperienceDO> workExperiences = BeanUtils.toBean(workExperienceList, EmployeeWorkExperienceDO.class);
        workExperiences.forEach(item -> {
            item.setId(null);
            item.setEmployeeId(employeeId);
            employeeWorkExperienceMapper.insert(item);
        });
    }

    /**
     * 保存教育经历列表
     */
    private void saveEducations(Long employeeId, List<EmployeeEducationVO> educationList) {
        if (CollUtil.isEmpty(educationList)) {
            return;
        }
        List<EmployeeEducationDO> educations = BeanUtils.toBean(educationList, EmployeeEducationDO.class);
        educations.forEach(item -> {
            item.setId(null);
            item.setEmployeeId(employeeId);
            employeeEducationMapper.insert(item);
        });
    }

    /**
     * 保存家属信息列表
     */
    private void saveFamilies(Long employeeId, List<EmployeeFamilyVO> familyList) {
        if (CollUtil.isEmpty(familyList)) {
            return;
        }
        List<EmployeeFamilyDO> families = BeanUtils.toBean(familyList, EmployeeFamilyDO.class);
        families.forEach(item -> {
            item.setId(null);
            item.setEmployeeId(employeeId);
            employeeFamilyMapper.insert(item);
        });
    }

}

