package cn.iocoder.yudao.module.oa.service.meetingroom;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.oa.controller.admin.meetingroom.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.meetingroom.MeetingRoomDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 会议室信息 Service 接口
 *
 * @author 芋道源码
 */
public interface MeetingRoomService {

    /**
     * 创建会议室信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMeetingRoom(@Valid MeetingRoomSaveReqVO createReqVO);

    /**
     * 更新会议室信息
     *
     * @param updateReqVO 更新信息
     */
    void updateMeetingRoom(@Valid MeetingRoomSaveReqVO updateReqVO);

    /**
     * 删除会议室信息
     *
     * @param id 编号
     */
    void deleteMeetingRoom(Long id);

    /**
     * 批量删除会议室信息
     *
     * @param ids 编号列表
     */
    void deleteMeetingRoomListByIds(List<Long> ids);

    /**
     * 获得会议室信息
     *
     * @param id 编号
     * @return 会议室信息
     */
    MeetingRoomDO getMeetingRoom(Long id);

    /**
     * 获得会议室信息分页
     *
     * @param pageReqVO 分页查询
     * @return 会议室信息分页
     */
    PageResult<MeetingRoomDO> getMeetingRoomPage(MeetingRoomPageReqVO pageReqVO);

}

