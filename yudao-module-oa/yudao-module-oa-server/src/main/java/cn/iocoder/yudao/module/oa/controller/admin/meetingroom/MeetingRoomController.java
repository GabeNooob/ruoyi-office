package cn.iocoder.yudao.module.oa.controller.admin.meetingroom;

import cn.hutool.core.util.StrUtil;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.oa.controller.admin.meetingroom.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.meetingroom.MeetingRoomDO;
import cn.iocoder.yudao.module.oa.service.meetingroom.MeetingRoomService;

@Tag(name = "OA协同办公 - 会议室管理")
@RestController
@RequestMapping("/oa/meeting-room")
@Validated
public class MeetingRoomController {

    @Resource
    private MeetingRoomService meetingRoomService;

    @PostMapping("/create")
    @Operation(summary = "创建会议室信息")
    @PreAuthorize("@ss.hasPermission('oa:meeting-room:create')")
    public CommonResult<Long> createMeetingRoom(@Valid @RequestBody MeetingRoomSaveReqVO createReqVO) {
        return success(meetingRoomService.createMeetingRoom(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新会议室信息")
    @PreAuthorize("@ss.hasPermission('oa:meeting-room:update')")
    public CommonResult<Boolean> updateMeetingRoom(@Valid @RequestBody MeetingRoomSaveReqVO updateReqVO) {
        meetingRoomService.updateMeetingRoom(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除会议室信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:meeting-room:delete')")
    public CommonResult<Boolean> deleteMeetingRoom(@RequestParam("id") Long id) {
        meetingRoomService.deleteMeetingRoom(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除会议室信息")
    @PreAuthorize("@ss.hasPermission('oa:meeting-room:delete')")
    public CommonResult<Boolean> deleteMeetingRoomList(@RequestParam("ids") List<Long> ids) {
        meetingRoomService.deleteMeetingRoomListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得会议室信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:meeting-room:query')")
    public CommonResult<MeetingRoomRespVO> getMeetingRoom(@RequestParam("id") Long id) {
        MeetingRoomDO meetingRoom = meetingRoomService.getMeetingRoom(id);
        MeetingRoomRespVO respVO = BeanUtils.toBean(meetingRoom, MeetingRoomRespVO.class);
        // 处理设备列表（逗号分隔字符串转数组）
        if (StrUtil.isNotBlank(meetingRoom.getEquipment())) {
            respVO.setEquipment(Arrays.asList(meetingRoom.getEquipment().split(",")));
        }
        // 处理预定成员列表（逗号分隔字符串转数组）
        if (StrUtil.isNotBlank(meetingRoom.getBookingMembers())) {
            respVO.setBookingMembers(
                Arrays.stream(meetingRoom.getBookingMembers().split(","))
                    .map(Long::valueOf)
                    .collect(Collectors.toList())
            );
        }
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得会议室信息分页")
    @PreAuthorize("@ss.hasPermission('oa:meeting-room:query')")
    public CommonResult<PageResult<MeetingRoomRespVO>> getMeetingRoomPage(@Valid MeetingRoomPageReqVO pageReqVO) {
        PageResult<MeetingRoomDO> pageResult = meetingRoomService.getMeetingRoomPage(pageReqVO);
        PageResult<MeetingRoomRespVO> respPageResult = BeanUtils.toBean(pageResult, MeetingRoomRespVO.class);
        // 处理每个记录的设备列表和预定成员列表
        respPageResult.getList().forEach(respVO -> {
            MeetingRoomDO meetingRoom = pageResult.getList().stream()
                .filter(item -> item.getId().equals(respVO.getId()))
                .findFirst()
                .orElse(null);
            if (meetingRoom != null) {
                // 处理设备列表
                if (StrUtil.isNotBlank(meetingRoom.getEquipment())) {
                    respVO.setEquipment(Arrays.asList(meetingRoom.getEquipment().split(",")));
                }
                // 处理预定成员列表
                if (StrUtil.isNotBlank(meetingRoom.getBookingMembers())) {
                    respVO.setBookingMembers(
                        Arrays.stream(meetingRoom.getBookingMembers().split(","))
                            .map(Long::valueOf)
                            .collect(Collectors.toList())
                    );
                }
            }
        });
        return success(respPageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出会议室信息 Excel")
    @PreAuthorize("@ss.hasPermission('oa:meeting-room:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportMeetingRoomExcel(@Valid MeetingRoomPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MeetingRoomDO> list = meetingRoomService.getMeetingRoomPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "会议室信息.xls", "数据", MeetingRoomRespVO.class,
                        BeanUtils.toBean(list, MeetingRoomRespVO.class));
    }

}

