package cn.iocoder.yudao.module.oa.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * oa 错误码枚举类
 *
 * oa 系统，使用 1-101-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== oa车辆管理 1-101-000-000 ============
    ErrorCode CAR_NOT_EXISTS = new ErrorCode(1_101_000_000, "车辆信息不存在");

    // ========== 用车申请单 TODO 补充编号 ==========
    ErrorCode CAR_APPLY_BILL_NOT_EXISTS = new ErrorCode(1_101_000_001, "用车申请单不存在");
    ErrorCode CAR_APPLY_SAVE_INFO_NOT_NULL = new ErrorCode(1_101_000_002, "保存信息不能为空");
}
