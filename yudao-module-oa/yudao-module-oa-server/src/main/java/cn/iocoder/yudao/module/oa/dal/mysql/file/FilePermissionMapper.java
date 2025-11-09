package cn.iocoder.yudao.module.oa.dal.mysql.file;

import java.util.*;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.oa.dal.dataobject.file.FilePermissionDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 企业云盘-文件权限 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface FilePermissionMapper extends BaseMapperX<FilePermissionDO> {

    default List<FilePermissionDO> selectListByFileId(Long fileId) {
        return selectList(FilePermissionDO::getFileId, fileId);
    }

    default void deleteByFileId(Long fileId) {
        delete(FilePermissionDO::getFileId, fileId);
    }

    default FilePermissionDO selectByFileIdAndTarget(Long fileId, Integer shareType, Long targetId) {
        return selectOne(new LambdaQueryWrapperX<FilePermissionDO>()
                .eq(FilePermissionDO::getFileId, fileId)
                .eq(FilePermissionDO::getShareType, shareType)
                .eq(FilePermissionDO::getTargetId, targetId));
    }

}

