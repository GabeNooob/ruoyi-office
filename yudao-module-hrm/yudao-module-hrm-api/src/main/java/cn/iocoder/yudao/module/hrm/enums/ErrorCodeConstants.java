package cn.iocoder.yudao.module.hrm.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * HRM 错误码枚举类
 * <p>
 * HRM 系统，使用 1-050-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 员工档案 1-050-001-000 ==========
    ErrorCode EMPLOYEE_ARCHIVE_NOT_EXISTS = new ErrorCode(1_050_001_001, "员工档案不存在");

}

