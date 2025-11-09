package cn.iocoder.yudao.module.oa.dal.dataobject.file;

import lombok.*;

import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 企业云盘-文件权限 DO
 *
 * @author 芋道源码
 */
@TableName("oa_file_permission")
@KeySequence("oa_file_permission_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库,可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilePermissionDO extends BaseDO {

    /**
     * 权限ID
     */
    @TableId
    private Long id;
    
    /**
     * 文件ID
     */
    private Long fileId;
    
    /**
     * 分享类型(0人员 1组织)
     */
    private Integer shareType;
    
    /**
     * 人员或组织ID
     */
    private Long targetId;
    
    /**
     * 人员或组织名称
     */
    private String targetName;
    
    /**
     * 权限(0仅查看 1可管理)
     */
    private Integer permission;

}

