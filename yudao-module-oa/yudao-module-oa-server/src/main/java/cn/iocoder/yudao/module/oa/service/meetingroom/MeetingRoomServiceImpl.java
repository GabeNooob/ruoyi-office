package cn.iocoder.yudao.module.oa.service.meetingroom;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;
import cn.iocoder.yudao.module.oa.controller.admin.meetingroom.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.meetingroom.MeetingRoomDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.oa.dal.mysql.meetingroom.MeetingRoomMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.oa.enums.ErrorCodeConstants.*;

/**
 * 会议室信息 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MeetingRoomServiceImpl implements MeetingRoomService {

    @Resource
    private MeetingRoomMapper meetingRoomMapper;

    @Override
    public Long createMeetingRoom(MeetingRoomSaveReqVO createReqVO) {
        // 转换并插入
        MeetingRoomDO meetingRoom = BeanUtils.toBean(createReqVO, MeetingRoomDO.class);
        // 处理设备列表（数组转逗号分隔字符串）
        if (CollUtil.isNotEmpty(createReqVO.getEquipment())) {
            meetingRoom.setEquipment(String.join(",", createReqVO.getEquipment()));
        }
        // 处理预定成员列表（数组转逗号分隔字符串）
        if (CollUtil.isNotEmpty(createReqVO.getBookingMembers())) {
            meetingRoom.setBookingMembers(
                createReqVO.getBookingMembers().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","))
            );
        }
        meetingRoomMapper.insert(meetingRoom);
        // 返回
        return meetingRoom.getId();
    }

    @Override
    public void updateMeetingRoom(MeetingRoomSaveReqVO updateReqVO) {
        // 校验存在
        validateMeetingRoomExists(updateReqVO.getId());
        // 转换并更新
        MeetingRoomDO updateObj = BeanUtils.toBean(updateReqVO, MeetingRoomDO.class);
        // 处理设备列表（数组转逗号分隔字符串）
        if (CollUtil.isNotEmpty(updateReqVO.getEquipment())) {
            updateObj.setEquipment(String.join(",", updateReqVO.getEquipment()));
        } else {
            updateObj.setEquipment(null);
        }
        // 处理预定成员列表（数组转逗号分隔字符串）
        if (CollUtil.isNotEmpty(updateReqVO.getBookingMembers())) {
            updateObj.setBookingMembers(
                updateReqVO.getBookingMembers().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","))
            );
        } else {
            updateObj.setBookingMembers(null);
        }
        meetingRoomMapper.updateById(updateObj);
    }

    @Override
    public void deleteMeetingRoom(Long id) {
        // 校验存在
        validateMeetingRoomExists(id);
        // 删除
        meetingRoomMapper.deleteById(id);
    }

    @Override
    public void deleteMeetingRoomListByIds(List<Long> ids) {
        // 校验存在
        validateMeetingRoomExists(ids);
        // 删除
        meetingRoomMapper.deleteByIds(ids);
    }

    private void validateMeetingRoomExists(List<Long> ids) {
        List<MeetingRoomDO> list = meetingRoomMapper.selectByIds(ids);
        if (CollUtil.isEmpty(list) || list.size() != ids.size()) {
            throw exception(MEETING_ROOM_NOT_EXISTS);
        }
    }

    private void validateMeetingRoomExists(Long id) {
        if (meetingRoomMapper.selectById(id) == null) {
            throw exception(MEETING_ROOM_NOT_EXISTS);
        }
    }

    @Override
    public MeetingRoomDO getMeetingRoom(Long id) {
        return meetingRoomMapper.selectById(id);
    }

    @Override
    public PageResult<MeetingRoomDO> getMeetingRoomPage(MeetingRoomPageReqVO pageReqVO) {
        return meetingRoomMapper.selectPage(pageReqVO);
    }

}

