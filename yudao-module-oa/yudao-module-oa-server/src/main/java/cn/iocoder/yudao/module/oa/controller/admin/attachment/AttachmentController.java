package cn.iocoder.yudao.module.oa.controller.admin.attachment;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.module.oa.controller.admin.attachment.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.attachment.AttachmentDO;
import cn.iocoder.yudao.module.oa.service.attachment.AttachmentService;

@Tag(name = "管理后台 - 通用附件信息")
@RestController
@RequestMapping("/oa/attachment")
@Validated
public class AttachmentController {

    @Resource
    private AttachmentService attachmentService;

    @PostMapping("/create")
    @Operation(summary = "创建附件信息")
    @PreAuthorize("@ss.hasPermission('oa:attachment:create')")
    public CommonResult<Long> createAttachment(@Valid @RequestBody AttachmentSaveReqVO createReqVO) {
        return success(attachmentService.createAttachment(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新附件信息")
    @PreAuthorize("@ss.hasPermission('oa:attachment:update')")
    public CommonResult<Boolean> updateAttachment(@Valid @RequestBody AttachmentSaveReqVO updateReqVO) {
        attachmentService.updateAttachment(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除附件信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:attachment:delete')")
    public CommonResult<Boolean> deleteAttachment(@RequestParam("id") Long id) {
        attachmentService.deleteAttachment(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得附件信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:attachment:query')")
    public CommonResult<AttachmentRespVO> getAttachment(@RequestParam("id") Long id) {
        AttachmentDO attachment = attachmentService.getAttachment(id);
        return success(BeanUtils.toBean(attachment, AttachmentRespVO.class));
    }

    @GetMapping("/list-by-business")
    @Operation(summary = "根据业务类型和业务ID获取附件列表")
    @Parameter(name = "businessType", description = "业务类型", required = true)
    @Parameter(name = "businessId", description = "业务ID", required = true)
    @PreAuthorize("@ss.hasPermission('oa:attachment:query')")
    public CommonResult<List<AttachmentRespVO>> getAttachmentListByBusiness(
            @RequestParam("businessType") String businessType,
            @RequestParam("businessId") Long businessId) {
        List<AttachmentDO> list = attachmentService.getAttachmentListByBusiness(businessType, businessId);
        return success(BeanUtils.toBean(list, AttachmentRespVO.class));
    }

}
