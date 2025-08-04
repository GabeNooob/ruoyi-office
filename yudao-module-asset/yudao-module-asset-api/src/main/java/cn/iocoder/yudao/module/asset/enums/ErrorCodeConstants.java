package cn.iocoder.yudao.module.asset.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * oa 错误码枚举类
 *
 * oa 系统，使用 1-101-000-000 段
 */
public interface ErrorCodeConstants {


    // ========== 资产类别 ==========
    ErrorCode CATEGORY_NOT_EXISTS = new ErrorCode(1_102_001_000, "资产类别不存在");
    ErrorCode CATEGORY_EXITS_CHILDREN = new ErrorCode(1_102_002_000, "存在存在子资产类别，无法删除");
    ErrorCode CATEGORY_PARENT_NOT_EXITS = new ErrorCode(1_102_003_000,"父级资产类别不存在");
    ErrorCode CATEGORY_PARENT_ERROR = new ErrorCode(1_102_004_000, "不能设置自己为父资产类别");
    ErrorCode CATEGORY_CATEGORY_NAME_DUPLICATE = new ErrorCode(1_102_005_000, "已经存在该类别名称的资产类别");
    ErrorCode CATEGORY_PARENT_IS_CHILD = new ErrorCode(1_102_006_000, "不能设置自己的子Category为父Category");

}
