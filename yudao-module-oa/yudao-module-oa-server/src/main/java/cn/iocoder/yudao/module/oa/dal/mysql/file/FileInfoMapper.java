package cn.iocoder.yudao.module.oa.dal.mysql.file;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.oa.dal.dataobject.file.FileInfoDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.oa.controller.admin.file.vo.*;

/**
 * 企业云盘-文件信息 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface FileInfoMapper extends BaseMapperX<FileInfoDO> {

    default PageResult<FileInfoDO> selectPage(FileInfoPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FileInfoDO>()
                .eqIfPresent(FileInfoDO::getParentId, reqVO.getParentId())
                .eqIfPresent(FileInfoDO::getFileType, reqVO.getFileType())
                .likeIfPresent(FileInfoDO::getFileName, reqVO.getFileName())
                .eqIfPresent(FileInfoDO::getOwnerId, reqVO.getOwnerId())
                .eqIfPresent(FileInfoDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(FileInfoDO::getIsShared, reqVO.getIsShared())
                .eqIfPresent(FileInfoDO::getShareType, reqVO.getShareType())
                .betweenIfPresent(FileInfoDO::getCreateTime, reqVO.getCreateTime())
                .orderByAsc(FileInfoDO::getFileType) // 文件夹排前面（0=文件夹，1=文件）
                .orderByDesc(FileInfoDO::getUpdateTime) // 按更新时间倒序
                .orderByAsc(FileInfoDO::getSortOrder));
    }

    default List<FileInfoDO> selectListByParentId(Long parentId) {
        return selectList(new LambdaQueryWrapperX<FileInfoDO>()
                .eq(FileInfoDO::getParentId, parentId)
                .orderByAsc(FileInfoDO::getFileType) // 文件夹排前面（0=文件夹，1=文件）
                .orderByDesc(FileInfoDO::getUpdateTime) // 按更新时间倒序
                .orderByAsc(FileInfoDO::getSortOrder));
    }

    default List<FileInfoDO> selectListByParentId(Long parentId, Long ownerId) {
        return selectList(new LambdaQueryWrapperX<FileInfoDO>()
                .eq(FileInfoDO::getParentId, parentId)
                .eq(FileInfoDO::getOwnerId, ownerId)
                .orderByAsc(FileInfoDO::getFileType) // 文件夹排前面（0=文件夹，1=文件）
                .orderByDesc(FileInfoDO::getUpdateTime) // 按更新时间倒序
                .orderByAsc(FileInfoDO::getSortOrder));
    }

}

