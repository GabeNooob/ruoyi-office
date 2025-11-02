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

    // ========== 用车申请单 ==========
    ErrorCode CAR_APPLY_BILL_NOT_EXISTS = new ErrorCode(1_101_000_001, "用车申请单不存在");
    ErrorCode CAR_APPLY_SAVE_INFO_NOT_NULL = new ErrorCode(1_101_000_002, "保存信息不能为空");
    // ========== 还车申请单  ==========
    ErrorCode CAR_RETURN_BILL_NOT_EXISTS = new ErrorCode(1_101_000_003, "还车申请单不存在");
    ErrorCode CAR_APPLY_BILL_ALREADY_RETURNED = new ErrorCode(1_101_000_004, "用车申请单已还车，不能重复还车");
    ErrorCode CAR_TIME_CONFLICT = new ErrorCode(1_101_000_005, "车辆使用时间冲突，该时间段已有其他申请单");

    // ========== 印章管理 1-101-001-000 ============
    ErrorCode SEAL_NOT_EXISTS = new ErrorCode(1_101_001_000, "印章信息不存在");
    ErrorCode SEAL_NO_DUPLICATE = new ErrorCode(1_101_001_001, "印章编号已存在");
}
