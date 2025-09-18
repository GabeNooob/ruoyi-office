package cn.iocoder.yudao.module.oa.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用车申请单还车状态枚举
 *
 * @author 芋道源码
 */
@AllArgsConstructor
@Getter
public enum CarReturnStatusEnum {

    /**
     * 未还车
     */
    NOT_RETURNED(0, "未还车"),
    
    /**
     * 还车中
     */
    RETURNING(1, "还车中"),
    
    /**
     * 已还车
     */
    RETURNED(2, "已还车");

    /**
     * 状态值
     */
    private final Integer status;
    
    /**
     * 状态名称
     */
    private final String name;

    /**
     * 根据状态值获取枚举
     *
     * @param status 状态值
     * @return 枚举
     */
    public static CarReturnStatusEnum getByStatus(Integer status) {
        if (status == null) {
            return null;
        }
        for (CarReturnStatusEnum value : values()) {
            if (value.getStatus().equals(status)) {
                return value;
            }
        }
        return null;
    }
}
